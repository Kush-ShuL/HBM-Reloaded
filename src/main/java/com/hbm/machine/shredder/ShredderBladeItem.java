package com.hbm.machine.shredder;

import net.minecraft.world.item.Item;

/** ItemBlades: zero max damage is the original indestructible desh variant. */
public final class ShredderBladeItem extends Item {
    public ShredderBladeItem(int durability) { super(durability > 0 ? new Properties().durability(durability) : new Properties().stacksTo(1)); }
}
