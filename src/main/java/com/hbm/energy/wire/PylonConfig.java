package com.hbm.energy.wire;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PylonConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue RENDER_CABLE_HANG;
    public static final ModConfigSpec.IntValue INFO_POSITION,INFO_OFFSET_HORIZONTAL,INFO_OFFSET_VERTICAL;
    static {
        var builder=new ModConfigSpec.Builder();
        RENDER_CABLE_HANG=builder.comment("绘制自然下垂的电线。").define(java.util.List.of("wires","saggingEnabled"),true);
        INFO_POSITION=builder.comment("信息栏位置：0 左上，1 右上，2 准星右下，其他值为准星左下。").defineInRange(java.util.List.of("hud","infoBox","position"),0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        INFO_OFFSET_HORIZONTAL=builder.defineInRange(java.util.List.of("hud","infoBox","horizontalOffset"),0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        INFO_OFFSET_VERTICAL=builder.defineInRange(java.util.List.of("hud","infoBox","verticalOffset"),0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        SPEC=builder.build();
    }
    private PylonConfig() {}
}
