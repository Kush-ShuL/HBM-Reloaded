package com.hbm.weapon.sedna.gun.mixin;
import com.hbm.weapon.sedna.gun.GunHands;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=ServerGamePacketListenerImpl.class,remap=false)
public abstract class GunCreativeOffhandMixin {
    @Shadow public ServerPlayer player;
    @Inject(method="handleSetCreativeModeSlot",at=@At(value="INVOKE",target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",shift=At.Shift.AFTER),cancellable=true)
    private void hbm$rejectCreativeOffhand(ServerboundSetCreativeModeSlotPacket packet,CallbackInfo callback){if(packet.slotNum()==45&&GunHands.forbiddenOffhand(packet.itemStack())){player.inventoryMenu.sendAllDataToRemote();callback.cancel();}}
}
