package com.hbm.armor.ncrpa;
import net.neoforged.neoforge.common.ModConfigSpec;
/** The four real NCR-specific XFactoryPA/ServerConfig wrappers. */
public final class NcrSettings {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.DoubleValue SWINGS,SWEEP,LARGE_MULT,RANGED;
 static{var b=new ModConfigSpec.Builder();SWINGS=b.defineInRange(java.util.List.of("damage","swing"),15D,-Double.MAX_VALUE,Double.MAX_VALUE);SWEEP=b.defineInRange(java.util.List.of("damage","sweep"),35D,-Double.MAX_VALUE,Double.MAX_VALUE);LARGE_MULT=b.defineInRange(java.util.List.of("damage","largeTargetMultiplier"),2.5D,-Double.MAX_VALUE,Double.MAX_VALUE);RANGED=b.defineInRange(java.util.List.of("damage","ranged"),25D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=b.build();}
 private NcrSettings(){}
}
