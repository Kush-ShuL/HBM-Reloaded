package com.hbm.world.structure.dependencies.industrial44;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** ItemBlockBlastInfo, including the actual source resistance after Block's 3/5 conversion. */
public final class BlastPaneItem extends BlockItem {
 public BlastPaneItem(ReinforcedPaneBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("tooltip.hbm.meteor_materials.blast_resistance",getBlock().getExplosionResistance()).withStyle(ChatFormatting.GOLD));}
}
