package com.hbm.block.ore;

import java.util.List;
import java.util.stream.IntStream;
import com.hbm.item.EnumMultiItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemDrillbit data, names, one-item stacks and tooltip values. */
public final class DrillbitItem extends EnumMultiItem {
    public static final List<String> NAMES = List.of("steel", "steel_diamond", "hss", "hss_diamond", "desh", "desh_diamond", "tcalloy", "tcalloy_diamond", "ferro", "ferro_diamond");
    private static final double[] SPEED = {1, 1, 1.2, 1.2, 1.5, 1.5, 2, 2, 2.5, 2.5};
    private static final int[] FORTUNE = {0, 2, 0, 3, 1, 4, 1, 4, 1, 4};
    public DrillbitItem() { super("drillbit", 1, false, NAMES, IntStream.range(0, 10).boxed().toList()); }
    public int tier(ItemStack stack) { return ordinal(stack) / 2 + 1; }
    public double speed(ItemStack stack) { return SPEED[ordinal(stack)]; }
    public int fortune(ItemStack stack) { return FORTUNE[ordinal(stack)]; }
    public boolean vein(ItemStack stack) { return ordinal(stack) >= 2; }
    public boolean silk(ItemStack stack) { int index = ordinal(stack); return index == 1 || index >= 3; }
    @Override public String getDescriptionId(ItemStack stack) { return "item.hbm.drillbit_" + NAMES.get(ordinal(stack)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable(getDescriptionId(stack)); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.hbm.drillbit.speed", (int) (speed(stack) * 100)).withStyle(ChatFormatting.YELLOW));
        lines.add(Component.translatable("tooltip.hbm.drillbit.tier", tier(stack)).withStyle(ChatFormatting.YELLOW));
        if (fortune(stack) > 0) lines.add(Component.translatable("tooltip.hbm.drillbit.fortune", fortune(stack)).withStyle(ChatFormatting.LIGHT_PURPLE));
        if (vein(stack)) lines.add(Component.translatable("tooltip.hbm.drillbit.vein").withStyle(ChatFormatting.GREEN));
        if (silk(stack)) lines.add(Component.translatable("tooltip.hbm.drillbit.silk").withStyle(ChatFormatting.GREEN));
    }
}
