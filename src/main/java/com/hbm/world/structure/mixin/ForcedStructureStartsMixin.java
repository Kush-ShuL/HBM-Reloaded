package com.hbm.world.structure.mixin;

import com.hbm.world.structure.NaturalStructures;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Open the native start phase only for explicitly forced source structures.
 * The generator's existing per-entry hook still rejects every unrelated start. */
@Mixin(value=ChunkStatusTasks.class,remap=false)
public abstract class ForcedStructureStartsMixin {
    @WrapOperation(method="generateStructureStarts",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/WorldOptions;generateStructures()Z"))
    private static boolean hbm$forcedSourceStarts(WorldOptions options,Operation<Boolean> original,WorldGenContext context,ChunkStep step,StaticCache2D<GenerationChunkHolder> cache,ChunkAccess chunk){
        return original.call(options)||context.level().dimension()==Level.OVERWORLD&&NaturalStructures.forcesStructures();
    }
}
