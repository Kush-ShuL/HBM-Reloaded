package com.hbm.machine.ammopress;
import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Original three-wide/two-high body with inventory-only proxy cells on every source dummy. */
public final class AmmoPressBlock extends DummyableBlock {
 public AmmoPressBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{1,0,0,0,1,1};}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new AmmoPressEntity(pos,state):new DummyInventoryProxy(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,AmmoPressContent.ENTITY.get(),AmmoPressEntity::tick):null;}
}
