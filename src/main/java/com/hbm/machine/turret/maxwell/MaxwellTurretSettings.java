package com.hbm.machine.turret.maxwell;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Actual XFactoryTurret27/40 RunningConfig key; no separate weapon damage formula or FE compatibility. */
public final class MaxwellTurretSettings {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.DoubleValue DAMAGE;
 static{var b=new ModConfigSpec.Builder();DAMAGE=b.defineInRange(java.util.List.of("damage","maxwell"),.25D,0,Float.MAX_VALUE);SPEC=b.build();}
 public static float damage(){return DAMAGE.get().floatValue();}
 private MaxwellTurretSettings(){}
}
