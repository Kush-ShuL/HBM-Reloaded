package com.hbm.network.narrowrails;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
public final class SourceCartRailItem extends BlockItem {
 public SourceCartRailItem(SourceCartRail block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){var kind=((SourceCartRail)getBlock()).kind;float multiplier=kind.maxSpeed/.4F;if(multiplier!=1F)lines.add(Component.translatable("tooltip.hbm.cart_rail.speed",(int)(multiplier*100)).withStyle(multiplier>1?ChatFormatting.BLUE:ChatFormatting.RED));if(!kind.turnedTexture)lines.add(Component.translatable("tooltip.hbm.cart_rail.no_turn").withStyle(ChatFormatting.RED));}
}
