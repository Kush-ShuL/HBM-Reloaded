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
    // 打开主菜单时，是否小概率换成本模组的随机标语；默认开启。
    private static final ModConfigSpec.BooleanValue CUSTOM_MAIN_MENU_SPLASHES;
    // HUD左上角是否显示满足完整规则的模式徽章；默认开启。
    private static final ModConfigSpec.BooleanValue MODE_BADGES;
    // 配方浏览器是否隐藏秘密物品及相关通用机器配方；默认开启。
    private static final ModConfigSpec.BooleanValue RECIPE_VIEWER_HIDE_SECRETS;

    // 是否显示准星所指方块和工具的实时观察提示；默认开启。
    private static final ModConfigSpec.BooleanValue LOOK_OVERLAY;
    // 是否额外显示所指方块的真实注册ID和状态属性；默认关闭，受观察提示总开关控制。
    private static final ModConfigSpec.BooleanValue BLOCK_STATE_DEBUG;

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
        CUSTOM_MAIN_MENU_SPLASHES = builder.comment(
                "打开主菜单时，小概率换成本模组的随机标语。默认true；关闭后不再进行新的替换。",
                "已替换的当前主菜单对象保留现有文字；不改变 Minecraft 自身隐藏标语的选项。")
                .define(java.util.List.of("mainMenu", "customSplashesEnabled"), true);
        MODE_BADGES = builder.comment(
                "在 HUD 左上角显示模式徽章。默认true；仅显示服务器确认满足完整模式规则的徽章。",
                "只控制徽章显示；不改变模式、配方或生物行为。按F1隐藏 HUD 时也隐藏徽章。")
                .define(java.util.List.of("hud", "modeBadgesEnabled"), true);
        RECIPE_VIEWER_HIDE_SECRETS = builder.comment(
                "在 JEI/REI 中隐藏秘密武器、秘密弹药、秘密材料，以及秘密配方池或含隐藏原料/产物的通用机器配方。默认true。",
                "更改后重载配方浏览器或重启客户端，以重新索引条目和配方。只控制展示，不改变实际合成或处理。",
                "泥板配方仍需单独显式允许；其他独立隐藏规则、蓝图册图标和创造栏规则不由此开关解除。")
                .define(java.util.List.of("recipeViewer", "hideSecretsEnabled"), true);
        LOOK_OVERLAY = builder.comment(
                "显示准星所指方块和工具的实时观察提示，包括 RBMK 诊断、机器、管道和结构工具。默认true。",
                "只控制观察提示；不改变机器状态、Jade 提示、计时通知、工具能力图标或世界中的结构预览。")
                .define(java.util.List.of("hud", "lookOverlayEnabled"), true);
        BLOCK_STATE_DEBUG = builder.comment(
                "在准星旁额外显示所指方块的注册ID、描述键和当前状态属性。默认false；仅在观察提示总开关开启时显示。",
                "显示当前原生方块状态，不生成旧版元数据编号，不改变方块或存档。")
                .define(java.util.List.of("hud", "blockStateDebugEnabled"), false);
        SPEC = builder.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC, FILE_NAME);
    }

    public static boolean nuclearHudShakeEnabled() { return !SPEC.isLoaded() || NUCLEAR_HUD_SHAKE.get(); }

    public static boolean nuclearHudFlashEnabled() { return !SPEC.isLoaded() || NUCLEAR_HUD_FLASH.get(); }

    public static boolean coolingTowerSteamParticlesEnabled() { return !SPEC.isLoaded() || COOLING_TOWER_STEAM_PARTICLES.get(); }

    public static int solarBoilerHeliostatBeamLimit() { return SPEC.isLoaded() ? SOLAR_BOILER_MAX_HELIOSTAT_BEAMS.get() : 250; }

    public static boolean mainMenuCustomSplashesEnabled() { return !SPEC.isLoaded() || CUSTOM_MAIN_MENU_SPLASHES.get(); }

    public static boolean modeBadgesEnabled() { return !SPEC.isLoaded() || MODE_BADGES.get(); }

    public static boolean recipeViewerHideSecretsEnabled() { return !SPEC.isLoaded() || RECIPE_VIEWER_HIDE_SECRETS.get(); }

    public static boolean lookOverlayEnabled() { return !SPEC.isLoaded() || LOOK_OVERLAY.get(); }

    public static boolean blockStateDebugEnabled() { return SPEC.isLoaded() && BLOCK_STATE_DEBUG.get(); }

    private ClientEffectsConfig() {}
}
