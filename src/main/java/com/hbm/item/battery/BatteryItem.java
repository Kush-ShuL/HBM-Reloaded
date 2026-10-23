package com.hbm.item.battery;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** Direct rewrite of ItemBattery: an untagged newly created battery starts full. */
public class BatteryItem extends Item implements HbmBattery {
    private final long capacity;
    private final long chargeRate;
    private final long dischargeRate;

    public BatteryItem(long capacity, long chargeRate, long dischargeRate) {
        super(new Properties().stacksTo(1));
        this.capacity = capacity;
        this.chargeRate = chargeRate;
        this.dischargeRate = dischargeRate;
    }

    @Override public void chargeBattery(ItemStack stack, long amount) { setCharge(stack, rawCharge(stack, 0) + amount); }
    @Override public void dischargeBattery(ItemStack stack, long amount) { setCharge(stack, rawCharge(stack, capacity) - amount); }

    private long rawCharge(ItemStack stack, long untaggedValue) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? untaggedValue : data.copyTag().getLong("charge");
    }

    @Override
    public void setCharge(ItemStack stack, long amount) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        data.putLong("charge", amount);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    @Override
    public long getCharge(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) setCharge(stack, capacity);
        return rawCharge(stack, capacity);
    }

    @Override public long getMaxCharge(ItemStack stack) { return capacity; }
    @Override public long getChargeRate(ItemStack stack) { return chargeRate; }
    @Override public long getDischargeRate(ItemStack stack) { return dischargeRate; }
    @Override public boolean isBarVisible(ItemStack stack) { return true; }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13F * (float) ((double) getCharge(stack) / capacity)); }
    @Override public int getBarColor(ItemStack stack) { return net.minecraft.util.Mth.hsvToRgb(Math.max(0F, (float) ((double) getCharge(stack) / capacity)) / 3F, 1F, 1F); }

    public ItemStack emptyStack() { ItemStack stack = new ItemStack(this); setCharge(stack, 0); return stack; }
    public ItemStack fullStack() { ItemStack stack = new ItemStack(this); setCharge(stack, capacity); return stack; }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.hbm.battery.energy_stored", shortNumber(getCharge(stack)), shortNumber(capacity)));
        tooltip.add(Component.translatable("tooltip.hbm.battery.charge_rate", shortNumber(chargeRate)));
        tooltip.add(Component.translatable("tooltip.hbm.battery.discharge_rate", shortNumber(dischargeRate)));
    }

    public static String shortNumber(long value) {
        long abs = Math.abs(value);
        String[] suffixes = {"k", "M", "G", "T", "P", "E"};
        for (int exponent = 18; exponent >= 3; exponent -= 3) {
            if (abs >= Math.pow(10, exponent)) {
                double result = value / Math.pow(10, exponent);
                double rounding = result <= -100 ? 10 : 100;
                return Math.round(result * rounding) / rounding + suffixes[exponent / 3 - 1];
            }
        }
        return Long.toString(value);
    }
}
