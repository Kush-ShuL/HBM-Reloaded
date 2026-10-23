package com.hbm.bomb.nuclear;

import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Original ItemBlockEnum's raw metadata on placement; display name intentionally stays shared across all four source duds. */
public final class NuclearDudItem extends BlockItem {
 public NuclearDudItem(Block block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
 @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);return state==null?null:state.setValue(NuclearDudBlock.META,context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&15);}
}
