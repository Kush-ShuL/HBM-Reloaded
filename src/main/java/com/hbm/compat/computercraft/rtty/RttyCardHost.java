package com.hbm.compat.computercraft.rtty;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/** One actual Pocket/Turtle upgrade slot; components are owned and saved by CC, never by a second database. */
interface RttyCardHost {
    ServerLevel level();
    ItemStack card();
    void save(ItemStack card);
}
