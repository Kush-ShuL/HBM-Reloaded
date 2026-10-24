package com.hbm.worldgen.gas;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Uses the existing WorldgenControls spec; no parallel gas settings file. */
public final class GasBubbleWorldgen {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");

    public static void register(IEventBus bus) {
        FEATURES.register("gas_bubbles",GasBubbleFeature::new);
        FEATURES.register(bus);
    }

    private GasBubbleWorldgen() { }
}
