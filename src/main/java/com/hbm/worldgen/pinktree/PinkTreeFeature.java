package com.hbm.worldgen.pinktree;

import com.hbm.armor.neutrinolens.NeutrinoLensContent;
import com.hbm.config.WorldgenControls;
import com.hbm.world.structure.NaturalStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** HbmWorldGen361..376, after native tree decoration rather than before trees exist. */
public final class PinkTreeFeature extends Feature<NoneFeatureConfiguration> {
    public PinkTreeFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level = context.level();
        if (!level.getLevel().dimension().equals(Level.OVERWORLD) || !NaturalStructures.enabled()
                || !level.getLevel().getServer().getWorldData().worldGenOptions().generateStructures()
                || !WorldgenControls.feature("pink_trees")) return false;
        var rule = WorldgenControls.rule("pink_trees", "pink_log");
        if (!rule.enabled()) return false;
        int chance = rule.chance(1000);
        var random = context.random();
        if (chance == 0 || random.nextInt(chance) != 0) return false;
        int x = (context.origin().getX() & ~15) + random.nextInt(16);
        int z = (context.origin().getZ() & ~15) + random.nextInt(16);
        int minY = Math.max(level.getMinBuildHeight(), rule.integer("minY", 0));
        int maxY = Math.min(level.getMaxBuildHeight() - 1, rule.integer("maxY", 255));
        var pos = new BlockPos.MutableBlockPos();
        boolean changed = false;
        for (int y = minY; y <= maxY; y++) {
            pos.set(x, y, z);
            var state = level.getBlockState(pos);
            // Original Blocks.log with raw metadata0: oak and upright, never other wood/axes.
            if (state.is(Blocks.OAK_LOG) && state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y
                    && level.ensureCanWrite(pos)) {
                changed |= level.setBlock(pos, NeutrinoLensContent.PINK_LOG.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        return changed;
    }
}
