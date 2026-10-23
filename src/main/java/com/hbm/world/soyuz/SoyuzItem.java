package com.hbm.world.soyuz;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Actual ItemSoyuz is a three-skin descriptive item; flight is performed by the separate real launch machinery. */
public final class SoyuzItem extends Item {
    public SoyuzItem() { super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON)); }
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0); }
    public static ItemStack stack(int metadata) {
        var stack = new ItemStack(SoyuzCapsulesContent.SOYUZ.get());
        if (metadata != 0) stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
        return stack;
    }
    public static Rarity sourceRarity(ItemStack stack) {
        return switch (metadata(stack)) { case 0 -> Rarity.UNCOMMON; case 1 -> Rarity.RARE; case 2 -> Rarity.EPIC; default -> Rarity.COMMON; };
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        String skin = switch (metadata(stack)) { case 0 -> "orig"; case 1 -> "luna"; case 2 -> "war"; default -> null; };
        if (skin == null) return;
        var color = metadata(stack) == 0 ? ChatFormatting.GOLD : metadata(stack) == 1 ? ChatFormatting.BLUE : ChatFormatting.GREEN;
        lines.add(Component.translatable("item.missile_soyuz.desc").append(" ")
                .append(Component.translatable("item.missile_soyuz." + skin + ".desc").withStyle(color)));
    }
}
