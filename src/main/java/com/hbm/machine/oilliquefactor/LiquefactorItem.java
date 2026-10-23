package com.hbm.machine.oilliquefactor;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Original ITooltipProvider machine dimensions. No persistent-fluid item state exists in X5846. */
public final class LiquefactorItem extends BlockItem {
 public LiquefactorItem(LiquefactorBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("tooltip.hbm.liquefactor.dimensions"));}
}
