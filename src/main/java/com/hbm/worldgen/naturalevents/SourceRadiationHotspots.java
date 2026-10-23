package com.hbm.worldgen.naturalevents;

import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.config.GeneralModes;
import com.hbm.config.WorldgenControls;
import com.hbm.world.structure.StructureSurfaceBiomes;
import com.hbm.world.structure.generated.GeneratedLegacyFeatures;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

/** Original HbmWorld.registerNTMTerrain MapGenCrater; writes only its current 16x16 target. */
public final class SourceRadiationHotspots {
    public static boolean place(WorldGenLevel level, ChunkPos target) {
        if (!GeneralModes.flag("1.06_enableRadHotspotSpawn")
                || !WorldgenControls.oreFeature("radiation_hotspots")) return false;
        var control = WorldgenControls.rule("radiation_hotspots", "sellafield_crater");
        int frequency = control.chance(GeneratedLegacyFeatures.radiationHotspotFrequency());
        if (!control.enabled() || frequency <= 0) return false;
        int low = Math.max(level.getMinBuildHeight(), control.integer("minY", 0));
        int high = Math.min(level.getMaxBuildHeight() - 1, control.integer("maxY", 127));
        if (high < low) return false;
        var seed = new Random(level.getSeed());
        long xSeed = seed.nextLong(), zSeed = seed.nextLong();
        var surface = HbmWorldFluids.block("sellafield_slaked").defaultBlockState();
        var air = Blocks.AIR.defaultBlockState();
        var pos = new BlockPos.MutableBlockPos();
        boolean changed = false;
        // MapGenBase.range remains eight: the source registration never calls setSize().
        for (int ox = target.x - 8; ox <= target.x + 8; ox++) {
            for (int oz = target.z - 8; oz <= target.z + 8; oz++) {
                var random = new Random((long) ox * xSeed ^ (long) oz * zSeed ^ level.getSeed());
                if (random.nextInt(frequency) != 0 || !desert(level, ox * 16, oz * 16)) continue;
                int xCoord = -ox + target.x, zCoord = -oz + target.z;
                double radius = random.nextInt(64 - 8) + 8;
                double depth = radius * 0.35D;
                for (int bx = 15; bx >= 0; bx--) {
                    for (int bz = 15; bz >= 0; bz--) {
                        int wx = target.getMinBlockX() + bx, wz = target.getMinBlockZ() + bz;
                        for (int y = high; y >= low; y--) {
                            pos.set(wx, y, wz);
                            var old = level.getBlockState(pos);
                            if (!old.canOcclude() && !LegacyMaterials.of(old).isLiquid()) continue;
                            int x = xCoord * 16 + bx, z = zCoord * 16 + bz;
                            double r = Math.sqrt(x * x + z * z);
                            if (r - random.nextInt(3) <= radius) {
                                double curve = -Math.pow(r, 2) / Math.pow(radius, 2) * depth + depth;
                                // Source clamp_double checks the lower bound before the upper bound.
                                int dep = (int) clamp(curve, 0, y - low - 1);
                                for (int i = 0; i < dep; i++) {
                                    pos.set(wx, y - i, wz);
                                    if (level.setBlock(pos, air, 2)) changed = true;
                                }
                                y -= dep;
                                dep = Math.min(3, y - low - 1);
                                // Registration sets BOTH regolith and rock to the same real slaked block.
                                // Keep the branch's nextInt(3), even though its two outputs are identical.
                                boolean regolith = r + random.nextInt(3) <= radius / 3D;
                                if (regolith) {
                                    for (int i = 0; i < dep; i++) {
                                        pos.set(wx, y - i, wz);
                                        if (level.setBlock(pos, surface, 2)) changed = true;
                                    }
                                } else {
                                    for (int i = 0; i < dep; i++) {
                                        pos.set(wx, y - i, wz);
                                        if (level.setBlock(pos, surface, 2)) changed = true;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return changed;
    }

    private static boolean desert(WorldGenLevel level, int x, int z) {
        var chunks = level.getLevel().getChunkSource();
        var generator = chunks.getGenerator();
        var source = generator.getBiomeSource();
        // Virtual MapGen sources may be eight chunks away. Native base columns do not
        // request an out-of-region chunk, unlike WorldGenRegion.getHeight there.
        int y = source instanceof FixedBiomeSource
                || source instanceof MultiNoiseBiomeSource multi
                && multi.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD) ? 0
                : generator.getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                        level, chunks.randomState()) - 1;
        return StructureSurfaceBiomes.sample(source, chunks.randomState().sampler(), x, y, z).is(Biomes.DESERT);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return value < minimum ? minimum : value > maximum ? maximum : value;
    }

    private SourceRadiationHotspots() { }
}
