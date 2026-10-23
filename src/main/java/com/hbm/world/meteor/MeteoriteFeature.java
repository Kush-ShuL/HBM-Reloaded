package com.hbm.world.meteor;

import com.hbm.config.WorldgenControls;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** HbmWorldGen's fallen meteor entry, with the original position/shape draw order. */
public final class MeteoriteFeature extends Feature<NoneFeatureConfiguration> {
    public MeteoriteFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!WorldgenControls.ores() || !MeteorConfig.WORLDGEN_ENABLED.get()) return false;
        var level = context.level();
        var dimension = level.getLevel().dimension();
        if (dimension.equals(Level.NETHER) || dimension.equals(Level.END)) return false;
        if (!dimension.equals(Level.OVERWORLD) && !MeteorConfig.WORLDGEN_OTHER_DIMENSIONS.get()) return false;
        int chance = MeteorConfig.WORLDGEN_CHANCE.get();
        var random = context.random();
        if (chance <= 0 || random.nextInt(chance) != 0) return false;
        int x = (context.origin().getX() & ~15) + random.nextInt(16) + 8;
        int z = (context.origin().getZ() & ~15) + random.nextInt(16) + 8;
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - random.nextInt(10);
        if (y <= level.getMinBuildHeight() + 1) return false;
        var ground = level.getBlockState(new BlockPos(x, y - 2, z));
        if (ground.isAir() || ground.liquid()) return false;
        // Normal source shapes reach at most three blocks from their center.
        // The native feature region already owns the target and adjacent generation chunks.
        new MeteoriteGenerator().generateNatural(level, random, x, y, z);
        return true;
    }
}
