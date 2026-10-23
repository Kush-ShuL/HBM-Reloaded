package com.hbm.armor.cladding;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source obsidian cladding affects the actual player-tossed armor entity, not equipped durability. */
public final class ItemModObsidian extends ArmorModItem {
    public ItemModObsidian() { super(ArmorModStorage.CLADDING, true, true, true, true); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.cladding_obsidian.name"); }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.armor_cladding.obsidian_installed", stack.getHoverName()).withStyle(ChatFormatting.DARK_PURPLE);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.armor_cladding.obsidian").withStyle(ChatFormatting.DARK_PURPLE));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
