package com.hbm.machine.refinery.deco;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** X5846 BlockDecoCT: same-block connectivity, plus the source steel/rusty-steel pair. */
public final class DecoSteelBlock extends Block {
    private static final MapCodec<DecoSteelBlock> CODEC = simpleCodec(DecoSteelBlock::new);
    public DecoSteelBlock(Properties properties) { super(properties); }
    public DecoSteelBlock() { this(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops()); }
    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
    public boolean canConnect(BlockState other) {
        Block block = other.getBlock();
        return this == block
                || this == DecoSteelContent.STEEL.get() && block == DecoSteelContent.RUSTY_STEEL.get()
                || this == DecoSteelContent.RUSTY_STEEL.get() && block == DecoSteelContent.STEEL.get();
    }
}
