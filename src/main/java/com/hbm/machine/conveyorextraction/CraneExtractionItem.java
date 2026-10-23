package com.hbm.machine.conveyorextraction;

import com.hbm.conveyor.crane.CraneBaseBlock;
import com.hbm.item.BasicBlockItem;
import com.hbm.item.TooltipHints;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class CraneExtractionItem extends BlockItem {
    private final String sourceId;
    public CraneExtractionItem(CraneBaseBlock block,String id){super(block,new Properties());sourceId=id;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Component.translatable("tile."+sourceId+".desc").getString().split("\\$",-1))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
