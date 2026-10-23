package com.hbm.armor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Only the five original ItemModCladding items supply this resistance exponent. */
public final class ArmorCladdingItem extends ArmorModItem {
    public final double radiation;
    public ArmorCladdingItem(double radiation) { super(ArmorModStorage.CLADDING,true,true,true,true);this.radiation=radiation; }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.armor_cladding.radiation",radiation).withStyle(ChatFormatting.YELLOW));lines.add(Component.empty());super.appendHoverText(stack,context,lines,flags);
    }
}
