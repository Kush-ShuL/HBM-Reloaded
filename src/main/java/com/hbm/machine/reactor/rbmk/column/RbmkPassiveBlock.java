package com.hbm.machine.reactor.rbmk.column;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class RbmkPassiveBlock extends RbmkColumnBlock {
    public final RbmkColumnKind kind;
    public RbmkPassiveBlock(RbmkColumnKind kind) { this.kind = kind; }
    @Override protected MapCodec<? extends RbmkPassiveBlock> codec() { return MapCodec.unit(this); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return isCore(state) ? new RbmkPassiveEntity(pos, state) : null; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) { return isCore(state) ? createTickerHelper(type, RbmkColumnsContent.PASSIVE.get(), RbmkPassiveEntity::tick) : null; }
}
