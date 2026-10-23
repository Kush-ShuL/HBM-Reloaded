package com.hbm.weapon.sedna.gun.fireext;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Exact original XFactoryTool runtime damage key; hit callbacks still only extinguish as in source. */
public final class FireExtSettings {
 public static final ModConfigSpec SPEC;private static final ModConfigSpec.DoubleValue DAMAGE;
 static {var builder=new ModConfigSpec.Builder();DAMAGE=builder.defineInRange(java.util.List.of("damage","extinguisher"),0D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
 public static Float damage(){return SPEC.isLoaded()?DAMAGE.get().floatValue():0F;}
 private FireExtSettings(){}
}
