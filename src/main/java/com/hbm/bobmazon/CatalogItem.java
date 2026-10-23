package com.hbm.bobmazon;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class CatalogItem extends Item {
 public final boolean hidden;
 public CatalogItem(boolean hidden){super(new Properties().stacksTo(1));this.hidden=hidden;}
 @Override public Component getName(ItemStack stack){return Component.translatable(hidden?"item.bobmazon_hidden.name":"item.bobmazon.name");}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){if(level.isClientSide)BobmazonContent.CLIENT_OPEN.accept(player,hand);return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),level.isClientSide);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(hidden){lines.add(Component.translatable("tooltip.hbm.bobmazon.guide"));lines.add(Component.translatable("tooltip.hbm.bobmazon.promise"));}}
}
