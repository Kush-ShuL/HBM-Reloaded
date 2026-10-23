package com.hbm.environment.impact.mixin;
import com.hbm.environment.impact.ImpactGeneration;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.*;
import org.spongepowered.asm.mixin.Mixin;
/** NeoForge removed Populate/Decorate events: one scoped native pass retains one decision per source category. */
@Mixin(value=ChunkGenerator.class,remap=false)
public abstract class ImpactGenerationMixin {
    @WrapMethod(method="applyBiomeDecoration")
    private void hbm$impactDecoration(WorldGenLevel region,ChunkAccess chunk,StructureManager structures,Operation<Void> original){Object prior=ImpactGeneration.begin(region);try{original.call(region,chunk,structures);ImpactGeneration.afterDecoration(region,chunk);}finally{ImpactGeneration.end(prior);}}
}
