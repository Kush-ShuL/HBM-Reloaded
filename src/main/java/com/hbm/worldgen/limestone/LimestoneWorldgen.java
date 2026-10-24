package com.hbm.worldgen.limestone;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers only the missing natural feature; reuses the existing limestone block and generation spec. */
public final class LimestoneWorldgen {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");

    public static void register(IEventBus bus) {
        FEATURES.register("limestone",LimestoneFeature::new);
        FEATURES.register(bus);
    }

    private LimestoneWorldgen() { }
}
