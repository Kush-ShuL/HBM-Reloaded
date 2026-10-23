package com.hbm.machine.oilmixer;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** The actual three occupied cells, with a native endpoint on the source top port. */
public final class MixerBlock extends DummyableBlock {
 public MixerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,18).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new MixerBlock());}@Override public int[] dimensions(){return new int[]{2,0,0,0,0,0};}@Override public int offset(){return 0;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide)setPort(level,pos.above(2),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new MixerEntity(pos,state):isPort(state)?new MixerPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,MixerContent.ENTITY.get(),MixerEntity::tick):null;}
}
