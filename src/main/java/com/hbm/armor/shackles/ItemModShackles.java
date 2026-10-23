package com.hbm.armor.shackles;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source undamageable leg attachment: raw item damage counts revives, not durability. */
public final class ItemModShackles extends ArmorModItem {
    public ItemModShackles() { super(ArmorModStorage.EXTRA, false, false, true, false); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.shackles.name"); }
    // Native defaults clamp damage to maxDamage(0). Keep the original raw counter without making the item breakable.
    @Override public int getDamage(ItemStack stack) {
        return stack.getOrDefault(DataComponents.DAMAGE, stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0));
    }
    @Override public void setDamage(ItemStack stack, int value) { stack.set(DataComponents.DAMAGE, Math.max(0, value)); }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.shackles.installed", stack.getHoverName()).withStyle(ChatFormatting.GOLD);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.shackles.speak").withStyle(ChatFormatting.RED));
        lines.add(Component.translatable("tooltip.hbm.shackles.eat").withStyle(ChatFormatting.RED));
        lines.add(Component.translatable("tooltip.hbm.shackles.die").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        lines.add(Component.empty());
        lines.add(Component.translatable("tooltip.hbm.shackles.remaining").withStyle(ChatFormatting.GOLD));
        lines.add(Component.empty());
        super.appendHoverText(stack, context, lines, flags);
    }
}
