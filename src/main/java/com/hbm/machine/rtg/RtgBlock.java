package com.hbm.machine.rtg;

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

/** Original non-opaque full collision cube; no invented facing state or retained power item. */
public final class RtgBlock extends BaseEntityBlock {
    public static final MapCodec<RtgBlock> CODEC=simpleCodec(properties->new RtgBlock());
    private final Random dropRandom=new Random();
    public RtgBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RtgBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,RtgContent.ENTITY.get(),RtgBlockEntity::tick);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof RtgBlockEntity machine)server.openMenu(machine);return InteractionResult.CONSUME;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
        if(state.is(replacement.getBlock()))return;
        if(!level.isClientSide&&level.getBlockEntity(pos) instanceof RtgBlockEntity machine){
            for(int i=0;i<machine.getContainerSize();i++){var stack=machine.getItem(i);if(stack.isEmpty())continue;float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(stack.getCount(),dropRandom.nextInt(21)+10);var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));entity.setDeltaMovement((float)dropRandom.nextGaussian()*.05F,(float)dropRandom.nextGaussian()*.05F+.2F,(float)dropRandom.nextGaussian()*.05F);level.addFreshEntity(entity);}}
            level.updateNeighbourForOutputSignal(pos,this);
        }
        super.onRemove(state,level,pos,replacement,moving);
    }
}
