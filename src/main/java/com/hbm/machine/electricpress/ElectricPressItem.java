package com.hbm.machine.electricpress;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** Only the actual conveyor source implements ITooltipProvider. Electric press has no invented description. */
public final class ElectricPressItem extends BlockItem {
    private final boolean conveyor;
    public ElectricPressItem(Block block,boolean conveyor){super(block,new Properties());this.conveyor=conveyor;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(!conveyor)return;if(com.hbm.item.BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:Language.getInstance().getOrDefault("tile.machine_conveyor_press.desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
