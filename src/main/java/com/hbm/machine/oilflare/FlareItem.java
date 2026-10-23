package com.hbm.machine.oilflare;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source fuel-efficiency information, translated without changing the source figures. */
public final class FlareItem extends BlockItem {
    public FlareItem(FlareBlock block){super(block,new Item.Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("desc.hbm.flare.function").withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("desc.hbm.flare.burn",Component.literal("10mB/t").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("desc.hbm.flare.vent",Component.literal("50mB/t").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.GOLD));lines.add(Component.empty());lines.add(Component.translatable("desc.hbm.flare.efficiency").withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("desc.hbm.flare.gases",Component.literal("20%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("desc.hbm.flare.liquids",Component.literal("10%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.YELLOW));}
}
