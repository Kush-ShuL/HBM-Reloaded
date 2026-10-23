package com.hbm.machine.conveyorsplitter;
import com.hbm.item.*;import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;
/** Actual source ITooltipProvider, shared translated left-shift prompt. */
public final class ConveyorSplitterItem extends BlockItem{
 public ConveyorSplitterItem(ConveyorSplitterBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Component.translatable("tile.crane_splitter.desc").getString().split("\\$",-1))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
