package com.hbm.block.cokestorage;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** One real COAL, LIGNITE or PETROLEUM storage block; the type is immutable. */
public final class CokeStorageBlock extends Block {
    public static final List<String> NAMES = List.of("coal", "lignite", "petroleum");
    private final int type;

    public CokeStorageBlock(int type) {
        super(Properties.of().mapColor(MapColor.METAL).strength(5, 6)
                .sound(SoundType.METAL).requiresCorrectToolForDrops());
        if (type < 0 || type >= NAMES.size()) throw new IllegalArgumentException("Unknown coke storage type " + type);
        this.type = type;
    }

    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    public int type() { return type; }
    @Override public String getDescriptionId() { return "block.hbm.block_coke." + NAMES.get(type); }
    @Override public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 5; }
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 10; }
}
