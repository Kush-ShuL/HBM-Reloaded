package com.hbm.machine.chimney;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class ChimneyBlock extends DummyableBlock {
 public final boolean industrial;public ChimneyBlock(boolean industrial){super(Properties.of().strength(5,60).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.industrial=industrial;}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{industrial?22:12,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ChimneyBlockEntity(pos,state):isPort(state)?new ChimneyProxy(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ChimneyContent.ENTITY.get(),ChimneyBlockEntity::tick):null;}
 @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){return net.minecraft.world.InteractionResult.PASS;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var direction:Direction.Plane.HORIZONTAL)setPort(level,core.relative(direction),true);}
}
