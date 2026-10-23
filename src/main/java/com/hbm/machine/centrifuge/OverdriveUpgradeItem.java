package com.hbm.machine.centrifuge;
import net.minecraft.world.item.Item;
/** Source non-mutex OVERDRIVE upgrade, summed across slots and capped by the receiving machine. */
public final class OverdriveUpgradeItem extends Item {
    private final int tier;
    public OverdriveUpgradeItem(int tier) { super(new Properties().stacksTo(1)); this.tier=tier; }
    public int tier() { return tier; }
}
