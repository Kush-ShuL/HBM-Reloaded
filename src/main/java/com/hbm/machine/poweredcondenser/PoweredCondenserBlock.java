package com.hbm.machine.poweredcondenser;
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
public final class PoweredCondenserBlock extends DummyableBlock {
 public PoweredCondenserBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends PoweredCondenserBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{2,0,1,1,3,3};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new PoweredCondenserEntity(pos,state):isPort(state)?new PoweredCondenserPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,PoweredCondenserContent.ENTITY.get(),PoweredCondenserEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var dir=placementFacing(state);var core=pos.relative(dir,-offset());for(var port:PoweredCondenserEntity.connections(core,dir))setPort(level,port.origin(),true);}
}
