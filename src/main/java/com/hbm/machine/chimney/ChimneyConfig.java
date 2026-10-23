package com.hbm.machine.chimney;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Original shared MobConfig's two smokestack controls, retained as a small native config until the mob group unifies them. */
public final class ChimneyConfig {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.BooleanValue RAMPANT;public static final ModConfigSpec.DoubleValue OVERRIDE;
 static{var builder=new ModConfigSpec.Builder();builder.push("mobPressure");RAMPANT=builder.define(java.util.List.of("rampantModeEnabled"),false);OVERRIDE=builder.defineInRange(java.util.List.of("smokestackPollutionMultiplier"),.4,0,Double.MAX_VALUE);builder.pop();SPEC=builder.build();}
 public static double pollutionMod(boolean industrial){return RAMPANT.get()?(industrial?OVERRIDE.get()/2:OVERRIDE.get()):(industrial?.1:.25);}private ChimneyConfig(){}
}
