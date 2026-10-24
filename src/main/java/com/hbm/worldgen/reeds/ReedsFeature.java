package com.hbm.worldgen.reeds;

import com.hbm.block.structurebasic.BasicStructureContent;
import com.hbm.config.WorldgenControls;
import com.hbm.saveddata.TomSaveData;
import com.hbm.world.structure.StructureSurfaceBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
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

        // The old population offset matters: do not recenter or clamp the patch to its owner chunk.
        int centerX = (chunkX << 4) + random.nextInt(16) + 8;
        int centerZ = (chunkZ << 4) + random.nextInt(16) + 8;
        var center = readyChunk(level, chunkX, chunkZ, centerX, centerZ);
        if (center == null) return false;
        int centerY = center.getHeight(Heightmap.Types.WORLD_SURFACE, centerX & 15, centerZ & 15) + 1;
        int low = Math.max(level.getMinBuildHeight(), control.integer("minY", level.getMinBuildHeight()));
        int high = Math.min(level.getMaxBuildHeight() - 1, control.integer("maxY", level.getMaxBuildHeight() - 1));
        var reeds = BasicStructureContent.REEDS.get().defaultBlockState();
        var pos = new BlockPos.MutableBlockPos();
        boolean changed = false;
        for (int attempt = 0; attempt < 64; attempt++) {
            // Keep all six original random draws, even if this position cannot be written.
            int x = centerX + random.nextInt(8) - random.nextInt(8);
            int y = centerY + random.nextInt(4) - random.nextInt(4);
            int z = centerZ + random.nextInt(8) - random.nextInt(8);
            if (y < low || y > high || y <= level.getMinBuildHeight()
                    || level.getLevel().dimensionType().hasCeiling() && y >= level.getMaxBuildHeight() - 1) continue;
            pos.set(x, y, z);
            var chunk = readyChunk(level, chunkX, chunkZ, x, z);
            if (chunk == null || !level.ensureCanWrite(pos) || !chunk.getBlockState(pos).isAir()) continue;
            // Actual SourceReedsBlock.canSurvive accepts only vanilla WATER, including its flowing states.
            if (reeds.canSurvive(level, pos)) changed |= level.setBlock(pos, reeds, Block.UPDATE_CLIENTS);
        }
        return changed;
    }

    private static ChunkAccess readyChunk(WorldGenLevel level, int ownerX, int ownerZ, int x, int z) {
        int chunkX = x >> 4, chunkZ = z >> 4;
        // Native FEATURES supplies CARVERS-ready neighbours at distance 1 and permits only these writes.
        if (Math.abs(chunkX - ownerX) > 1 || Math.abs(chunkZ - ownerZ) > 1
                || !level.hasChunk(chunkX, chunkZ)) return null;
        if (level instanceof WorldGenRegion) return level.getChunk(chunkX, chunkZ, ChunkStatus.CARVERS, false);
        // /place feature and other direct callers may use ServerLevel: never request or wait for a new chunk.
        return level.getLevel().getChunkSource().getChunkNow(chunkX, chunkZ);
    }
}
