package com.hbm.item.battery;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.EnumMultiItem;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** X5846 ItemBatterySC is a constant-output radiovoltaic device; charge mutations are no-ops. */
public final class SelfChargingBatteryItem extends EnumMultiItem implements HbmBattery {
    public enum EnumBatterySC {
        EMPTY(0), WASTE(150), RA226(200), TC99(500), CO60(750), PU238(1_000), PO210(1_250), AU198(1_500), PB209(2_000), AM241(2_500);
        public final long power;
        EnumBatterySC(long power) { this.power = power; }
    }
    public SelfChargingBatteryItem() {
        super("battery_sc", 1, true, Arrays.stream(EnumBatterySC.values()).map(value -> value.name().toLowerCase(Locale.US)).toList(), IntStream.range(0, EnumBatterySC.values().length).boxed().toList());
    }
    public EnumBatterySC variant(ItemStack stack) { return EnumBatterySC.values()[ordinal(stack)]; }
    public ItemStack stack(EnumBatterySC variant) { ItemStack stack = new ItemStack(this); stack.set(HbmHazards.LEGACY_METADATA.get(), variant.ordinal()); return stack; }
    @Override public void chargeBattery(ItemStack stack, long amount) {}
    @Override public void setCharge(ItemStack stack, long amount) {}
    @Override public void dischargeBattery(ItemStack stack, long amount) {}
    @Override public long getChargeRate(ItemStack stack) { return 0; }
    @Override public long getCharge(ItemStack stack) { return getMaxCharge(stack); }
    @Override public long getDischargeRate(ItemStack stack) { return getMaxCharge(stack); }
    @Override public long getMaxCharge(ItemStack stack) { return variant(stack).power; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        long power = variant(stack).power;
        if (power > 0) tooltip.add(Component.translatable("tooltip.hbm.battery.discharge_rate", BatteryItem.shortNumber(power)).withStyle(ChatFormatting.YELLOW));
        for (String line : Component.translatable("item.battery_sc.desc").getString().split("\\$")) tooltip.add(Component.literal(line).withStyle(ChatFormatting.RED));
    }
}
