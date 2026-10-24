package com.hbm.client.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** 核爆客户端画面效果的本地设置。 */
public final class ClientEffectsConfig {
    public static final String FILE_NAME = "hbm-effects-client.toml";
    public static final ModConfigSpec SPEC;
    // 核爆冲击波抵达后，是否平移热栏及其后绘制的 HUD；默认开启。
    private static final ModConfigSpec.BooleanValue NUCLEAR_HUD_SHAKE;
    // 核爆开始后的五秒内，是否绘制全屏白色渐淡闪光；默认开启。
    private static final ModConfigSpec.BooleanValue NUCLEAR_HUD_FLASH;

    static {
        var builder = new ModConfigSpec.Builder();
        NUCLEAR_HUD_SHAKE = builder.comment(
                "核爆冲击波抵达后，使热栏及其后绘制的 HUD 震动1.5秒。默认true；关闭后这些 HUD 不再平移。",
                "只控制 HUD 位移；受伤镜头反馈、核爆闪光、声音和实际伤害均按原有行为执行。")
                .define(java.util.List.of("nuclear", "hudShakeEnabled"), true);
        NUCLEAR_HUD_FLASH = builder.comment(
                "显示核爆开始后持续5秒逐渐淡出的全屏白色闪光。默认true；关闭后不绘制该 HUD 闪光。",
                "只控制准星绘制阶段的 HUD 白光；世界闪光、粒子、声音、受伤镜头和 HUD 震动按各自现有行为执行。")
                .define(java.util.List.of("nuclear", "hudFlashEnabled"), true);
        SPEC = builder.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC, FILE_NAME);
    }

    public static boolean nuclearHudShakeEnabled() { return !SPEC.isLoaded() || NUCLEAR_HUD_SHAKE.get(); }

    public static boolean nuclearHudFlashEnabled() { return !SPEC.isLoaded() || NUCLEAR_HUD_FLASH.get(); }

    private ClientEffectsConfig() {}
}
