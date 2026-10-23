package com.hbm.worldgen.bedrockresource;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.*;

/** Source Nether order is ordinary veins → weighted bedrock → depth deposits → smolder trials. */
public record BedrockResourceBiomeModifier(HolderSet<Biome> biomes,Holder<PlacedFeature> feature,boolean nether) implements BiomeModifier {
    public static final MapCodec<BedrockResourceBiomeModifier> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(BedrockResourceBiomeModifier::biomes),PlacedFeature.CODEC.fieldOf("feature").forGetter(BedrockResourceBiomeModifier::feature),com.mojang.serialization.Codec.BOOL.fieldOf("nether").forGetter(BedrockResourceBiomeModifier::nether)).apply(instance,BedrockResourceBiomeModifier::new));
    private static final List<ResourceLocation> ORDER=List.of(ResourceLocation.parse("hbm:legacy_nether_ores"),ResourceLocation.parse("hbm:legacy_nether_bedrock_resources"),ResourceLocation.parse("hbm:legacy_nether_depth_deposits"),ResourceLocation.parse("hbm:legacy_nether_smolder"));
    @Override public void modify(Holder<Biome> biome,Phase phase,ModifiableBiomeInfo.BiomeInfo.Builder builder){
        if(phase!=Phase.AFTER_EVERYTHING||!biomes.contains(biome))return;
        var features=builder.getGenerationSettings().getFeatures(GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
        if(!nether){features.removeIf(entry->entry.equals(feature));features.add(feature);return;}
        var present=new HashMap<ResourceLocation,Holder<PlacedFeature>>();int insert=features.size();
        for(int i=0;i<features.size();i++){var entry=features.get(i);var id=entry.unwrapKey().map(key->key.location()).orElse(null);if(id!=null&&ORDER.contains(id)){insert=Math.min(insert,i);present.put(id,entry);}}
        present.put(ORDER.get(1),feature);
        // Only these four actual source sibling features move. Every unrelated
        // datapack feature keeps its relative order; nothing is removed as a fallback.
        features.removeIf(entry->entry.unwrapKey().map(key->ORDER.contains(key.location())).orElse(false));
        int at=Math.min(insert,features.size());for(var id:ORDER){var entry=present.get(id);if(entry!=null)features.add(at++,entry);}
    }
    @Override public MapCodec<? extends BiomeModifier> codec(){return CODEC;}
}
