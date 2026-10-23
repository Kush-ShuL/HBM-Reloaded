package com.hbm.worldgen.pinktree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record PinkTreeBiomeModifier(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) implements BiomeModifier {
    public static final MapCodec<PinkTreeBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(PinkTreeBiomeModifier::biomes),
            PlacedFeature.CODEC.fieldOf("feature").forGetter(PinkTreeBiomeModifier::feature)
    ).apply(instance, PinkTreeBiomeModifier::new));

    @Override public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.AFTER_EVERYTHING || !biomes.contains(biome)) return;
        var features = builder.getGenerationSettings().getFeatures(GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
        features.removeIf(feature::equals);
        features.add(feature);
    }

    @Override public MapCodec<? extends BiomeModifier> codec() { return CODEC; }
}
