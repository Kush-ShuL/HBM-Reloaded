package com.hbm.block.falling;

import com.hbm.block.fluid.LegacyMaterial;
import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Exact old BlockFalling free-space and canPlaceEntityOnSide material predicates. */
public final class LegacyFallingRules {
    private LegacyFallingRules() { }
    public static LegacyMaterial material(BlockState state) {
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        // Flattened old variants whose registry names changed after 1.7.10.
        if (id.startsWith("minecraft:")) {
            String path = id.substring(10);
            if (java.util.Set.of("dandelion", "poppy", "blue_orchid", "allium", "azure_bluet", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy").contains(path))
                return new LegacyMaterial("plants", false, false, false, false, false);
            if (java.util.Set.of("short_grass", "fern", "dead_bush", "vine").contains(path))
                return new LegacyMaterial("vine", false, false, false, true, true);
            if (path.endsWith("_torch") || path.equals("torch")) return new LegacyMaterial("circuits", false, false, false, false, false);
        }
        return LegacyMaterials.of(state);
    }
    public static boolean isFree(BlockState state) {
        LegacyMaterial material = material(state);
        return material.name().equals("air") || state.is(Blocks.FIRE) || material == LegacyMaterial.water || material == LegacyMaterial.lava;
    }
    public static boolean canPlace(Level level, BlockPos pos, BlockState state) {
        if (level.isOutsideBuildHeight(pos)) return false;
        var previous = level.getBlockState(pos);
        // Old canPlaceEntityOnSide ignores collision here; its sole special case is anvil on circuits.
        boolean anvil = state.is(net.minecraft.tags.BlockTags.ANVIL);
        return material(previous).name().equals("circuits") && anvil
                || (material(previous).isReplaceable() || previous.getBlock() instanceof com.hbm.bomb.nuclear.NuclearTerrain.Layer) && state.canSurvive(level, pos);
    }
}
