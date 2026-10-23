package com.hbm.block.sourcestones;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/** Actual BlockPorous extends BlockStone. Native loot preserves cobblestone / silk-touch vanilla stone. */
public final class PorousStoneBlock extends Block {
    public PorousStoneBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(1.5F,18F).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
}
