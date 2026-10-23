package com.hbm.item.battery;

import net.minecraft.world.item.ItemStack;

/** X5846 IBatteryItem contract, in native long HE and HE/t. */
public interface HbmBattery {
    void chargeBattery(ItemStack stack, long amount);
    void setCharge(ItemStack stack, long amount);
    void dischargeBattery(ItemStack stack, long amount);
    long getCharge(ItemStack stack);
    long getMaxCharge(ItemStack stack);
    long getChargeRate(ItemStack stack);
    long getDischargeRate(ItemStack stack);
}
