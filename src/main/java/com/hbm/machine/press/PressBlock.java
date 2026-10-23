package com.hbm.machine.press;

import com.hbm.multiblock.DummyableBlock;
import com.hbm.multiblock.DummyInventoryProxy;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class PressBlock extends DummyableBlock {
    private static final MapCodec<PressBlock> CODEC = simpleCodec(ignored -> new PressBlock());
    public PressBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return CODEC; }
    @Override public int[] dimensions() { return new int[]{2,0,0,0,0,0}; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return isCore(state) ? new PressBlockEntity(pos,state) : isPort(state) ? new DummyInventoryProxy(pos,state) : null; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return createTickerHelper(type, PressContent.ENTITY.get(), PressBlockEntity::tick); }
}
