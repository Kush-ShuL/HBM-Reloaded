package com.hbm.worldgen.alexandrite;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** One native feature; all settings remain in the existing WorldgenControls spec. */
public final class AlexandriteWorldgen {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");

    public static void register(IEventBus bus) {
        FEATURES.register("alexandrite",AlexandriteFeature::new);
        FEATURES.register(bus);
    }

    private AlexandriteWorldgen() { }
}
