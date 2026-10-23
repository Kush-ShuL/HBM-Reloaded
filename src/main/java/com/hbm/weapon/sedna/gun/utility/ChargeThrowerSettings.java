package com.hbm.weapon.sedna.gun.utility;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Actual XFactoryTool ServerConfig value, independent of other tool factories. */
public final class ChargeThrowerSettings {
 public static final ModConfigSpec SPEC;
 private static final ModConfigSpec.DoubleValue DAMAGE;
 static {var builder=new ModConfigSpec.Builder();DAMAGE=builder.defineInRange(java.util.List.of("damage","chargeThrower"),10D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
 public static Float damage(){return SPEC.isLoaded()?DAMAGE.get().floatValue():10F;}
 private ChargeThrowerSettings(){}
}
