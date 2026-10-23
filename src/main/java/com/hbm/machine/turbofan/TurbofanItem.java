package com.hbm.machine.turbofan;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source ITooltipProvider always lists AERO grade at100%, without an invented SHIFT gate. */
public final class TurbofanItem extends BlockItem {
 public TurbofanItem(TurbofanBlock block){super(block,new Properties());}@Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("tooltip.hbm.turbofan.efficiency").withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("tooltip.hbm.turbofan.aero").withStyle(ChatFormatting.YELLOW).append(Component.literal("100%").withStyle(ChatFormatting.RED)));}
}
