package com.hbm.block.cable;

import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class SwitchedCableBlock extends NativeCableBlock {
    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
    private final MapCodec<SwitchedCableBlock> codec;
    public SwitchedCableBlock(CableKind kind) {
        super(kind);
        codec = simpleCodec(ignored -> new SwitchedCableBlock(kind));
        registerDefaultState(defaultBlockState().setValue(ENABLED, false));
    }
    @Override protected MapCodec<SwitchedCableBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); builder.add(ENABLED);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (kind() != CableKind.SWITCH || player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!level.isClientSide) setActive(level, pos, state, !state.getValue(ENABLED));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block adjacent, BlockPos adjacentPos, boolean moving) {
        super.neighborChanged(state, level, pos, adjacent, adjacentPos, moving);
        if (!level.isClientSide && kind() == CableKind.DETECTOR) setActive(level, pos, state, level.hasNeighborSignal(pos));
    }
    public void setActive(Level level, BlockPos pos, BlockState state, boolean enabled) {
        if (state.getValue(ENABLED) == enabled) return;
        level.setBlock(pos, state.setValue(ENABLED, enabled), Block.UPDATE_CLIENTS);
        level.playSound(null, pos, HbmSounds.get("block.reactorStart"), SoundSource.BLOCKS, 1F, enabled ? 1F : .85F);
        if (level.getBlockEntity(pos) instanceof NativeCableBlockEntity cable) cable.updateConductingState();
    }
}
