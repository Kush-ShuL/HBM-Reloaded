package com.hbm.item.toolability;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Source ClientConfig's two exact integer crosshair indicator offsets. */
public final class CobaltToolClientConfig {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue HUD_X=BUILDER.defineInRange(java.util.List.of("hud","horizontalOffset"),0,Integer.MIN_VALUE,Integer.MAX_VALUE),HUD_Y=BUILDER.defineInRange(java.util.List.of("hud","verticalOffset"),0,Integer.MIN_VALUE,Integer.MAX_VALUE);
    public static final ModConfigSpec SPEC=BUILDER.build();
    private CobaltToolClientConfig(){}
}
