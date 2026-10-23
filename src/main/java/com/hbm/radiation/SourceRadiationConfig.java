package com.hbm.radiation;

import com.hbm.exposure.ExposureSettings;
import com.hbm.exposure.HbmExposure;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/** Configures the one existing chunk-radiation and exposure backend. */
public final class SourceRadiationConfig {
    public static final String FILE_NAME = "hbm-radiation-server.toml";
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue CHUNK_ENABLED, TERRAIN_ENABLED, EXPOSURE_ENABLED;
    private static final ModConfigSpec.IntValue FOG_THRESHOLD, FOG_CHANCE;
    private static final ModConfigSpec.DoubleValue NETHER_RATE;
    private static final ModConfigSpec.EnumValue<RadiationAlgorithm> ALGORITHM;
    private static boolean registered;

    static {
        var builder = new ModConfigSpec.Builder();
        builder.comment("区块辐射；仅控制区块场的读取、写入及扩散，不影响独立武器或方块的直接伤害。").push("chunk");
        CHUNK_ENABLED = builder.comment("启用区块辐射。默认true；关闭时暂停计算，保留已保存的辐射和地形处理进度。")
                .define("enabled", true);
        ALGORITHM = builder.comment("扩散算法。默认SIMPLE；SIMPLE为整柱扩散，PRISM为16×16×16格分区、六向扩散与方块屏蔽。修改后需重启世界。",
                        "PRISM不执行辐射雾及死亡地形转换，已排队的地形进度保留并暂停。",
                        "已有非零辐射场时不能直接改用另一算法；不会自动转换或丢弃数据。若切换被拒绝，请改回提示中的算法并重启。")
                .worldRestart().defineEnum("algorithm", RadiationAlgorithm.SIMPLE);
        builder.pop();
        builder.comment("环境地形变化；只影响尚未完成的死亡草地、枯叶等辐射转换。").push("terrain");
        TERRAIN_ENABLED = builder.comment("启用辐射地形转换。默认true；关闭不撤销已有方块变化，也不丢弃已排队进度。")
                .define("enabled", true);
        builder.pop();
        builder.comment("区块辐射雾；在辐射扩散写入时判断，不修改污染烟雾或核爆特效。").push("fog");
        FOG_THRESHOLD = builder.comment("产生辐射雾所需的辐射量，必须严格大于此值。默认100；单位RAD；范围0至2147483647。")
                .defineInRange("thresholdRad", 100, 0, Integer.MAX_VALUE);
        FOG_CHANCE = builder.comment("每次符合条件时产生辐射雾的概率分母，概率为1/n。默认20；范围1至2147483647；1表示必定尝试。")
                .defineInRange("chanceDenominator", 20, 1, Integer.MAX_VALUE);
        builder.pop();
        builder.comment("生物辐射剂量；与石棉、煤尘和迪伽马分别控制。").push("exposure");
        EXPOSURE_ENABLED = builder.comment("启用体内辐射剂量及其症状。默认true；关闭时保留已保存剂量，不改变石棉、煤尘或迪伽马设置。")
                .define("enabled", true);
        NETHER_RATE = builder.comment("高温维度的最低环境辐射率。默认0.1；单位RAD/秒；范围0至3.4028234663852886E38；0禁用该最低值。仍受现有免疫、防护及出生保护规则约束。")
                .defineInRange("netherRateRadPerSecond", 0.1D, 0D, (double) Float.MAX_VALUE);
        builder.pop();
        SPEC = builder.build();
    }

    public static void register(IEventBus bus, ModContainer container) {
        if (registered) return;
        registered = true;
        RadiationFogEffects.register(bus);
        container.registerConfig(ModConfig.Type.SERVER, SPEC, FILE_NAME);
        bus.addListener(SourceRadiationConfig::loading);
        bus.addListener(SourceRadiationConfig::reloading);
    }

    private static void loading(ModConfigEvent.Loading event) { configure(event); }
    private static void reloading(ModConfigEvent.Reloading event) { configure(event); }

    private static void configure(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC || event.getConfig().getType() != ModConfig.Type.SERVER) return;
        // Capture only values actually loaded by this spec, never unrelated client defaults.
        var values = new Values(ALGORITHM.get(), CHUNK_ENABLED.get(), TERRAIN_ENABLED.get(), FOG_THRESHOLD.get(),
                FOG_CHANCE.get(), EXPOSURE_ENABLED.get(), NETHER_RATE.get().floatValue());
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null && !server.isSameThread()) server.execute(() -> apply(values));
        else apply(values);
    }

    private record Values(RadiationAlgorithm algorithm, boolean chunkEnabled, boolean terrainEnabled, int fogThreshold,
                          int fogChance, boolean exposureEnabled, float netherRate) {}

    private static void apply(Values values) {
        HbmRadiation.configureAlgorithm(values.algorithm);
        // Preserve diffusion, timing, limits and the current persistent terrain scheduler.
        var current = HbmRadiation.settings();
        var terrain = current.worldEffects();
        HbmRadiation.configure(new RadiationSettings(values.chunkEnabled, current.updateIntervalTicks(),
                current.maximumRadiation(), current.diffusion(),
                new RadiationSettings.Fog(values.fogThreshold, values.fogChance),
                new RadiationSettings.WorldEffects(values.terrainEnabled, terrain.sampledChunks(),
                        terrain.attemptsPerChunk(), terrain.minimumRadiation())));
        // Other specs own the lung-disease flags; preserve their loaded values on every application.
        var exposure = HbmExposure.settings();
        HbmExposure.configure(new ExposureSettings(values.exposureEnabled, exposure.asbestos(),
                exposure.coalDust(), values.netherRate));
    }

    private SourceRadiationConfig() {}
}
