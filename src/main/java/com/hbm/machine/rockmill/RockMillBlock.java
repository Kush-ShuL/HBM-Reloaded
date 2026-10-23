package com.hbm.machine.rockmill;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Original 5x3x5 body, offset2, and eight inventory/power/private-fluid proxy cells. */
public final class RockMillBlock extends DummyableBlock {
 public RockMillBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,60).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends RockMillBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{2,0,2,2,2,2};}@Override public int offset(){return 2;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RockMillEntity(pos,state):isPort(state)?new RockMillPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RockMillContent.ENTITY.get(),RockMillEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:RockMillEntity.portDefinitions(core))setPort(level,port.position(),true);}
}
