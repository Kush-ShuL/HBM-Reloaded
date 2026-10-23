package com.hbm.world.structure;

import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;

/** The source's two-dimensional biome lookup, projected through the native surface climate. */
public final class StructureSurfaceBiomes {
    public static Holder<Biome> sample(Structure.GenerationContext context, int x, int z) {
        var source = context.biomeSource();
        var sampler = context.randomState().sampler();
        if (withoutHeight(source)) return sample(source, sampler, x, 0, z);
        int y = context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState()) - 1;
        return sample(source, sampler, x, y, z);
    }

    public static Holder<Biome> sample(WorldGenLevel level, int x, int z) {
        var chunks = level.getLevel().getChunkSource();
        var source = chunks.getGenerator().getBiomeSource();
        var sampler = chunks.randomState().sampler();
        if (withoutHeight(source)) return sample(source, sampler, x, 0, z);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
        return sample(source, sampler, x, y, z);
    }

    private static boolean withoutHeight(BiomeSource source) {
        // Native overworld noise_router's other five climate axes are 2D
        // (shifted_noise y_scale=0). Sampling depth zero needs no terrain column.
        return source instanceof FixedBiomeSource || source instanceof MultiNoiseBiomeSource multi
                && multi.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
    }

    public static Holder<Biome> sample(BiomeSource source, Climate.Sampler sampler, int x, int y, int z) {
        if (source instanceof MultiNoiseBiomeSource multi && multi.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
            var target = sampler.sample(x >> 2, y >> 2, z >> 2);
            // Actual OverworldBiomeBuilder.addSurfaceBiome emits matching climate
            // points at depth 0 and 1. Underground points use .2--.9 (caves) and
            // 1.1 (deep dark). Preserve the real remaining five climate axes.
            return multi.getNoiseBiome(new Climate.TargetPoint(target.temperature(), target.humidity(),
                    target.continentalness(), target.erosion(), 0, target.weirdness()));
        }
        // Fixed sources and custom depth parameter lists retain the author's
        // actual biome; a cave selected explicitly for a flat world is not jungle.
        return source.getNoiseBiome(x >> 2, y >> 2, z >> 2, sampler);
    }

    private StructureSurfaceBiomes() { }
}
