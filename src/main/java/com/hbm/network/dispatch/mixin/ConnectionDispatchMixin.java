package com.hbm.network.dispatch.mixin;

import com.hbm.network.dispatch.DispatchConnection;
import com.hbm.network.dispatch.PacketDispatch;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Connection.class, remap = false)
public abstract class ConnectionDispatchMixin implements DispatchConnection {
    @Unique private final AtomicLong hbm$packetEpoch = new AtomicLong();

    @Override public long hbm$dispatchEpoch() { return hbm$packetEpoch.get(); }
    @Override public void hbm$invalidateDispatch() { hbm$packetEpoch.incrementAndGet(); }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V",
            at = @At("HEAD"), cancellable = true)
    private void hbm$sourceDispatch(Packet<?> packet, PacketSendListener listener, boolean flush, CallbackInfo ci) {
        var connection = (Connection) (Object) this;
        PacketDispatch.beforeNativeSend(connection, packet);
        if (PacketDispatch.intercept(connection, packet, listener, flush)) ci.cancel();
    }

    @Inject(method = "setupInboundProtocol", at = @At("HEAD"))
    private void hbm$inboundContext(ProtocolInfo<?> protocol, PacketListener listener, CallbackInfo ci) {
        hbm$invalidateDispatch();
    }

    @Inject(method = "setupOutboundProtocol", at = @At("HEAD"))
    private void hbm$outboundContext(ProtocolInfo<?> protocol, CallbackInfo ci) {
        hbm$invalidateDispatch();
    }

    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("HEAD"))
    private void hbm$closedContext(DisconnectionDetails reason, CallbackInfo ci) {
        hbm$invalidateDispatch();
    }
}
