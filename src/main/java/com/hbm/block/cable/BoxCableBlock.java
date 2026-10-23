package com.hbm.block.cable;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class BoxCableBlock extends NativeCableBlock {
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 4);
    private static final MapCodec<BoxCableBlock> CODEC = simpleCodec(ignored -> new BoxCableBlock());
    public BoxCableBlock() { super(CableKind.BOX); registerDefaultState(defaultBlockState().setValue(SIZE, 0)); }
    @Override protected MapCodec<BoxCableBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); builder.add(SIZE);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        int size = context.getItemInHand().getItem() instanceof CableBlockItem item ? item.size(context.getItemInHand()) : 0;
        return super.getStateForPlacement(context).setValue(SIZE, size);
    }
}
