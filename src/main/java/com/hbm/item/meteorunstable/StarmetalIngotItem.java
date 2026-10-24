package com.hbm.item.meteorunstable;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Four fixed source ItemStarmetal icons; only the original1/2/3 kinds have italic star-name lore. */
public final class StarmetalIngotItem extends Item {
    private final int variant;
    public StarmetalIngotItem(int variant) { super(new Properties()); if (variant < 0 || variant > 3) throw new IllegalArgumentException("Not an actual source Starmetal kind: " + variant); this.variant = variant; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.ingot_starmetal.name"); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String suffix = switch (variant) { case 1 -> "astra"; case 2 -> "ursa"; case 3 -> "orion"; default -> null; };
        if (suffix != null) tooltip.add(Component.translatable("tooltip.hbm.starmetal." + suffix).withStyle(ChatFormatting.ITALIC));
    }
}
