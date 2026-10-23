package com.hbm.block.sourceemitter;

import com.hbm.api.tool.ToolableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Real opaque source PartEmitter cube: only its hand-drill mode cycle, no invented activation/energy. */
public final class PartEmitterBlock extends BaseEntityBlock implements ToolableBlock {
    public PartEmitterBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5F, 12F).sound(SoundType.STONE).requiresCorrectToolForDrops()); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PartEmitterEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, SourceEmittersContent.PARTICLE_ENTITY.get(), PartEmitterEntity::tick);
    }
    @Override public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float x, float y, float z, ToolType tool) {
        if (tool != ToolType.HAND_DRILL || !(level.getBlockEntity(pos) instanceof PartEmitterEntity emitter)) return false;
        emitter.effect = (emitter.effect + 1) % 4; emitter.setChanged(); return true;
    }
}
