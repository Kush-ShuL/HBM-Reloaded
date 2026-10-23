package com.hbm.conveyor.belt;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Hidden source BlockItems retain the original ITooltipProvider descriptions. */
public final class ConveyorBlockItem extends BlockItem {
 private final String id;public ConveyorBlockItem(ConveyorBeltBlock block,String id){super(block,new Properties());this.id=id;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(ConveyorWandItem.LEFT_SHIFT.getAsBoolean()){for(String text:Component.translatable("tile."+id+".desc").getString().split("\\$",-1))lines.add(Component.literal(text).withStyle(ChatFormatting.YELLOW));}else lines.add(Component.translatable("gui.hbm.conveyor.shift",Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC)).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));}
}
