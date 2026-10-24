package com.hbm.network.dispatch.mixin;

import com.hbm.network.dispatch.AttachmentFanoutScope;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentSync;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.payload.SyncAttachmentsPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Initial mixed attachment packets and native no-recipient serialization short-circuits stay untouched. */
@Mixin(value=AttachmentSync.class,remap=false)
public abstract class AttachmentDispatchMixin {
    @WrapMethod(method="syncUpdate")
    private static <T> void hbm$singleUpdate(AttachmentHolder holder,AttachmentType<T> type,
            List<ServerPlayer> players,Operation<Void> original) {
        try(var scope=AttachmentFanoutScope.begin(holder)) {
            original.call(holder,type,players);
            scope.commit();
        }
    }

    @WrapOperation(method="syncUpdate",at=@At(value="INVOKE",
        target="Lnet/neoforged/neoforge/network/payload/SyncAttachmentsPayload;toVanillaClientbound()Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;",remap=false))
    private static ClientboundCustomPayloadPacket hbm$constructedUpdate(SyncAttachmentsPayload payload,
            Operation<ClientboundCustomPayloadPacket> original) {
        var packet=original.call(payload);
        AttachmentFanoutScope.packetBuilt(packet);
        return packet;
    }
}
