package com.hbm.multiblock;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Real ToolType.HAND_DRILL adapter; tool items decide their own durability and charge cost. */
public interface MultiblockTool {
    boolean isHandDrill(ItemStack stack, Player player);
    default void onPartRemoved(ItemStack stack, Player player, InteractionHand hand) {}
}
