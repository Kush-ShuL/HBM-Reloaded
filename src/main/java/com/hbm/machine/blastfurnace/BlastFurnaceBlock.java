package com.hbm.machine.blastfurnace;

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

/** Original 3x3x7 rectangular multiblock, offset1 and seven inventory/fluid proxy ports. */
public final class BlastFurnaceBlock extends DummyableBlock {
    public BlastFurnaceBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{6,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new BlastFurnaceBlockEntity(pos,state):isPort(state)?new BlastFurnacePort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,BlastFurnaceContent.ENTITY.get(),BlastFurnaceBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:BlastFurnaceBlockEntity.portPositions(core,placementFacing(state)))setPort(level,port,true);}
}
