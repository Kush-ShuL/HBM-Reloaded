package com.hbm.client.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** 客户端画面效果的本地设置。 */
public final class ClientEffectsConfig {
    public static final String FILE_NAME = "hbm-effects-client.toml";
    public static final ModConfigSpec SPEC;
    // 核爆冲击波抵达后，是否平移热栏及其后绘制的 HUD；默认开启。
    private static final ModConfigSpec.BooleanValue NUCLEAR_HUD_SHAKE;
    // 核爆开始后的五秒内，是否绘制全屏白色渐淡闪光；默认开启。
    private static final ModConfigSpec.BooleanValue NUCLEAR_HUD_FLASH;
    // 小型和大型冷却塔工作时，是否生成蒸汽云粒子；默认开启。
    private static final ModConfigSpec.BooleanValue COOLING_TOWER_STEAM_PARTICLES;
    // 每次绘制单台太阳能锅炉时，最多显示的定日镜光束数量；默认250。
    private static final ModConfigSpec.IntValue SOLAR_BOILER_MAX_HELIOSTAT_BEAMS;

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
        COOLING_TOWER_STEAM_PARTICLES = builder.comment(
                "冷却塔工作时生成蒸汽云粒子。默认true；同时控制小型和大型冷却塔。",
                "只控制新生成的蒸汽云粒子；不改变冷凝效率、流体容量或装置状态。")
                .define(java.util.List.of("coolingTower", "steamParticlesEnabled"), true);
        SOLAR_BOILER_MAX_HELIOSTAT_BEAMS = builder.comment(
                "每次绘制单台太阳能锅炉时，最多显示的定日镜光束数量。默认250；0或负数不绘制光束；范围-2147483648至2147483647。",
                "光束仍需开启精美画质。只限制可见光束，不改变定日镜数量、对准目标、太阳能热量或锅炉处理。")
                .defineInRange(java.util.List.of("solarBoiler", "maxHeliostatBeams"), 250, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SPEC = builder.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC, FILE_NAME);
    }

    public static boolean nuclearHudShakeEnabled() { return !SPEC.isLoaded() || NUCLEAR_HUD_SHAKE.get(); }

    public static boolean nuclearHudFlashEnabled() { return !SPEC.isLoaded() || NUCLEAR_HUD_FLASH.get(); }

    public static boolean coolingTowerSteamParticlesEnabled() { return !SPEC.isLoaded() || COOLING_TOWER_STEAM_PARTICLES.get(); }

    public static int solarBoilerHeliostatBeamLimit() { return SPEC.isLoaded() ? SOLAR_BOILER_MAX_HELIOSTAT_BEAMS.get() : 250; }

    private ClientEffectsConfig() {}
}
