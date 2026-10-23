package com.hbm.machine.solidifier;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source ITooltipProvider left-shift description; original machine item does not store fluid contents. */
public final class SolidifierItem extends BlockItem {
 public SolidifierItem(SolidifierBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(com.hbm.item.BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:Language.getInstance().getOrDefault("tile.machine_solidifier.desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
