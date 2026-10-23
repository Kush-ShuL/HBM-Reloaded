package com.hbm.worldgen.ore;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual configured/placed feature registration with an overworld biome modifier. */
public final class OreWorldgen {
    public static final List<OreProfile> PROFILES = profiles();
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "hbm");
    public static final ModConfigSpec CONFIG;
    public static final ModConfigSpec.BooleanValue OVERWORLD, DEEP;
    public static final ModConfigSpec.IntValue DEEP_BOTTOM_OFFSET, DEEP_TOP;
    public static final ModConfigSpec.DoubleValue DEEP_MULTIPLIER;
    private static final Map<String, ModConfigSpec.IntValue> COUNTS = new LinkedHashMap<>();
    static {
        var builder = new ModConfigSpec.Builder();
        OVERWORLD = builder.define("overworldOre", true);
        DEEP = builder.comment("New 1.18+ extension; normal positive-height ore bands remain unchanged.").define("deepslateOre", true);
        DEEP_BOTTOM_OFFSET = builder.defineInRange("deepBottomOffset", 5, 0, 512);
        DEEP_TOP = builder.defineInRange("deepTopY", -1, -2048, -1);
        DEEP_MULTIPLIER = builder.defineInRange("deepCountMultiplier", 1D, 0D, 100D);
        for (OreProfile profile : PROFILES) COUNTS.put(profile.config(), builder.defineInRange(profile.config(), profile.count(), 0, 10000));
        CONFIG = builder.build();
    }
    private OreWorldgen() { }
    private static List<OreProfile> profiles() {
        try (var stream = OreWorldgen.class.getResourceAsStream("/hbm/ore-worldgen.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_deepslate_ores.py");
            return List.copyOf(new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Definitions.class).profiles());
        } catch (java.io.IOException error) { throw new IllegalStateException(error); }
    }
    public static void register(IEventBus bus, ModContainer container) {
        FEATURES.register("legacy_overworld_ores", LegacyOverworldOreFeature::new);
        FEATURES.register(bus);
        container.registerConfig(ModConfig.Type.COMMON, CONFIG, "hbm-ore-worldgen-common.toml");
    }
    public static int count(OreProfile profile) { return profile.enabled() ? COUNTS.get(profile.config()).get() : 0; }
    /** Source settings also drive custom-host ores; disabling duplicate ordinary copper must not disable gneiss copper. */
    public static int configuredCount(String key) { var field=COUNTS.get(key); if(field==null) throw new IllegalArgumentException("Unknown source ore count "+key); return field.get(); }
    private record Definitions(List<OreProfile> profiles) { }
}
