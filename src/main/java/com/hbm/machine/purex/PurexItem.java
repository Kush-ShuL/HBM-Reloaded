package com.hbm.machine.purex;

import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** MachinePUREX/ITooltipProvider's exact left-Shift description convention. */
public final class PurexItem extends BlockItem {
    public PurexItem(){super(PurexContent.BLOCK.get(),new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean()){for(var line:Language.getInstance().getOrDefault("tile.machine_purex.desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(Component.translatable("tooltip.hbm.purex.shift",Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC)).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));}
}
