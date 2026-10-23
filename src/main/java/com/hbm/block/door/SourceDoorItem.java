package com.hbm.block.door;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/** Original UP-only placement, one block above the clicked support; stack size one. */
public final class SourceDoorItem extends BlockItem {
    public SourceDoorItem(SourceDoorBlock block){super(block,new Item.Properties().stacksTo(1));}
    @Override public InteractionResult useOn(UseOnContext context){
        var player=context.getPlayer();var level=context.getLevel();var stack=context.getItemInHand();
        if(player==null||context.getClickedFace()!=Direction.UP)return InteractionResult.PASS;
        var pos=context.getClickedPos().above();var top=pos.above();
        if(pos.getY()>=level.getMaxBuildHeight()-1||!player.mayUseItemAt(pos,Direction.UP,stack)||!player.mayUseItemAt(top,Direction.UP,stack)
            ||!level.getBlockState(pos).canBeReplaced()||!level.getBlockState(top).canBeReplaced())return InteractionResult.FAIL;
        int metadata=Mth.floor((double)((player.getYRot()+180F)*4F/360F)-.5D)&3;
        Direction facing=switch(metadata){case 0->Direction.EAST;case 1->Direction.SOUTH;case 2->Direction.WEST;default->Direction.NORTH;};
        var state=getBlock().defaultBlockState().setValue(DoorBlock.FACING,facing).setValue(DoorBlock.HINGE,SourceDoorBlock.sourceHinge(level,pos,facing,getBlock()));
        if(!state.canSurvive(level,pos))return InteractionResult.FAIL;
        level.setBlock(pos,state,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        level.setBlock(top,state.setValue(DoorBlock.HALF,DoubleBlockHalf.UPPER),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        level.updateNeighborsAt(pos,getBlock());level.updateNeighborsAt(top,getBlock());
        stack.consume(1,player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
