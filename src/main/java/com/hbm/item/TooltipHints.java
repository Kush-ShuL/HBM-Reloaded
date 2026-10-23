package com.hbm.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Shared source left-shift hint; text follows the active language while key emphasis is retained. */
public final class TooltipHints {
    public static Component moreInfo() {
        return Component.translatable("tooltip.hbm.more_info",
                Component.translatable("tooltip.hbm.left_shift").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC))
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
    }

    private TooltipHints() {}
}
