package com.hbm.weapon.sedna.gun.energy;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Reloadable original ServerConfig keys. */
public final class EnergySettings {
public static final ModConfigSpec SPEC;
private static final ModConfigSpec.DoubleValue GUN_TESLA_CANNON_DAMAGE_VALUE;
private static final ModConfigSpec.DoubleValue GUN_LASER_PISTOL_DAMAGE_VALUE;
private static final ModConfigSpec.DoubleValue GUN_PEW_PEW_DAMAGE_VALUE;
private static final ModConfigSpec.DoubleValue GUN_MORNING_GLORY_DAMAGE_VALUE;
private static final ModConfigSpec.DoubleValue GUN_LASER_RIFLE_DAMAGE_VALUE;
static {var b=new ModConfigSpec.Builder();
GUN_TESLA_CANNON_DAMAGE_VALUE=b.defineInRange(java.util.List.of("damage","teslaCannon"),35D,-Double.MAX_VALUE,Double.MAX_VALUE);
GUN_LASER_PISTOL_DAMAGE_VALUE=b.defineInRange(java.util.List.of("damage","laserPistol"),25D,-Double.MAX_VALUE,Double.MAX_VALUE);
GUN_PEW_PEW_DAMAGE_VALUE=b.defineInRange(java.util.List.of("damage","pewPew"),30D,-Double.MAX_VALUE,Double.MAX_VALUE);
GUN_MORNING_GLORY_DAMAGE_VALUE=b.defineInRange(java.util.List.of("damage","morningGlory"),30D,-Double.MAX_VALUE,Double.MAX_VALUE);
GUN_LASER_RIFLE_DAMAGE_VALUE=b.defineInRange(java.util.List.of("damage","laserRifle"),50D,-Double.MAX_VALUE,Double.MAX_VALUE);
SPEC=b.build();}
public static Float GUN_TESLA_CANNON_DAMAGE(){return SPEC.isLoaded()?GUN_TESLA_CANNON_DAMAGE_VALUE.get().floatValue():35F;}
public static Float GUN_LASER_PISTOL_DAMAGE(){return SPEC.isLoaded()?GUN_LASER_PISTOL_DAMAGE_VALUE.get().floatValue():25F;}
public static Float GUN_PEW_PEW_DAMAGE(){return SPEC.isLoaded()?GUN_PEW_PEW_DAMAGE_VALUE.get().floatValue():30F;}
public static Float GUN_MORNING_GLORY_DAMAGE(){return SPEC.isLoaded()?GUN_MORNING_GLORY_DAMAGE_VALUE.get().floatValue():30F;}
public static Float GUN_LASER_RIFLE_DAMAGE(){return SPEC.isLoaded()?GUN_LASER_RIFLE_DAMAGE_VALUE.get().floatValue():50F;}
}
