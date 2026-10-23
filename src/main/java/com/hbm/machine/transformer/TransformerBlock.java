package com.hbm.machine.transformer;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/** Exact X5846 MachineTransformer: solid static component, distinct top/bottom and side textures. */
public final class TransformerBlock extends Block {
    private static final MapCodec<TransformerBlock> CODEC=simpleCodec(TransformerBlock::new);
    public TransformerBlock(){this(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    public TransformerBlock(Properties properties){super(properties);}
    @Override protected MapCodec<? extends Block> codec(){return CODEC;}
}
