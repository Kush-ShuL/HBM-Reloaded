package com.hbm.item.recipedependencies;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source ItemBlockBlastInfo, no invented laminate armor or radiation protection. */
public final class ReinforcedLaminateItem extends BlockItem {
 public ReinforcedLaminateItem(ReinforcedLaminateBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.recipe_inputs53.blast_resistance",180F).withStyle(ChatFormatting.GOLD));}
}
