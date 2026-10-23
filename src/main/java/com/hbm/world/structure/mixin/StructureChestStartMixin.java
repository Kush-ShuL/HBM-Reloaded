package com.hbm.world.structure.mixin;

import com.hbm.world.structure.StructureChestPairs;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;

/** All actual HBM piece families meet at this native structure placement boundary. */
@Mixin(value=StructureStart.class,remap=false)
public abstract class StructureChestStartMixin {
    @WrapMethod(method="placeInChunk")
    private void hbm$newStructureChests(WorldGenLevel level,StructureManager manager,ChunkGenerator generator,
            RandomSource random,BoundingBox clip,ChunkPos chunk,Operation<Void> original) {
        try(var scope=StructureChestPairs.begin(level,(StructureStart)(Object)this,clip)) {
            original.call(level,manager,generator,random,clip,chunk);
            if(scope!=null)scope.complete();
        }
    }
}
