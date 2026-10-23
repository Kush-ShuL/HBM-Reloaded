package com.hbm.machine.siren;

import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Unoriented full cube: source top/bottom steel icon, same siren icon on all four sides. */
public final class SirenBlock extends BaseEntityBlock {
    private final Random dropRandom=new Random();
    public SirenBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SirenBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,SirenContent.ENTITY.get(),(world,pos,block,machine)->machine.tickServer());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof SirenBlockEntity machine)server.openMenu(machine);return InteractionResult.CONSUME;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!next.is(this)&&level.getBlockEntity(pos) instanceof SirenBlockEntity machine){var stack=machine.getItem(0);if(!stack.isEmpty()){float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int amount=Math.min(dropRandom.nextInt(21)+10,stack.getCount());var drop=stack.copyWithCount(amount);stack.shrink(amount);var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,drop);entity.setDeltaMovement((float)dropRandom.nextGaussian()*.05F,(float)dropRandom.nextGaussian()*.05F+.2F,(float)dropRandom.nextGaussian()*.05F);level.addFreshEntity(entity);}}level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moving);}
}
