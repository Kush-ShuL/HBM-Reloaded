package com.hbm.world.structure.mixin;

import com.hbm.world.structure.StructureChestPairs;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value=WorldGenRegion.class,remap=false)
public abstract class StructureChestRegionWriteMixin {
    @WrapOperation(method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at=@At(value="INVOKE",target="Lnet/minecraft/world/level/chunk/ChunkAccess;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState hbm$captureNewChest(ChunkAccess chunk,BlockPos pos,BlockState next,boolean moving,Operation<BlockState> original) {
        var previous=original.call(chunk,pos,next,moving);
        if(StructureChestPairs.trackedWrite(previous,next))StructureChestPairs.wrote(this,pos,previous,next);
        return previous;
    }
}
