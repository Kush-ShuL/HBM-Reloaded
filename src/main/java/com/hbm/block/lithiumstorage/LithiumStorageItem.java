package com.hbm.block.lithiumstorage;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

/** Source ItemBlock calls BlockLithium.onBlockPlaced before placeBlockAt, then still places the block after the reaction. */
public final class LithiumStorageItem extends BlockItem {
    public LithiumStorageItem(LithiumStorageBlock block){super(block,new Properties());}
    @Override protected boolean placeBlock(BlockPlaceContext context,BlockState state){
        if(LithiumStorageBlock.touchesWater(context.getLevel(),context.getClickedPos()))LithiumStorageBlock.react(context.getLevel(),context.getClickedPos());
        return super.placeBlock(context,state);
    }
}
