package com.hbm.machine.conveyorrouter;

import com.hbm.item.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ITooltipProvider lore, including the existing left-shift hint and original three lines. */
public final class CraneRouterItem extends BlockItem {
    public CraneRouterItem(CraneRouterBlock block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Component.translatable("tile.crane_router.desc").getString().split("\\$",-1))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
