package com.hbm.machine.mininglaser;

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

/** X5846 ceiling-mounted 3x3x3: core one block below the clicked cell, four combo and one power-only proxies. */
public final class MiningLaserBlock extends DummyableBlock {
    public MiningLaserBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{1,1,1,1,1,1};}
    @Override public int heightOffset(){return -1;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack item){
        super.setPlacedBy(level,pos,state,player,item);
        if(level.isClientSide)return;
        BlockPos core=pos.below();
        // rotatePorts leaves this symmetric union unchanged for every horizontal placement.
        for(var side:net.minecraft.core.Direction.Plane.HORIZONTAL)setPort(level,core.relative(side),true);
        setPort(level,core.above(),true);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new MiningLaserEntity(pos,state):isPort(state)?new MiningLaserPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,MiningLaserContent.ENTITY.get(),MiningLaserEntity::tick):null;}
}
