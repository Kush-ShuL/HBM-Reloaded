package com.hbm.machine.pistoninserter;

import com.hbm.item.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class PistonInserterItem extends BlockItem {
    public PistonInserterItem(PistonInserterBlock block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Component.translatable("tile.piston_inserter.desc").getString().split("\\$",-1))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
