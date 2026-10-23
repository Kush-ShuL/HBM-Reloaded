package com.hbm.machine.conveyorpackaging;

import com.hbm.item.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Real BlockCraneBase ITooltipProvider lore and the shared source left-shift convention. */
public final class CranePackagingItem extends BlockItem {
    private final String sourceId;
    public CranePackagingItem(ConveyorPackagingBlock block,String id){super(block,new Properties());sourceId=id;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Component.translatable("tile."+sourceId+".desc").getString().split("\\$",-1))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
