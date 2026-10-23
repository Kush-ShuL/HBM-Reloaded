package com.hbm.machine.reactor.iter.auxiliary;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Actual two source Dummyable geometries, no invented GUI/energy/fluid ports. */
public final class FusionAuxiliaryBlock extends DummyableBlock {
 public final boolean collector;
 public FusionAuxiliaryBlock(boolean collector){super(Properties.of().strength(5,36).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());this.collector=collector;}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return collector?new int[]{3,0,2,1,2,2}:new int[]{3,0,1,1,1,1};}@Override public int offset(){return collector?1:0;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?collector?new FusionCollectorEntity(pos,state):new FusionCouplerEntity(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){if(!isCore(state)||level.isClientSide)return null;return collector?createTickerHelper(type,FusionAuxiliaryContent.COLLECTOR_ENTITY.get(),FusionCollectorEntity::tick):createTickerHelper(type,FusionAuxiliaryContent.COUPLER_ENTITY.get(),FusionCouplerEntity::tick);}
}
