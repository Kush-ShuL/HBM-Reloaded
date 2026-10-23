package com.hbm.machine.reactor.iter.auxiliary;
import com.hbm.item.BasicBlockItem;
import java.util.*;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual ITooltipProvider descriptions split on literal source$. */
public final class FusionAuxiliaryItem extends BlockItem {
 public final boolean collector;
 public FusionAuxiliaryItem(boolean collector){super((collector?FusionAuxiliaryContent.COLLECTOR:FusionAuxiliaryContent.COUPLER).get(),new Properties());this.collector=collector;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Language.getInstance().getOrDefault("tile.fusion_"+(collector?"collector":"coupler")+".desc").split("\\$"))lines.add(Component.literal("\u00a7e"+line));else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
