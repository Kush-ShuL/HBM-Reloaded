package com.hbm.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Source staircase geometry with the native StairBlock waterlogging chain. */
public final class LegacyStairsBlock extends StairBlock {
    private final MapCodec<LegacyStairsBlock> codec;

    public LegacyStairsBlock(BlockState base, Properties properties) {
        super(base, properties);
        codec = simpleCodec(value -> new LegacyStairsBlock(base, value));
    }

    @Override public MapCodec<? extends StairBlock> codec() { return codec; }
}
