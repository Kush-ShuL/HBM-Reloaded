package com.hbm.world.meteor;

import com.hbm.armor.*;
import com.hbm.damage.HbmDamageSources;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Source helmet-slot charms also fit chestplates, as explicitly declared by ItemModCharm. */
public final class MeteorCharmItem extends ArmorModItem {
 public final boolean prevent;
 public MeteorCharmItem(boolean prevent){super(ArmorModStorage.HELMET,true,true,false,false);this.prevent=prevent;}
 @Override public Component getName(ItemStack stack){return Component.translatable("item."+(prevent?"meteor_charm":"protection_charm")+".name");}
 public Component installedDescription(ItemStack stack){return Component.literal("  ").append(stack.getHoverName()).withStyle(ChatFormatting.GOLD);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){
  lines.add(Component.translatable("tooltip.hbm.meteor_charm.blessed").withStyle(ChatFormatting.AQUA));
  for(String key:prevent?List.of("disable","negate_broadcast"):List.of("divert","safe","halve_broadcast"))lines.add(Component.translatable("tooltip.hbm.meteor_charm."+key).withStyle(ChatFormatting.AQUA));
  super.appendHoverText(stack,context,lines,flags);
 }
 static void register(){NeoForge.EVENT_BUS.addListener(MeteorCharmItem::hurt);}
 private static void hurt(LivingIncomingDamageEvent event){
  var entity=event.getEntity();if(entity.level().isClientSide||!event.getSource().is(HbmDamageSources.broadcast))return;
  for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD}){
   var armor=entity.getItemBySlot(slot);if(!ArmorModStorage.hasMods(armor))continue;
   for(var mod:ArmorModStorage.pryMods(armor,entity.registryAccess()))if(mod.getItem() instanceof MeteorCharmItem charm)event.setAmount(charm.prevent?0:event.getAmount()*.5F);
  }
 }
}
