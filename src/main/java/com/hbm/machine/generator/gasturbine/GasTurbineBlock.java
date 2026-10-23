package com.hbm.machine.generator.gasturbine;

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

public final class GasTurbineBlock extends DummyableBlock {
    public GasTurbineBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends GasTurbineBlock> codec(){return simpleCodec(ignored->new GasTurbineBlock());}
    @Override public int[] dimensions(){return new int[]{2,0,1,1,4,5};}@Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new GasTurbineBlockEntity(pos,state):isPort(state)?new GasTurbinePort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,GasTurbineContent.ENTITY.get(),GasTurbineBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());for(var port:GasTurbineBlockEntity.fluidConnections(core,facing))setPort(level,port.origin(),true);setPort(level,GasTurbineBlockEntity.powerPort(core,facing),true);}
}
