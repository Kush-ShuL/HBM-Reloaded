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
        ENABLED = builder.comment("启用污染排放、扩散及污染环境影响。")
                .define("enabled", true);
        EMISSION_MULTIPLIER = builder.comment("污染倍率；猖獗模式会将值 1 提升为 3，并相应调整煤烟雾阈值。")
                .defineInRange("emission_multiplier", 1D, 0D, Double.MAX_VALUE);
        BUFF_MOB_THRESHOLD = builder.comment("煤烟污染超过此值时，强化在该区域生成的怪物。")
                .defineInRange("mob_buff_threshold", 15D, 0D, Double.MAX_VALUE);
        LEAD_FROM_BLOCKS = builder.comment("挖掘重金属污染区域的方块时可能导致铅中毒。")
                .define("lead_from_blocks", true);
        LEAD_POISONING = builder.comment("身处重金属污染区域时可能导致铅中毒。")
                .define("lead_poisoning", true);
        SOOT_FOG = builder.comment("煤烟污染超过阈值时显示污染雾。")
                .define("soot_fog", true);
        POISON = builder.comment("身处毒性污染区域时施加中毒或凋零效果。")
                .define("poison", true);
        SOOT_FOG_THRESHOLD = builder.comment("开始显示煤烟污染雾的污染值；猖獗模式还会乘以有效污染倍率。")
                .defineInRange("soot_fog_threshold", 35D, 0D, Double.MAX_VALUE);
        SOOT_FOG_DIVISOR = builder.comment("煤烟污染雾浓度的除数，值越大雾越淡。")
                .defineInRange("soot_fog_divisor", 120D, 1E-9D, Double.MAX_VALUE);
        builder.pop();
        SPEC = builder.build();
    }

    private PollutionConfig() {}

    public static double emissionMultiplier(){double configured=EMISSION_MULTIPLIER.get();return rampant()&&configured==1D?3D:configured;}
    public static double sootFogThreshold(){return SOOT_FOG_THRESHOLD.get()*(rampant()?emissionMultiplier():1D);}
    private static boolean rampant(){return com.hbm.machine.chimney.ChimneyConfig.RAMPANT.get();}
}
