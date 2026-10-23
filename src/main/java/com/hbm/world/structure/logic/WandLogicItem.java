package com.hbm.world.structure.logic;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public final class WandLogicItem extends BlockItem {
    public WandLogicItem(Block block) { super(block,new Item.Properties()); }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.wand_logic.screwdriver").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.hbm.wand_logic.defuser").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.hbm.wand_logic.drill").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.hbm.wand_logic.detonator").withStyle(ChatFormatting.YELLOW));
    }
}
