package com.hbm.block.masonry;

import net.minecraft.world.level.block.Block;

/** X5846 BlockSpeedy(rock,1.5): original client-side movement multiplier. */
public final class AsphaltBlock extends Block {
    public AsphaltBlock() { this(Properties.of().strength(15,72).requiresCorrectToolForDrops()); }
    public AsphaltBlock(Properties properties) { super(properties); }
}
