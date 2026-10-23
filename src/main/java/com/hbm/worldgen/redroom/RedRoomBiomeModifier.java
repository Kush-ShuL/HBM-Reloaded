package com.hbm.worldgen.redroom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.*;

public record RedRoomBiomeModifier(HolderSet<Biome> biomes,Holder<PlacedFeature> feature) implements BiomeModifier {
    public static final MapCodec<RedRoomBiomeModifier> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(RedRoomBiomeModifier::biomes),PlacedFeature.CODEC.fieldOf("feature").forGetter(RedRoomBiomeModifier::feature)).apply(instance,RedRoomBiomeModifier::new));
    @Override public void modify(Holder<Biome> biome,Phase phase,ModifiableBiomeInfo.BiomeInfo.Builder builder){if(phase==Phase.AFTER_EVERYTHING&&biomes.contains(biome)){var list=builder.getGenerationSettings().getFeatures(GenerationStep.Decoration.TOP_LAYER_MODIFICATION);list.removeIf(entry->entry.equals(feature));list.add(feature);}}
    @Override public MapCodec<? extends BiomeModifier> codec(){return CODEC;}
}
