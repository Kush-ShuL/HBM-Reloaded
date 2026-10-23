package com.hbm.machine.chemicalfactory;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Original5x5x3 filled assembly and26 physical proxy positions (30 outward faces). */
public final class ChemicalFactoryBlock extends DummyableBlock {
 private static final MapCodec<ChemicalFactoryBlock> CODEC=simpleCodec(ignored->new ChemicalFactoryBlock());
 public ChemicalFactoryBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,18).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{2,0,2,2,2,2};}@Override public int offset(){return 2;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ChemicalFactoryEntity(pos,state):isPort(state)?new ChemicalFactoryPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ChemicalFactoryContent.ENTITY.get(),ChemicalFactoryEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:ChemicalFactoryEntity.powerDefinitions(core,placementFacing(state)))setPort(level,port.position(),true);}
}
