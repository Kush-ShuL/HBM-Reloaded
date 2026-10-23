package com.hbm.machine.chemicalplant;

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

/** Original 3x3x3 dummy structure, offset1, eight bottom ports; other parts have no BE. */
public final class ChemicalPlantBlock extends DummyableBlock {
    private static final MapCodec<ChemicalPlantBlock> CODEC = simpleCodec(ignored -> new ChemicalPlantBlock());
    public ChemicalPlantBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5,18).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return CODEC; }
    @Override public int[] dimensions() { return new int[]{2,0,1,1,1,1}; }
    @Override public int offset() { return 1; }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);
        if (!level.isClientSide) for (var port : ChemicalPlantBlockEntity.portPositions(pos.relative(placementFacing(state),-offset()))) setPort(level,port,true);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return isCore(state) ? new ChemicalPlantBlockEntity(pos,state) : isPort(state) ? new ChemicalPlantPort(pos,state) : null;
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) {
        return isCore(state) ? createTickerHelper(type,ChemicalPlantContent.ENTITY.get(),ChemicalPlantBlockEntity::tick) : null;
    }
}
