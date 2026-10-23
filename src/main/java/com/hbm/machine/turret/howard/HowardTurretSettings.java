package com.hbm.machine.turret.howard;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Source XFactoryTurret damage wrappers and WeaponConfig7.03_ciwsAccuracy (not a forced0..100 clamp). */
public final class HowardTurretSettings {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.DoubleValue HOWARD_DAMAGE,METHUSALEM_DAMAGE;public static final ModConfigSpec.IntValue CIWS_ACCURACY;
 static{var b=new ModConfigSpec.Builder();HOWARD_DAMAGE=b.defineInRange(java.util.List.of("damage","howard"),2D,0,Float.MAX_VALUE);METHUSALEM_DAMAGE=b.defineInRange(java.util.List.of("damage","methusalem"),2D,0,Float.MAX_VALUE);CIWS_ACCURACY=b.defineInRange(java.util.List.of("targeting","ciwsAccuracy"),50,Integer.MIN_VALUE,Integer.MAX_VALUE);SPEC=b.build();}
 public static float damage(boolean damaged){return(damaged?METHUSALEM_DAMAGE:HOWARD_DAMAGE).get().floatValue();}private HowardTurretSettings(){}
}
