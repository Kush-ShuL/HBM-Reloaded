package com.hbm.item.battery;

import com.hbm.HbmReloaded;
import net.minecraft.world.item.Item;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** X5846 ItemBatteryPack: declaration-order metadata, initially empty, native long HE. */
public final class BatteryPackItem extends Item implements HbmBattery {
    public enum EnumBatteryPack {
        BATTERY_REDSTONE("battery_redstone", 100L, false),
        BATTERY_LEAD("battery_lead", 1_000L, false),
        BATTERY_LITHIUM("battery_lithium", 10_000L, false),
        BATTERY_SODIUM("battery_sodium", 50_000L, false),
        BATTERY_SCHRABIDIUM("battery_schrabidium", 250_000L, false),
        BATTERY_QUANTUM("battery_quantum", 1_000_000L, 20L * 60 * 60),
        CAPACITOR_COPPER("capacitor_copper", 1_000L, true),
        CAPACITOR_GOLD("capacitor_gold", 10_000L, true),
        CAPACITOR_NIOBIUM("capacitor_niobium", 100_000L, true),
        CAPACITOR_TANTALUM("capacitor_tantalum", 500_000L, true),
        CAPACITOR_BISMUTH("capacitor_bismuth", 2_500_000L, true),
        CAPACITOR_SPARK("capacitor_spark", 10_000_000L, true);

        public final ResourceLocation texture;
        public final long capacity;
        public final long chargeRate;
        public final long dischargeRate;
        EnumBatteryPack(String texture, long dischargeRate, boolean capacitor) {
            this(texture, capacitor ? dischargeRate * 20 * 30 : dischargeRate * 20 * 60 * 15,
                    capacitor ? dischargeRate : dischargeRate * 10, dischargeRate);
        }
        EnumBatteryPack(String texture, long dischargeRate, long duration) { this(texture, dischargeRate * duration, dischargeRate * 10, dischargeRate); }
        EnumBatteryPack(String texture, long capacity, long chargeRate, long dischargeRate) {
            this.texture = HbmReloaded.id("textures/models/machines/" + texture + ".png");
            this.capacity = capacity; this.chargeRate = chargeRate; this.dischargeRate = dischargeRate;
        }
        public boolean isCapacitor() { return ordinal() > BATTERY_QUANTUM.ordinal(); }
        public ItemStack stack() { return BatteryContent.pack(this); }
    }

    private final EnumBatteryPack type;
    public BatteryPackItem(EnumBatteryPack type) {super(new Properties().stacksTo(1));this.type=java.util.Objects.requireNonNull(type);}
    public EnumBatteryPack type() {return type;}
    public EnumBatteryPack variant(ItemStack stack) {return type;}
    @Override public String getDescriptionId(ItemStack stack) {return "item.hbm.battery_pack_"+type.name().toLowerCase(java.util.Locale.ROOT);}
    @Override public Component getName(ItemStack stack) {return Component.translatable("item.battery_pack."+type.name().toLowerCase(java.util.Locale.ROOT)+".name");}
    private long rawCharge(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLong("charge"); }
    @Override public void chargeBattery(ItemStack stack, long amount) { setCharge(stack, rawCharge(stack) + amount); }
    @Override public void setCharge(ItemStack stack, long amount) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        data.putLong("charge", amount); stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }
    @Override public void dischargeBattery(ItemStack stack, long amount) {
        if (stack.has(DataComponents.CUSTOM_DATA)) setCharge(stack, rawCharge(stack) - amount);
        else setCharge(stack, 0); // Source's uninitialized packs are empty, even on the first discharge call.
    }
    @Override public long getCharge(ItemStack stack) { if (!stack.has(DataComponents.CUSTOM_DATA)) setCharge(stack, 0); return rawCharge(stack); }
    @Override public long getMaxCharge(ItemStack stack) { return variant(stack).capacity; }
    @Override public long getChargeRate(ItemStack stack) { return variant(stack).chargeRate; }
    @Override public long getDischargeRate(ItemStack stack) { return variant(stack).dischargeRate; }
    @Override public boolean isBarVisible(ItemStack stack) { return 1D - (double) getCharge(stack) / getMaxCharge(stack) != 0; }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13F * (float) ((double) getCharge(stack) / getMaxCharge(stack))); }
    @Override public int getBarColor(ItemStack stack) { return net.minecraft.util.Mth.hsvToRgb(Math.max(0F, (float) ((double) getCharge(stack) / getMaxCharge(stack))) / 3F, 1F, 1F); }

    public static ItemStack makeEmptyBattery(ItemStack stack) { return initialize(stack, 0); }
    public static ItemStack makeFullBattery(ItemStack stack) { return initialize(stack, ((BatteryPackItem) stack.getItem()).getMaxCharge(stack)); }
    private static ItemStack initialize(ItemStack stack, long charge) {
        CompoundTag data = new CompoundTag(); data.putLong("charge", charge);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data)); return stack;
    }
    public List<ItemStack> creativeStacks() {return List.of(makeEmptyBattery(new ItemStack(this)),makeFullBattery(new ItemStack(this)));}
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        long capacity = getMaxCharge(stack), chargeRate = getChargeRate(stack), dischargeRate = getDischargeRate(stack);
        // The source tooltip shows full charge before an untagged pack is initialized; retain that behavior.
        long charge = stack.has(DataComponents.CUSTOM_DATA) ? getCharge(stack) : capacity;
        tooltip.add(Component.translatable("tooltip.hbm.battery.energy_stored_percent", BatteryItem.shortNumber(charge), BatteryItem.shortNumber(capacity), charge * 1000 / capacity / 10D).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.hbm.battery.charge_rate", BatteryItem.shortNumber(chargeRate)).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.hbm.battery.discharge_rate", BatteryItem.shortNumber(dischargeRate)).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.hbm.battery.full_charge_time", capacity / chargeRate / 20 / 60D).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.hbm.battery.discharge_duration", capacity / dischargeRate / 20 / 60D).withStyle(ChatFormatting.GOLD));
    }
}
