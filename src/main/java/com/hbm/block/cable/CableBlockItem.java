package com.hbm.block.cable;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class CableBlockItem extends BlockItem {
    public CableBlockItem(NativeCableBlock block) { super(block, new Properties()); }
    public int size(ItemStack stack) { return Math.floorMod(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0), 5); }
    public ItemStack stack(int size) {
        var stack = new ItemStack(this);
        stack.set(HbmHazards.LEGACY_METADATA.get(), Math.floorMod(size, 5));
        return stack;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        if (((NativeCableBlock) getBlock()).kind() == CableKind.DIODE)
            text.add(Component.literal("Limits throughput and restricts flow direction").withStyle(ChatFormatting.GOLD));
    }
}
