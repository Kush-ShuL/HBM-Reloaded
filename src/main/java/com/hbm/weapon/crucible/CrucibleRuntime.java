package com.hbm.weapon.crucible;
import com.hbm.bomb.conventional.grenade.UniversalGrenadeItem;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
/** Source IEquipReceiver item-change boundary and ItemStack-sensitive active charge attributes. */
@EventBusSubscriber(modid="hbm")
public final class CrucibleRuntime {
 @SubscribeEvent public static void equip(LivingEquipmentChangeEvent event){
  if(event.getSlot()!=EquipmentSlot.MAINHAND||!(event.getEntity() instanceof ServerPlayer player)||event.getFrom().is(event.getTo().getItem()))return;
  var stack=event.getTo();
  if(stack.getItem() instanceof CrucibleItem)CrucibleItem.equip(player,stack);
  else if(stack.getItem() instanceof ItemGunBaseNT gun)gun.onEquip(player,stack);
  else if(stack.getItem() instanceof UniversalGrenadeItem grenade)grenade.equip(player,stack);
 }
 @SubscribeEvent public static void attributes(ItemAttributeModifierEvent event){
  var stack=event.getItemStack();if(!(stack.getItem() instanceof CrucibleItem))return;
  // Original explicit AttributeModifiers stack NBT overrides an item's own map. Respect its modern component equivalent too.
  if(stack.getComponentsPatch().get(DataComponents.ATTRIBUTE_MODIFIERS)!=null)return;
  event.clearModifiers();if(!CrucibleItem.active(stack))return;
  event.addModifier(Attributes.ATTACK_DAMAGE,new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,CrucibleItem.SOURCE_DAMAGE,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
  event.addModifier(Attributes.MOVEMENT_SPEED,new AttributeModifier(ResourceLocation.parse("hbm:source_tool_movement"),CrucibleItem.SOURCE_MOVEMENT,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),EquipmentSlotGroup.MAINHAND);
 }
 private CrucibleRuntime(){}
}
