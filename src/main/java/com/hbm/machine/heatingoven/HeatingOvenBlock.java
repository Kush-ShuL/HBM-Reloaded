package com.hbm.machine.heatingoven;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Original HeaterOven3x1x3, offset1; no fabricated multi-capability or heat proxies. */
public final class HeatingOvenBlock extends DummyableBlock {
 public HeatingOvenBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{0,0,1,1,1,1};}@Override public int offset(){return 1;}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new HeatingOvenBlockEntity(pos,state):new HeatingOvenProxy(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,HeatingOvenContent.ENTITY.get(),HeatingOvenBlockEntity::tick):null;}
}
