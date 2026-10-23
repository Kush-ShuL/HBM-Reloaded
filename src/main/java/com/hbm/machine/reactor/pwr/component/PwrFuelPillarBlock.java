package com.hbm.machine.reactor.pwr.component;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/** Original BlockPillarPWR extends BlockPillar: fixed top/bottom, no rotated axis, tile, fission or temperature simulation. */
public final class PwrFuelPillarBlock extends Block {
    public PwrFuelPillarBlock(){this(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    private PwrFuelPillarBlock(Properties properties){super(properties);}
    @Override protected MapCodec<? extends PwrFuelPillarBlock> codec(){return simpleCodec(PwrFuelPillarBlock::new);}
}
