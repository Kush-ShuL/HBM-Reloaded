package com.hbm.machine.radgen;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Actual MachineRadGen XR{2,0,3,2,1,1}/offset2 and the source three inventory/power proxy cells. */
public final class RadGenBlock extends DummyableBlock {
    public RadGenBlock() { super(Properties.of().strength(5, 6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends RadGenBlock> codec() { return MapCodec.unit(this); }
    @Override public int[] dimensions() { return new int[]{2, 0, 3, 2, 1, 1}; }
    @Override public int offset() { return 2; }
    public static BlockPos[] ports(BlockPos core, Direction facing) {
        var rot = facing.getClockWise(); // Exact Forge1.7.10 UP rotation.
        return new BlockPos[]{core.relative(facing, -3), core.relative(rot), core.relative(rot.getOpposite())};
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
        super.setPlacedBy(level, pos, state, player, stack);
        if (!level.isClientSide) { var facing = placementFacing(state); var core = pos.relative(facing, -offset()); for (var port : ports(core, facing)) setPort(level, port, true); }
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return isCore(state) ? new RadGenEntity(pos, state) : isPort(state) ? new RadGenProxy(pos, state) : null; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return isCore(state) ? createTickerHelper(type, RadGenContent.ENTITY.get(), RadGenEntity::tick) : null; }
}
