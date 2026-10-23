package com.hbm.item.structurelooteditor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class LootEditorItem extends BlockItem {
    public LootEditorItem(LootEditorBlock block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){
        lines.add(Component.translatable("tooltip.hbm.wand_loot.description"));
        for(String key:List.of("minimum","maximum","pool","replacement"))lines.add(Component.translatable("tooltip.hbm.wand_loot."+key).withStyle(ChatFormatting.GOLD));
    }
}
