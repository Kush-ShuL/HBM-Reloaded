package com.hbm.machine.trackingradar;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class TrackingRadarBlock extends DummyableBlock {
    public TrackingRadarBlock() { super(Properties.of().strength(5, 10).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends TrackingRadarBlock> codec() { return MapCodec.unit(this); }
    @Override public int[] dimensions() { return new int[]{9, 0, 1, 1, 1, 1}; }
    @Override public int offset() { return 1; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return isCore(state) ? new TrackingRadarContent.RadarEntity(pos, state) : null; }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            var core = pos.relative(placementFacing(state), -offset());
            for (int x : new int[]{-1, 1}) for (int z : new int[]{-1, 1}) setPort(level, core.offset(x, 0, z), true);
        }
    }
}
