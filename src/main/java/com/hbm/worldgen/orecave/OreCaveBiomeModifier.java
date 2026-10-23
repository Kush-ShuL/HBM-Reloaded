package com.hbm.worldgen.orecave;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.*;

/** Preserve original SchistStratum → OreCave → OreLayer3D order independently of datapack filename order. */
public record OreCaveBiomeModifier(HolderSet<Biome> biomes,Holder<PlacedFeature> feature) implements BiomeModifier {
    public static final MapCodec<OreCaveBiomeModifier> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(OreCaveBiomeModifier::biomes),PlacedFeature.CODEC.fieldOf("feature").forGetter(OreCaveBiomeModifier::feature)).apply(instance,OreCaveBiomeModifier::new));
    private static final ResourceLocation GNEISS=ResourceLocation.parse("hbm:legacy_gneiss_stratum");
    @Override public void modify(Holder<Biome> biome,Phase phase,ModifiableBiomeInfo.BiomeInfo.Builder builder){
        if(phase!=Phase.AFTER_EVERYTHING||!biomes.contains(biome))return;
        var entries=builder.getGenerationSettings().getFeatures(GenerationStep.Decoration.LOCAL_MODIFICATIONS);
        // Remove only our feature if a datapack already added it, never another mod's entry.
        entries.removeIf(existing->existing.equals(feature));int insert=entries.size();
        for(int i=0;i<entries.size();i++)if(entries.get(i).unwrapKey().map(key->key.location().equals(GNEISS)).orElse(false))insert=i+1;
        entries.add(insert,feature);
    }
    @Override public MapCodec<? extends BiomeModifier> codec(){return CODEC;}
}
