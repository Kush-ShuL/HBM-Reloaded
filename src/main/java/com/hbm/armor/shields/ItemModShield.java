package com.hbm.armor.shields;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.item.redroom.ShieldInfusion;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemModShield; contributes capacity to the existing player shield, never a second buffer. */
public final class ItemModShield extends ArmorModItem implements ShieldInfusion.ArmorCapacity {
    public final float shield;
    public ItemModShield(float shield) { super(ArmorModStorage.KEVLAR, false, true, false, false); this.shield = shield; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.australium_iii.name"); }
    @Override public float shieldCapacity(ItemStack mod) { return shield; }
    private static ChatFormatting color() { return System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.YELLOW : ChatFormatting.GOLD; }
    private String displayShield() { return Double.toString(Math.round(shield * 10) * .1D); }
    @Override public Component installedDescription(ItemStack stack) {
        // Original installed text says health, while the item tooltip correctly says shield.
        return Component.translatable("tooltip.hbm.armor_shield.installed", stack.getHoverName(), displayShield()).withStyle(color());
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.armor_shield.capacity", displayShield()).withStyle(color()));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
