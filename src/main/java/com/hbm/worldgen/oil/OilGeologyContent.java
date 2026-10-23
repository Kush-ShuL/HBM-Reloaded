package com.hbm.worldgen.oil;
import net.minecraft.core.registries.Registries;import net.minecraft.world.level.levelgen.feature.Feature;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.DeferredRegister;
public final class OilGeologyContent {private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");public static void register(IEventBus bus){FEATURES.register("legacy_oil_terrain",OilGeologyFeature::new);FEATURES.register(bus);}}
