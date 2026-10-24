package com.hbm.network.dispatch.mixin;

import com.hbm.network.dispatch.PacketDispatch;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelHeightAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Retains the holder's native recipient list and packet while grouping its existing sends. */
@Mixin(value=ChunkHolder.class,remap=false)
public abstract class ChunkHolderDispatchMixin {
    @Shadow @Final private LevelHeightAccessor levelHeightAccessor;

    @WrapMethod(method="broadcast(Ljava/util/List;Lnet/minecraft/network/protocol/Packet;)V")
    private void hbm$broadcast(List<ServerPlayer> players,Packet<?> packet,Operation<Void> original) {
        if(!(levelHeightAccessor instanceof ServerLevel level)) {
            original.call(players,packet);
            return;
        }
        try(var scope=PacketDispatch.beginGroup(level.getServer(),packet)) {
            original.call(players,packet);
            scope.commit();
        }
    }
}
