package com.hbm.worldgen.depthdeposit;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** ItemBlockBase dispatch to the actual BlockDepth ITooltipProvider. */
public final class DepthRockItem extends BlockItem {
    public DepthRockItem(Block block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("trait.tile.depth").withStyle(ChatFormatting.YELLOW));}
}
