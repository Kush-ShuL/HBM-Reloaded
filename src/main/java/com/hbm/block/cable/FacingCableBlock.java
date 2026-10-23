package com.hbm.block.cable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public final class FacingCableBlock extends NativeCableBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private final MapCodec<FacingCableBlock> codec;
    public FacingCableBlock(CableKind kind) {
        super(kind);
        codec = simpleCodec(ignored -> new FacingCableBlock(kind));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN));
    }
    @Override protected MapCodec<FacingCableBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); builder.add(FACING);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var player = context.getPlayer();
        Direction facing = context.getHorizontalDirection().getOpposite();
        var pos = context.getClickedPos();
        if (player != null && Math.abs(player.getX() - pos.getX()) < 2 && Math.abs(player.getZ() - pos.getZ()) < 2) {
            if (player.getEyeY() - pos.getY() > 2) facing = Direction.UP;
            else if (pos.getY() - player.getEyeY() > 0) facing = Direction.DOWN;
        }
        return super.getStateForPlacement(context).setValue(FACING, facing);
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return rotate(state, mirror.getRotation(state.getValue(FACING))); }
}
