package com.hbm.machine.pedestal;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Original redstone-triggered nine-pedestal circuit, including exact whole-stack deposit/withdrawal. */
public final class PedestalBlock extends BaseEntityBlock {
    private static final MapCodec<PedestalBlock> CODEC=simpleCodec(PedestalBlock::new);
    public PedestalBlock(Properties properties){super(properties);}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PedestalBlockEntity(pos,state);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,PedestalContent.ENTITY.get(),PedestalBlockEntity::tick);}
    private InteractionResult exchange(Level level,BlockPos pos,Player player,InteractionHand hand){
        if(player.isShiftKeyDown()||!(level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal))return InteractionResult.PASS;
        var held=player.getItemInHand(hand);
        if(pedestal.item().isEmpty()&&!held.isEmpty()){
            if(!level.isClientSide){pedestal.setItem(held.copy());player.setItemInHand(hand,ItemStack.EMPTY);}return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(!pedestal.item().isEmpty()&&held.isEmpty()){
            if(!level.isClientSide){player.setItemInHand(hand,pedestal.item().copy());pedestal.setItem(ItemStack.EMPTY);}return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return exchange(level,pos,player,hand).consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return exchange(level,pos,player,InteractionHand.MAIN_HAND);}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
        if(!state.is(replacement.getBlock())&&!level.isClientSide&&level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal&&!pedestal.item().isEmpty()){
            level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY(),pos.getZ()+.5,pedestal.item().copy()));
        }
        super.onRemove(state,level,pos,replacement,moving);
    }
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){
        if(!level.isClientSide&&level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal)pedestal.craft();
    }
}
