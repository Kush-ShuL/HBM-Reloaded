package com.hbm.machine.rotaryfurnace;
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
/** Original3x5x5 full dummy body and seven distinct physical inventory/fluid proxy cells. */
public final class RotaryFurnaceBlock extends DummyableBlock {
 public RotaryFurnaceBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends RotaryFurnaceBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{4,0,1,1,2,2};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RotaryFurnaceEntity(pos,state):isPort(state)?new RotaryFurnacePort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RotaryFurnaceContent.ENTITY.get(),RotaryFurnaceEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:RotaryFurnaceEntity.blockPorts(core,placementFacing(state)))setPort(level,port.position(),true);}
}
