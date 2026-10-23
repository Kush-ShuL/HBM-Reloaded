package com.hbm.machine.solidifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Actual3x4x3 dummyable with all6centered face ports. Bottom endpoint remains the real core. */
public final class SolidifierBlock extends DummyableBlock {
 public SolidifierBlock(){super(Properties.of().strength(10,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends SolidifierBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{3,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(!level.isClientSide){var core=pos.relative(placementFacing(state),-offset());for(var port:SolidifierEntity.portPositions(core))if(!port.equals(core))setPort(level,port,true);}}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new SolidifierEntity(pos,state):isPort(state)?new SolidifierPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,SolidifierContent.ENTITY.get(),SolidifierEntity::tick):null;}
}
