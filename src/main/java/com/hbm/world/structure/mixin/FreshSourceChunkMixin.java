package com.hbm.world.structure.mixin;

import com.hbm.world.structure.SourceDecorationOwnership;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Carries only the native disk lookup result until that same load creates its EMPTY chunk. */
@Mixin(value=ChunkMap.class,remap=false)
public abstract class FreshSourceChunkMixin {
    @Shadow @Final private ServerLevel level;
    @Unique private final Set<Long> hbm$disklessSourceLoads=ConcurrentHashMap.newKeySet();

    @WrapOperation(method="scheduleChunkLoad",at=@At(value="INVOKE",target="Lnet/minecraft/server/level/ChunkMap;readChunk(Lnet/minecraft/world/level/ChunkPos;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Optional<CompoundTag>> hbm$recordRawDiskResult(ChunkMap owner,ChunkPos position,Operation<CompletableFuture<Optional<CompoundTag>>> original){
        return original.call(owner,position).thenApply(raw->{
            if(raw.isEmpty())hbm$disklessSourceLoads.add(position.toLong());
            else hbm$disklessSourceLoads.remove(position.toLong());
            return raw;
        });
    }

    @Inject(method="createEmptyChunk",at=@At("RETURN"))
    private void hbm$admitOnlyDisklessEmpty(ChunkPos position,CallbackInfoReturnable<ChunkAccess> result){
        if(hbm$disklessSourceLoads.remove(position.toLong())&&level.dimension().equals(Level.OVERWORLD))
            SourceDecorationOwnership.admitNew(result.getReturnValue());
    }

    @Inject(method="handleChunkLoadFailure",at=@At("HEAD"))
    private void hbm$excludeFailedRead(Throwable error,ChunkPos position,CallbackInfoReturnable<ChunkAccess> result){
        hbm$disklessSourceLoads.remove(position.toLong());
    }

    @WrapMethod(method="scheduleChunkLoad")
    private CompletableFuture<ChunkAccess> hbm$releaseLookupRecord(ChunkPos position,Operation<CompletableFuture<ChunkAccess>> original){
        return original.call(position).whenComplete((chunk,error)->hbm$disklessSourceLoads.remove(position.toLong()));
    }
}
