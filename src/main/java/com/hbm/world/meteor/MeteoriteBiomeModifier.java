package com.hbm.world.meteor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/** A dimension check belongs to placement, since biomes can be shared between dimensions. */
public record MeteoriteBiomeModifier(Holder<PlacedFeature> feature) implements BiomeModifier {
    public static final MapCodec<MeteoriteBiomeModifier> CODEC = PlacedFeature.CODEC
        .fieldOf("feature").xmap(MeteoriteBiomeModifier::new, MeteoriteBiomeModifier::feature);

    @Override public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) builder.getGenerationSettings()
            .addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, feature);
    }
    @Override public MapCodec<? extends BiomeModifier> codec() { return CODEC; }
}
