package com.hbm.machine.electricfurnace;

import net.minecraft.world.item.Item;

/** Original single-item speed/power upgrade tiers. Machine-specific help belongs to its screen. */
public final class FurnaceUpgradeItem extends Item {
    public enum Type { SPEED, POWER }
    private final Type type;
    private final int tier;
    public FurnaceUpgradeItem(Type type, int tier) { super(new Properties().stacksTo(1)); this.type = type; this.tier = tier; }
    public Type type() { return type; }
    public int tier() { return tier; }
}
