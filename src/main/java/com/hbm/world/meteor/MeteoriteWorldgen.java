package com.hbm.world.meteor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class MeteoriteWorldgen {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "hbm");
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> MODIFIERS =
        DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "hbm");
    static {
        FEATURES.register("fallen_meteorite", MeteoriteFeature::new);
        MODIFIERS.register("fallen_meteorite", () -> MeteoriteBiomeModifier.CODEC);
    }
    public static void register(IEventBus bus) { FEATURES.register(bus); MODIFIERS.register(bus); }
    private MeteoriteWorldgen() {}
}
