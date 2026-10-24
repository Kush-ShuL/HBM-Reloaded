package com.hbm.client.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Options for the existing client tooltip event, using the existing tooltip event and native item tags. */
public final class ClientTooltipConfig {
    public static final String FILE_NAME = "hbm-tooltips-client.toml";
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue ITEM_TAGS, CUSTOM_NUKE;

    static {
        var builder = new ModConfigSpec.Builder();
        builder.comment("高级物品提示；使用F3+H切换高级提示。").push("advanced");
        ITEM_TAGS = builder.comment("显示物品所属的原生物品标签ID。默认true；仅在高级提示开启且物品实际具有标签时显示，不改变合成或材料匹配。")
                .define("itemTagsEnabled", true);
        builder.pop();
        builder.comment("物品作为特殊装置原料时的用途说明。").push("materials");
        CUSTOM_NUKE = builder.comment("显示自定义核弹原料对阶段数值或倍率的贡献。默认true；普通提示也可显示，数值按单件原料计算，不改变核弹配方或威力。")
                .define("customNukeEnabled", true);
        builder.pop();
        SPEC = builder.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC, FILE_NAME);
    }

    public static boolean itemTagsEnabled() { return !SPEC.isLoaded() || ITEM_TAGS.get(); }

    public static boolean customNukeEnabled() { return !SPEC.isLoaded() || CUSTOM_NUKE.get(); }

    private ClientTooltipConfig() {}
}
