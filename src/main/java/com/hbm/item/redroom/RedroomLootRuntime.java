package com.hbm.item.redroom;
import com.hbm.armor.ArmorModStorage;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.*;
import com.hbm.weapon.sedna.gun.GunNetwork;
import com.hbm.weapon.sedna.gun.kinetic.XFactory12ga;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
/** Original source event effects, in feet→helmet/mod-slot order; no arbitrary cooldown, free ammo or new damage multiplier. */
final class RedroomLootRuntime {
 private static final EquipmentSlot[] ARMOR={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
 static void register(){NeoForge.EVENT_BUS.addListener(EventPriority.HIGH,RedroomLootRuntime::hurt);NeoForge.EVENT_BUS.addListener(RedroomLootRuntime::tick);NeoForge.EVENT_BUS.addListener(RedroomLootRuntime::afterTick);NeoForge.EVENT_BUS.addListener(RedroomLootRuntime::punch);NeoForge.EVENT_BUS.addListener(RedroomLootRuntime::drops);NeoForge.EVENT_BUS.addListener(RedroomLootRuntime::smelted);NeoForge.EVENT_BUS.addListener(RedroomVillageLoot::loaded);}
 private static void hurt(LivingIncomingDamageEvent event){var entity=event.getEntity();if(entity.level().isClientSide)return;if(entity instanceof Player player){var data=ShieldInfusion.data(player);if(data.shield>0){float absorbed=Math.min(data.shield,event.getAmount());data.shield-=absorbed;event.setAmount(event.getAmount()-absorbed);}data.lastDamage=player.tickCount;player.syncData(ShieldInfusion.DATA);}
  com.hbm.item.medicine.MedicineDisease.applyDamageAmplification(event);
  for(var slot:ARMOR){var armor=entity.getItemBySlot(slot);if(!ArmorModStorage.hasMods(armor))continue;for(var mod:ArmorModStorage.pryMods(armor,entity.registryAccess())){if(mod.getItem() instanceof com.hbm.armor.ArmorModItem source)source.modDamage(event,armor);if(!(mod.getItem() instanceof RedroomArmorModItem item))continue;switch(item.kind){case POLISH->{if(entity.level().random.nextInt(20)==0)event.setAmount(0);}case BANDAID->{if(entity.level().random.nextInt(100)<3){event.setAmount(0);entity.heal(entity.getMaxHealth());}}case QUARTZ->HbmExposure.setRadiation(entity,Math.max(HbmExposure.radiation(entity)-10,0));default->{}}}}}
 private static void tick(EntityTickEvent.Pre event){if(!(event.getEntity() instanceof LivingEntity entity))return;for(var slot:ARMOR){var armor=entity.getItemBySlot(slot);if(!ArmorModStorage.hasMods(armor))continue;for(var mod:ArmorModStorage.pryMods(armor,entity.registryAccess()))if(mod.getItem() instanceof RedroomArmorModItem item&&item.kind==RedroomArmorModItem.Kind.MILK){var harmful=new java.util.ArrayList<net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>>();for(var effect:entity.getActiveEffects())if(effect.getEffect().value().getCategory()==MobEffectCategory.HARMFUL)harmful.add(effect.getEffect());for(var effect:harmful)entity.removeEffect(effect);}}}
 private static void afterTick(EntityTickEvent.Post event){if(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)ShieldInfusion.tick(player);}
 private static void punch(AttackEntityEvent event){var player=event.getEntity();if(player.level().isClientSide)return;var chest=player.getItemBySlot(EquipmentSlot.CHEST);if(chest.isEmpty()||!ArmorModStorage.hasMods(chest))return;var held=player.getMainHandItem();boolean[] attackModifier={false};held.forEachModifier(EquipmentSlot.MAINHAND,(attribute,modifier)->{if(attribute.equals(Attributes.ATTACK_DAMAGE))attackModifier[0]=true;});if(attackModifier[0])return;var servo=ArmorModStorage.pryMod(chest,ArmorModStorage.SERVOS,player.registryAccess());if(!(servo.getItem() instanceof RedroomArmorModItem item)||item.kind!=RedroomArmorModItem.Kind.BALLISTIC_GAUNTLET)return;BulletConfig fired=null;for(var config:new BulletConfig[]{XFactory12ga.g12_bp,XFactory12ga.g12_bp_magnum,XFactory12ga.g12_bp_slug,XFactory12ga.g12,XFactory12ga.g12_slug,XFactory12ga.g12_flechette,XFactory12ga.g12_magnum,XFactory12ga.g12_explosive,XFactory12ga.g12_phosphorus}){for(var stack:player.getInventory().items)if(!stack.isEmpty()&&config.ammo.matchesRecipe(stack,true)){stack.shrink(1);player.getInventory().setChanged();fired=config;break;}if(fired!=null)break;}if(fired==null)return;int count=fired.projectilesMin;if(fired.projectilesMax>count)count+=player.getRandom().nextInt(fired.projectilesMax-count);for(int i=0;i<count;i++){var projectile=new EntityBulletBaseMK4(player,fired,15,0,-.1875,-.0625,.5);player.level().addFreshEntity(projectile);if(i==0&&fired.blackPowder){var motion=projectile.getDeltaMovement();GunNetwork.blackPowder(player.level(),projectile.getX(),projectile.getY(),projectile.getZ(),motion.x,motion.y,motion.z,10,.25F,.5F,10,.25F);}}player.level().playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("weapon.shotgunShoot"),SoundSource.PLAYERS,1,1);}
 private static void drops(LivingDropsEvent event){
  var entity=event.getEntity();
  if(entity.level().isClientSide||!entity.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)||!(event.getSource().getEntity() instanceof Player player)||player instanceof FakePlayer)return;
  if(entity instanceof Spider&&entity.getRandom().nextInt(500)==0)drop(event,RedroomArmorModItem.Kind.MILK);
  if(entity instanceof CaveSpider&&entity.getRandom().nextInt(100)==0)drop(event,"serum");
  if(entity instanceof Animal&&entity.getRandom().nextInt(500)==0)drop(event,RedroomArmorModItem.Kind.BANDAID);
  if(entity instanceof Enemy){
   if(entity.getRandom().nextInt(1000)==0)drop(event,"heart_piece");
   if(entity.getRandom().nextInt(250)==0)drop(event,"key_red_cracked");
   if(entity.getRandom().nextInt(250)==0)drop(event,"launch_code_piece");
  }
  if(entity instanceof com.hbm.entity.mob.crab.CyberCrab&&entity.getRandom().nextInt(500)==0){var dropped=new net.minecraft.world.entity.item.ItemEntity(entity.level(),entity.getX(),entity.getY(),entity.getZ(),new ItemStack(com.hbm.armor.wd40.Wd40Content.WD40.get()));dropped.setDefaultPickUpDelay();event.getDrops().add(dropped);}
  if(entity instanceof Zombie){
   if(entity.getRandom().nextInt(200)==0)drop(event,"minecraft:copper_ingot");
   if(entity.getRandom().nextInt(200)==0)drop(event,"ingot_aluminium");
   if(entity.getRandom().nextInt(200)==0)drop(event,"ingot_titanium");
  }
 }
 private static void drop(LivingDropsEvent event,String symbol){var entity=event.getEntity();var drop=new net.minecraft.world.entity.item.ItemEntity(entity.level(),entity.getX(),entity.getY(),entity.getZ(),new ItemStack(com.hbm.registry.HbmContent.item(symbol)));drop.setDefaultPickUpDelay();event.getDrops().add(drop);}
 private static void drop(LivingDropsEvent event,RedroomArmorModItem.Kind kind){var entity=event.getEntity();var drop=new net.minecraft.world.entity.item.ItemEntity(entity.level(),entity.getX(),entity.getY()+1,entity.getZ(),new ItemStack(RedroomLootContent.MODS.get(kind).get()));drop.setDefaultPickUpDelay();event.getDrops().add(drop);}
 private static void smelted(PlayerEvent.ItemSmeltedEvent event){var player=event.getEntity();if(player.level().isClientSide||!BuiltInRegistries.ITEM.getKey(event.getSmelting().getItem()).equals(ResourceLocation.parse("hbm:ingot_uranium"))||player.getRandom().nextInt(64)!=0)return;var stack=new ItemStack(RedroomLootContent.MODS.get(RedroomArmorModItem.Kind.QUARTZ).get());if(!player.getInventory().add(stack))player.drop(stack,false);else player.containerMenu.broadcastChanges();}
 private RedroomLootRuntime(){}
}
