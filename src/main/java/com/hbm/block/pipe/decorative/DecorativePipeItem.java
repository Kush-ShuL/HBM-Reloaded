package com.hbm.block.pipe.decorative;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class DecorativePipeItem extends BlockItem {
    public DecorativePipeItem(DecorativePipeBlock block) { super(block,new Properties()); }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag) { tooltip.add(Component.literal("Purely decorative")); }
}
