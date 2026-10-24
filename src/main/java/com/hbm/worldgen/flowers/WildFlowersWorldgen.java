package com.hbm.worldgen.flowers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers the one real native feature for all four ordered source herb branches. */
public final class WildFlowersWorldgen {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");

    public static void register(IEventBus bus) {
        FEATURES.register("wild_flowers",WildFlowersFeature::new);
        FEATURES.register(bus);
    }

    private WildFlowersWorldgen() { }
}
