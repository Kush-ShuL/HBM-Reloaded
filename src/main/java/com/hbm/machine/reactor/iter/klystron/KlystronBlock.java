package com.hbm.machine.reactor.iter.klystron;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Actual source two-rectangle body:5×8×4base plus3×8 uppercap, offset3. */
public final class KlystronBlock extends DummyableBlock {
    private static final int[] TOP={4,-3,4,3,1,1};
    public KlystronBlock(){super(Properties.of().strength(5,36).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{3,0,4,3,2,2};}@Override public int offset(){return 3;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var bounds=rotate(TOP,placementFacing(state));for(var pos:BlockPos.betweenClosed(core.offset(-bounds[4],-bounds[1],-bounds[2]),core.offset(bounds[5],bounds[0],bounds[3])))if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;return state;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());var bounds=rotate(TOP,facing);safely(()->{for(var part:BlockPos.betweenClosed(core.offset(-bounds[4],4,-bounds[2]),core.offset(bounds[5],4,bounds[3])))level.setBlock(part,defaultBlockState().setValue(LEGACY_META,Direction.UP.get3DDataValue()),UPDATE_ALL);for(var connection:KlystronEntity.portConnections(core,facing))setPort(level,connection.origin(),true);});}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new KlystronEntity(pos,state):isPort(state)?new KlystronPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,KlystronContent.ENTITY.get(),KlystronEntity::tick):null;}
}
