package com.hbm.network.dispatch.mixin;

import com.hbm.network.dispatch.PacketDispatch;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** A native broadcast is one source dispatch task, including a broadcast with no recipients. */
@Mixin(value=PlayerList.class,remap=false)
public abstract class PlayerListDispatchMixin {
    @Shadow @Final private MinecraftServer server;

    @WrapMethod(method="broadcastAll(Lnet/minecraft/network/protocol/Packet;)V")
    private void hbm$broadcastAll(Packet<?> packet,Operation<Void> original) {
        try(var scope=PacketDispatch.beginGroup(server,packet)) {
            original.call(packet);
            scope.commit();
        }
    }

    @WrapMethod(method="broadcastAll(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/resources/ResourceKey;)V")
    private void hbm$broadcastDimension(Packet<?> packet,ResourceKey<Level> dimension,Operation<Void> original) {
        try(var scope=PacketDispatch.beginGroup(server,packet)) {
            original.call(packet,dimension);
            scope.commit();
        }
    }

    @WrapMethod(method="broadcast(Lnet/minecraft/world/entity/player/Player;DDDDLnet/minecraft/resources/ResourceKey;Lnet/minecraft/network/protocol/Packet;)V")
    private void hbm$broadcastNear(Player excluded,double x,double y,double z,double radius,
            ResourceKey<Level> dimension,Packet<?> packet,Operation<Void> original) {
        try(var scope=PacketDispatch.beginGroup(server,packet)) {
            original.call(excluded,x,y,z,radius,dimension,packet);
            scope.commit();
        }
    }
}
