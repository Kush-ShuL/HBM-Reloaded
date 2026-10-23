package com.hbm.block.structurebasic;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Exact ItemBlockBlastInfo tooltip, no invented manuscript item or GUI. */
public final class MarkedConcreteItem extends BlockItem {
 public MarkedConcreteItem(MarkedConcreteBlock block){super(block,new Item.Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("tooltip.hbm.structure_basic.blast_resistance",96F).withStyle(ChatFormatting.GOLD));}
}
