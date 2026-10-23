package com.hbm.armor.rpa;
import net.neoforged.neoforge.common.ModConfigSpec;
/** The three actual XFactoryPA RPA ConfigWrappers, not new guessed armor tuning values. */
public final class RpaSettings {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.DoubleValue SWINGS,SLAP,LARGE_MULT;
 static{var b=new ModConfigSpec.Builder();SWINGS=b.defineInRange(java.util.List.of("damage","swing"),15D,-Double.MAX_VALUE,Double.MAX_VALUE);SLAP=b.defineInRange(java.util.List.of("damage","slap"),35D,-Double.MAX_VALUE,Double.MAX_VALUE);LARGE_MULT=b.defineInRange(java.util.List.of("damage","largeTargetMultiplier"),2.5D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=b.build();}private RpaSettings(){}
}
