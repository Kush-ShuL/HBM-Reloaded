package com.hbm.item.battery;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** ItemBatteryCreative: infinite supply; mutations intentionally have no effect. */
public final class CreativeBatteryItem extends Item implements HbmBattery {
    public CreativeBatteryItem() { super(new Properties().stacksTo(1)); }
    @Override public void chargeBattery(ItemStack stack, long amount) {}
    @Override public void setCharge(ItemStack stack, long amount) {}
    @Override public void dischargeBattery(ItemStack stack, long amount) {}
    @Override public long getCharge(ItemStack stack) { return Long.MAX_VALUE / 2L; }
    @Override public long getMaxCharge(ItemStack stack) { return Long.MAX_VALUE; }
    @Override public long getChargeRate(ItemStack stack) { return Long.MAX_VALUE / 100L; }
    @Override public long getDischargeRate(ItemStack stack) { return Long.MAX_VALUE / 100L; }
}
