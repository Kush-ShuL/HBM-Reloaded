package com.hbm.block.lantern;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** X5846 ordinary BlockLantern, not the damaged/repairable Old Lantern. All five cells emit15. */
public final class LanternBlock extends DummyableBlock {
    private static final MapCodec<LanternBlock> CODEC = simpleCodec(ignored -> new LanternBlock());
    public LanternBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(3F).sound(SoundType.METAL)
            .requiresCorrectToolForDrops().lightLevel(state -> 15).noOcclusion()); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return CODEC; }
    @Override public int[] dimensions() { return new int[]{4, 0, 0, 0, 0, 0}; }
    @Override public int offset() { return 0; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return isCore(state) ? new LanternEntity(pos, state) : null; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && isCore(state) ? createTickerHelper(type, LanternContent.ENTITY.get(), LanternEntity::tick) : null;
    }
}
