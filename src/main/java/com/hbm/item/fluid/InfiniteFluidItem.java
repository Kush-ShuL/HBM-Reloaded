package com.hbm.item.fluid;

import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import net.minecraft.world.item.Item;

/** Original fixed type (or any type), amount, chance and pressure exception. */
public final class InfiniteFluidItem extends Item {
    private final String type;
    private final int amount, chance;
    private final boolean allowPressure;
    public InfiniteFluidItem(String type, int amount, int chance, int stackSize, boolean allowPressure) {
        super(new Properties().stacksTo(stackSize)); this.type = type; this.amount = amount; this.chance = chance; this.allowPressure = allowPressure;
    }
    public HbmFluidType getType() { return type == null ? null : HbmFluids.fromName(type); }
    public int getAmount() { return amount; }
    public int getChance() { return chance; }
    public boolean allowPressure(int pressure) { return allowPressure || pressure == 0; }
}
