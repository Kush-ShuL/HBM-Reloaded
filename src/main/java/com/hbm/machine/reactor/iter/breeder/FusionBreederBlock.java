package com.hbm.machine.reactor.iter.breeder;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Exact source4×5×3 Dummyable body with5 actual inventory/privatefluid extras andoffset2. */
public final class FusionBreederBlock extends DummyableBlock {
 public FusionBreederBlock(){super(Properties.of().strength(5,36).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{3,0,2,2,1,1};}@Override public int offset(){return 2;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var dir=placementFacing(state);var core=pos.relative(dir,-offset());safely(()->{for(var port:FusionBreederEntity.portConnections(core,dir))setPort(level,port.origin(),true);});}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new FusionBreederEntity(pos,state):isPort(state)?new FusionBreederPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,FusionBreederContent.ENTITY.get(),FusionBreederEntity::tick):null;}
}
