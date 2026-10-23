package com.hbm.bomb.nuclear;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemN2 / ItemFleija / ItemSolinium behavior, including Fleija propellant rarity. */
public final class NuclearBombPart extends Item {
 private final String symbol,bomb;
 public NuclearBombPart(String symbol,String bomb,boolean rare){super(new Properties().stacksTo(1).rarity(rare?Rarity.RARE:Rarity.COMMON));this.symbol=symbol;this.bomb=bomb;}
 @Override public Component getName(ItemStack stack){return Component.translatable("item."+symbol+".name");}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("item.bomb_part.used_in"));lines.add(Component.translatable("tile.nuke_"+bomb+".name"));}
}
