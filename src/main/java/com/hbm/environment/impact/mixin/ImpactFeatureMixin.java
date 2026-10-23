package com.hbm.environment.impact.mixin;
import com.hbm.environment.impact.ImpactGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=PlacedFeature.class,remap=false)
public abstract class ImpactFeatureMixin {
    @Inject(method="placeWithBiomeCheck",at=@At("HEAD"),cancellable=true)
    private void hbm$impactPlantPolicy(WorldGenLevel region,ChunkGenerator generator,RandomSource random,BlockPos origin,CallbackInfoReturnable<Boolean> cir){if(!ImpactGeneration.allowFeature(region,(PlacedFeature)(Object)this))cir.setReturnValue(false);}
}
