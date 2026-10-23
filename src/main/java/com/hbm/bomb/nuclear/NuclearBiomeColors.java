package com.hbm.bomb.nuclear;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

/** Original seed-2345 plant noise and its three crater grass palettes. */
public final class NuclearBiomeColors {
 public static final EnumProxy<GrassColorModifier> CRATER=new EnumProxy<>(GrassColorModifier.class,"hbm:crater",(GrassColorModifier.ColorModifier)(x,z,color)->color(x,z,0x606060,0x505050));
 public static final EnumProxy<GrassColorModifier> INNER=new EnumProxy<>(GrassColorModifier.class,"hbm:crater_inner",(GrassColorModifier.ColorModifier)(x,z,color)->color(x,z,0x404040,0x303030));
 public static final EnumProxy<GrassColorModifier> OUTER=new EnumProxy<>(GrassColorModifier.class,"hbm:crater_outer",(GrassColorModifier.ColorModifier)(x,z,color)->color(x,z,0x776F59,0x6F6752));
 private static int color(double x,double z,int low,int high){return Biome.BIOME_INFO_NOISE.getValue(x*.225D,z*.225D,false)<-.1D?low:high;}
 private NuclearBiomeColors(){}
}
