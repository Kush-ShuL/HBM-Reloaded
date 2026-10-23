package com.hbm.item.meteorunstable;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ItemStarmetal: all icons use abs(meta)%4, but only exact1/2/3 receive the italic star-name lore. */
public final class StarmetalIngotItem extends Item {
    public StarmetalIngotItem() { super(new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0)); }
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0); }
    public static int icon(ItemStack stack) { return Math.abs(metadata(stack)) % 4; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.ingot_starmetal.name"); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String suffix = switch (metadata(stack)) { case 1 -> "astra"; case 2 -> "ursa"; case 3 -> "orion"; default -> null; };
        if (suffix != null) tooltip.add(Component.translatable("tooltip.hbm.starmetal." + suffix).withStyle(ChatFormatting.ITALIC));
    }
}
