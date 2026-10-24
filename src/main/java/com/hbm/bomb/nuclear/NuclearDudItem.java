package com.hbm.bomb.nuclear;

import java.util.Map;
import java.util.Objects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Each fixed item places its one source dud type; all four retain the shared block name. */
public final class NuclearDudItem extends BlockItem {
 private final NuclearDudBlock.Type type;
 public NuclearDudItem(Block block,NuclearDudBlock.Type type){super(block,new Properties());this.type=Objects.requireNonNull(type);}
 public NuclearDudBlock.Type type(){return type;}
 /** The block's ordinary item form is its source default; placement and pick/drop remain type-specific. */
 @Override public void registerBlocks(Map<Block,Item> blocks,Item item){if(type==NuclearDudBlock.Type.BALEFIRE)super.registerBlocks(blocks,item);}
 @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);return state==null?null:state.setValue(NuclearDudBlock.META,type.ordinal());}
}
