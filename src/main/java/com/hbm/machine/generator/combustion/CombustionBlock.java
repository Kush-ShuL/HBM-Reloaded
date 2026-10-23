package com.hbm.machine.generator.combustion;

import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class CombustionBlock extends DummyableBlock {
    public CombustionBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends CombustionBlock> codec(){return simpleCodec(ignored->new CombustionBlock());}
    @Override public int[] dimensions(){return new int[]{1,0,1,0,3,2};}
    @Override public int offset(){return 0;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CombustionBlockEntity(pos,state):isPort(state)?new CombustionPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,CombustionContent.ENTITY.get(),CombustionBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide)for(var port:CombustionBlockEntity.portPositions(pos,placementFacing(state)))setPort(level,port,true);}
}
