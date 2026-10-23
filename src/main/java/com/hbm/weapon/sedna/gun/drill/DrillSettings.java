package com.hbm.weapon.sedna.gun.drill;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Source XFactoryDrill.initConfig accidentally names its drill wrapper GUN_DOUBLE_BARREL_DAMAGE; retain its literal label locally. */
public final class DrillSettings {
 public static final ModConfigSpec SPEC;private static final ModConfigSpec.DoubleValue DAMAGE;
 static{var builder=new ModConfigSpec.Builder();DAMAGE=builder.comment("动力钻的伤害参数，仅影响动力钻。").defineInRange(java.util.List.of("damage","poweredDrill"),10D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
 public static Float damage(){return SPEC.isLoaded()?DAMAGE.get().floatValue():10F;}private DrillSettings(){}
}
