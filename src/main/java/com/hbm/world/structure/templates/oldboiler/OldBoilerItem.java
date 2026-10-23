package com.hbm.world.structure.templates.oldboiler;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
/** Ordinary source ItemBlock, with one final native placement state instead of two visible yaw changes. */
public final class OldBoilerItem extends BlockItem {
    public OldBoilerItem(OldBoilerBlock block){super(block,new Properties());}
    @Override protected boolean placeBlock(BlockPlaceContext context,BlockState state){OldBoilerBlock.beginItemPlacement();try{return super.placeBlock(context,state);}finally{OldBoilerBlock.endItemPlacement();}}
}
