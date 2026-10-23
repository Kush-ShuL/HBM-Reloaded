package com.hbm.block.pylon;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public final class PylonBlockItem extends BlockItem {
    public final PylonKind kind;
    public PylonBlockItem(Block block,PylonKind kind) { super(block,new Properties()); this.kind=kind; }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags) {
        tooltip.add(Component.translatable("tooltip.hbm.network.connection_type",Component.translatable("tooltip.hbm.network."+(kind.wires==1?"single":kind.wires==3?"triple":"quadruple")).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.hbm.network.range",Component.translatable("tooltip.hbm.network.metres",(int)kind.range).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));
        if(kind==PylonKind.LARGE) tooltip.add(Component.translatable("tooltip.hbm.network.substation_required").withStyle(ChatFormatting.GOLD));
    }
}
