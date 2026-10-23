package com.hbm.bomb.nuclear;

import net.minecraft.world.entity.LivingEntity;

/** EntityEffectHandler's crater-biome radiation, independent of the mutable chunk radiation grid. */
public final class NuclearAmbientRadiation {
 private NuclearAmbientRadiation(){}
 public static float rate(LivingEntity entity){
  var biome=entity.level().getBiome(entity.blockPosition());float radiation=0;
  if(biome.is(NuclearCraterBiomes.OUTER))radiation=NuclearConfig.CRATER_OUTER_RADIATION.get().floatValue();
  if(biome.is(NuclearCraterBiomes.CRATER))radiation=NuclearConfig.CRATER_RADIATION.get().floatValue();
  if(biome.is(NuclearCraterBiomes.INNER))radiation=NuclearConfig.CRATER_INNER_RADIATION.get().floatValue();
  if(entity.isInWaterRainOrBubble())radiation*=NuclearConfig.CRATER_WATER_MULTIPLIER.get().floatValue();
  return radiation;
 }
}
