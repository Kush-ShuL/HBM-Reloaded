package com.hbm.machine.reactor.iter.utilities;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Original source ITooltipProvider descriptions, no fake machine GUI or standard fluid capability. */
public final class FusionUtilityItem extends BlockItem {
 public final boolean creative;
 public FusionUtilityItem(boolean creative){super((creative?FusionUtilityContent.CREATIVE:FusionUtilityContent.BOILER).get(),new Properties());this.creative=creative;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(var line:Language.getInstance().getOrDefault("tile.fusion_"+(creative?"klystron_creative":"boiler")+".desc").split("\\$"))lines.add(Component.literal("\u00a7e"+line));else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
