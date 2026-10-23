package com.hbm.item.rttycard;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original tier-2 card tooltip; no hand-use GUI or independent reader machine. */
public final class RttyCardItem extends Item {
    public RttyCardItem() { super(new Properties().stacksTo(1)); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        text.add(Component.translatable("item.rtty_card.desc"));
        var custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom != null) {
            var node = custom.copyTag().getCompound("oc:data").getCompound("node");
            if (node.contains("address", 8)) text.add(Component.literal(node.getString("address")).withStyle(ChatFormatting.DARK_GRAY));
        }
        text.add(Component.literal("Tier 2").withStyle(ChatFormatting.DARK_GRAY));
    }
}
