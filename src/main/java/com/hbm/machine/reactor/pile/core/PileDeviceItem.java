package com.hbm.machine.reactor.pile.core;
import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;import net.minecraft.world.level.block.Block;
public final class PileDeviceItem extends BlockItem {
 public PileDeviceItem(Block block){super(block,new Properties().component(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0));}
 public static int metadata(ItemStack stack){return stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 public ItemStack stack(int meta){var stack=new ItemStack(this);stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),meta);return stack;}
 public static String kind(ItemStack stack){return new String[]{"loader","vent","control"}[Math.abs(metadata(stack)%3)];}
 @Override public Component getName(ItemStack stack){return Component.translatable("tile.pile_device."+kind(stack)+".name");}
 @Override public String getDescriptionId(ItemStack stack){return "item.hbm.pile_device."+kind(stack);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(com.hbm.item.BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:net.minecraft.locale.Language.getInstance().getOrDefault("tile.pile_device."+kind(stack)+".desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
