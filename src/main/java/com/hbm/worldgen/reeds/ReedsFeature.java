package com.hbm.worldgen.reeds;

import com.hbm.block.structurebasic.BasicStructureContent;
import com.hbm.config.WorldgenControls;
import com.hbm.worldgen.surface.SurfaceFlowerScatter;
import com.hbm.saveddata.TomSaveData;
import com.hbm.world.structure.StructureSurfaceBiomes;
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

/** HbmWorldGen84..88 -> DungeonToolbox.generateFlowers -> original 64-attempt WorldGenFlowers. */
public final class ReedsFeature extends Feature<NoneFeatureConfiguration> {
    private static final TagKey<Biome> RIVERS = TagKey.create(Registries.BIOME, ResourceLocation.parse("hbm:reeds_river"));
    private static final TagKey<Biome> BEACHES = TagKey.create(Registries.BIOME, ResourceLocation.parse("hbm:reeds_beach"));

    public ReedsFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level = context.level();
        var dimension = level.getLevel().dimension();
        if (dimension.equals(Level.NETHER) || dimension.equals(Level.END)
                || !dimension.equals(Level.OVERWORLD) && !WorldgenControls.moddedDimensions()
                || !WorldgenControls.feature("reeds") || TomSaveData.forWorld(level.getLevel()).impact) return false;

        int chunkX = context.origin().getX() >> 4;
        int chunkZ = context.origin().getZ() >> 4;
        Holder<Biome> biome = StructureSurfaceBiomes.sample(level, chunkX << 4, chunkZ << 4);
        boolean changed = false;
        if (biome.is(RIVERS)) changed |= branch(level, context.random(), chunkX, chunkZ, "river", 4);
        if (biome.is(BEACHES)) changed |= branch(level, context.random(), chunkX, chunkZ, "beach", 8);
        return changed;
    }

    private static boolean branch(WorldGenLevel level, RandomSource random, int chunkX, int chunkZ,
                                  String name, int sourceChance) {
        var control = WorldgenControls.rule("reeds", name);
        if (!control.enabled()) return false;
        int chance = control.chance(sourceChance);
        if (chance <= 0 || random.nextInt(chance) != 0) return false;

        return SurfaceFlowerScatter.scatter(level,random,chunkX,chunkZ,
                BasicStructureContent.REEDS.get().defaultBlockState(),control);
    }
}
