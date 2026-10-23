package com.hbm.block.ore;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/** Original IDepthRockTool contract, with the original position/dimension/player decision retained. */
public interface DepthRockTool {
    boolean canBreakRock(BlockGetter level, Player player, ItemStack tool, BlockState rock, BlockPos position);
}
