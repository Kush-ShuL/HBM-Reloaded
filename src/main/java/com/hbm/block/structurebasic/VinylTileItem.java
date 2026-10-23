package com.hbm.block.structurebasic;
import com.hbm.block.ore.LegacyOreItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual inherited ItemBlockBase metadata and ItemBlockBlastInfo gold resistance line. */
public final class VinylTileItem extends LegacyOreItem {
 public VinylTileItem(SourceVinylTileBlock block){super(block);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("tooltip.hbm.structure_basic.blast_resistance",36F).withStyle(ChatFormatting.GOLD));}
}
