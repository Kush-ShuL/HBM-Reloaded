package com.hbm.machine.ashpit;
import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class AshpitBlock extends DummyableBlock {
 public AshpitBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{0,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new AshpitBlockEntity(pos,state):new AshpitProxy(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,AshpitContent.ENTITY.get(),AshpitBlockEntity::tick):null;}
}
