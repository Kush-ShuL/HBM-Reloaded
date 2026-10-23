package com.hbm.environment.impact.mixin;
import com.hbm.environment.impact.ImpactGeneration;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Surface rule grass is replaced before decoration; flat worlds never enter this noise-surface method. */
@Mixin(value=NoiseBasedChunkGenerator.class,remap=false)
public abstract class ImpactSurfaceMixin {
    @Inject(method="buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V",at=@At("RETURN"))
    private void hbm$impactTop(WorldGenRegion region,StructureManager structures,RandomState random,ChunkAccess chunk,CallbackInfo ci){ImpactGeneration.afterSurface(region,chunk);}
}
