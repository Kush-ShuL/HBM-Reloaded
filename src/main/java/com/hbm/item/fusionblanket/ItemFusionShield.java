package com.hbm.item.fusionblanket;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** The still-registered X5846 item; no removed fusion-machine behavior is restored. */
public final class ItemFusionShield extends Item {
    public long maxDamage;
    public int maxTemp;

    public ItemFusionShield(long maxDamage, int maxTemp) {
        super(new Properties().stacksTo(1));
        this.maxDamage = maxDamage;
        this.maxTemp = maxTemp;
    }

    public static long getShieldDamage(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            // Source creates an empty NBT compound on the first read.
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag()));
            return 0;
        }
        return data.copyTag().getLong("damage");
    }

    public static void setShieldDamage(ItemStack stack, long damage) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putLong("damage", damage);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) getShieldDamage(stack) / (double) maxDamage;
    }

    @Override public boolean isBarVisible(ItemStack stack) { return getDurabilityForDisplay(stack) != 0; }
    @Override public int getBarWidth(ItemStack stack) { return (int) Math.round(13 - getDurabilityForDisplay(stack) * 13); }
    @Override public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb((float) (1 - getDurabilityForDisplay(stack)) / 3, 1, 1);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        long damage = getShieldDamage(stack);
        long remaining = maxDamage - damage;
        // Preserve source long arithmetic, truncation and unbounded stored damage.
        int percent = (int) (remaining * 100 / maxDamage);
        lines.add(Component.translatable("tooltip.hbm.fusion_blanket.durability", remaining, maxDamage, percent));
        lines.add(Component.translatable("tooltip.hbm.fusion_blanket.melting",
            Component.literal(maxTemp + "°C").withStyle(ChatFormatting.RED)));
    }
}
