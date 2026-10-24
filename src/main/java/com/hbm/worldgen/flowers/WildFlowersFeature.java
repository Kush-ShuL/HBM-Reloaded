package com.hbm.worldgen.flowers;

import com.hbm.block.oilplant.OilBotanyContent;
import com.hbm.block.oilplant.OilFlowerBlock;
import com.hbm.config.WorldgenControls;
import com.hbm.saveddata.TomSaveData;
import com.hbm.world.structure.StructureSurfaceBiomes;
import com.hbm.worldgen.surface.SurfaceFlowerScatter;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** The four ordered HbmWorldGen surface herb branches, using the actual growing plant states. */
public final class WildFlowersFeature extends Feature<NoneFeatureConfiguration> {
    private static final TagKey<Biome> FORESTS=biomes("wild_flowers_forest");
    private static final TagKey<Biome> DARK_FORESTS=biomes("wild_flowers_dark_forest");
    private static final TagKey<Biome> JUNGLES=biomes("wild_flowers_jungle");

    public WildFlowersFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    private static TagKey<Biome> biomes(String name) {
        return TagKey.create(Registries.BIOME,ResourceLocation.fromNamespaceAndPath("hbm",name));
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level=context.level();
        var dimension=level.getLevel().dimension();
        if(dimension.equals(Level.NETHER)||dimension.equals(Level.END)
                ||!dimension.equals(Level.OVERWORLD)&&!WorldgenControls.moddedDimensions()
                ||!WorldgenControls.feature("wild_flowers")||TomSaveData.forWorld(level.getLevel()).impact)return false;

        int chunkX=context.origin().getX()>>4;
        int chunkZ=context.origin().getZ()>>4;
        Holder<Biome> biome=StructureSurfaceBiomes.sample(level,chunkX<<4,chunkZ<<4);
        boolean changed=false;
        // Keep the source order and allow both forest branches in a dark forest.
        if(biome.is(FORESTS))changed|=branch(level,context.random(),chunkX,chunkZ,"foxglove",16,0);
        if(biome.is(DARK_FORESTS))changed|=branch(level,context.random(),chunkX,chunkZ,"nightshade",8,2);
        if(biome.is(JUNGLES))changed|=branch(level,context.random(),chunkX,chunkZ,"tobacco",8,1);
        changed|=branch(level,context.random(),chunkX,chunkZ,"weed",64,3);
        return changed;
    }

    private static boolean branch(WorldGenLevel level,RandomSource random,int chunkX,int chunkZ,
                                  String name,int sourceChance,int plantState) {
        var control=WorldgenControls.rule("wild_flowers",name);
        if(!control.enabled())return false;
        int chance=control.chance(sourceChance);
        if(chance<=0||random.nextInt(chance)!=0)return false;
        var flower=OilBotanyContent.FLOWER.get().defaultBlockState().setValue(OilFlowerBlock.META,plantState);
        return SurfaceFlowerScatter.scatter(level,random,chunkX,chunkZ,flower,control);
    }
}
