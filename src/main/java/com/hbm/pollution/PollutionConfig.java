package com.hbm.pollution;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PollutionConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.DoubleValue EMISSION_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue BUFF_MOB_THRESHOLD;
    public static final ModConfigSpec.BooleanValue LEAD_FROM_BLOCKS,LEAD_POISONING,SOOT_FOG,POISON;
    public static final ModConfigSpec.DoubleValue SOOT_FOG_THRESHOLD,SOOT_FOG_DIVISOR;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("pollution");
        ENABLED = builder.define("POL_00_enablePollution", true);
        EMISSION_MULTIPLIER = builder.comment("污染倍率；猖獗模式会将值 1 提升为 3，并相应调整煤烟雾阈值。")
                .defineInRange("pollutionMult", 1D, 0D, Double.MAX_VALUE);
        BUFF_MOB_THRESHOLD = builder.defineInRange("POL_buffMobThreshold", 15D, 0D, Double.MAX_VALUE);
        LEAD_FROM_BLOCKS=builder.define("POL_01_enableLeadFromBlocks",true);
        LEAD_POISONING=builder.define("POL_02_enableLeadPoisoning",true);
        SOOT_FOG=builder.define("POL_03_enableSootFog",true);
        POISON=builder.define("POL_04_enablePoison",true);
        SOOT_FOG_THRESHOLD=builder.defineInRange("POL_06_sootFogThreshold",35D,0D,Double.MAX_VALUE);
        SOOT_FOG_DIVISOR=builder.defineInRange("POL_07_sootFogDivisor",120D,1E-9D,Double.MAX_VALUE);
        builder.pop();
        SPEC = builder.build();
    }

    private PollutionConfig() {}

    public static double emissionMultiplier(){double configured=EMISSION_MULTIPLIER.get();return rampant()&&configured==1D?3D:configured;}
    public static double sootFogThreshold(){return SOOT_FOG_THRESHOLD.get()*(rampant()?emissionMultiplier():1D);}
    private static boolean rampant(){return com.hbm.machine.chimney.ChimneyConfig.RAMPANT.get();}
}
