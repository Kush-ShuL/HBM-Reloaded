package com.hbm.machine.teleport;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/** The actual source anchor is a passive full cube; its receiver, storage and interaction are in ItemAnchorRemote. */
public final class TeleanchorBlock extends Block {
    public TeleanchorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
}
