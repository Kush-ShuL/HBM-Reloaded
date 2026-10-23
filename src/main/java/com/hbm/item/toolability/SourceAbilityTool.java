package com.hbm.item.toolability;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Item-specific source availability/harvesting passed into the sole existing preset, GUI and break engine. */
public interface SourceAbilityTool extends SourceAbilityCombat {
    AvailableAbilities abilities();
    boolean sourceCanHarvest(ItemStack stack,BlockState state);
    /** Source setShears only extends break eligibility; ordinary harvest/drop callbacks remain unchanged. */
    default boolean sourceCanShear(net.minecraft.world.entity.player.Player player,ItemStack stack,net.minecraft.world.level.Level level,net.minecraft.core.BlockPos pos){return false;}
}
