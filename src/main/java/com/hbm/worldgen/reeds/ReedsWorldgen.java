package com.hbm.worldgen.reeds;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual natural HBM reeds, independent of the mineral and building switches. */
public final class ReedsWorldgen {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "hbm");

    public static void register(IEventBus bus) {
        FEATURES.register("legacy_reeds", ReedsFeature::new);
        FEATURES.register(bus);
    }

    private ReedsWorldgen() { }
}
