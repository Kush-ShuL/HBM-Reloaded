package com.hbm.armor.t51;
import com.hbm.damage.*;
import com.hbm.exposure.HbmExposure;
import com.hbm.geiger.GeigerClicks;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.GunArmorPerks.FullSetArmor;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
/** Original FSB per-piece drain order comes from actual native Inventory.tick armor compartment, not a parallel energy store. */
public final class T51Runtime {
 public static final EquipmentSlot[]ORDER={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
 public static void register(){NeoForge.EVENT_BUS.addListener(T51Runtime::pre);NeoForge.EVENT_BUS.addListener(T51Runtime::post);NeoForge.EVENT_BUS.addListener(T51Runtime::jump);NeoForge.EVENT_BUS.addListener(T51Runtime::fall);NeoForge.EVENT_BUS.addListener(T51Runtime::flyFall);NeoForge.EVENT_BUS.addListener(T51Runtime::login);NeoForge.EVENT_BUS.addListener(T51Runtime::clonePlayer);NeoForge.EVENT_BUS.addListener(T51Runtime::respawn);}
 public static boolean fullSet(Player player,boolean enabled){var chest=player.getItemBySlot(EquipmentSlot.CHEST);if(!(chest.getItem()instanceof FullSetArmor plate))return false;for(var slot:ORDER){if(slot==EquipmentSlot.HEAD&&plate.noHelmet())continue;var item=player.getItemBySlot(slot);if(!(item.getItem()instanceof FullSetArmor armor)||armor.fsbMaterialIdentity()!=plate.fsbMaterialIdentity()||enabled&&!armor.isArmorEnabled(item))return false;}return true;}
 public static boolean active(Player player){return player.getItemBySlot(EquipmentSlot.CHEST).getItem()instanceof FullSetArmor armor&&armor.fsbVats()&&fullSet(player,true);}
 private static void effects(Player player){
  if(!fullSet(player,true))return;
  var chest=player.getItemBySlot(EquipmentSlot.CHEST).getItem();
  if(chest instanceof ArmorT51)player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,20,0,true,true));
  if(chest instanceof FullSetArmor armor)for(var template:armor.fsbEffects())player.addEffect(new MobEffectInstance(template));
 }
 private static void pre(PlayerTickEvent.Pre event){effects(event.getEntity());}
 private static void post(PlayerTickEvent.Post event){effects(event.getEntity());}
 public static void armorTick(com.hbm.armor.powered.SourcePoweredArmor armor,ItemStack stack,Level world,Player player){
  if(armor instanceof net.minecraft.world.item.ArmorItem armorItem&&armorItem.getEquipmentSlot()==EquipmentSlot.CHEST&&armor.sourceGeiger()&&!world.isClientSide&&fullSet(player,true)&&world.getGameTime()%5==0){
   boolean ownCounter=player.getInventory().items.stream().anyMatch(item->{var name=BuiltInRegistries.ITEM.getKey(item.getItem());return name.getNamespace().equals("hbm")&&(name.getPath().equals("geiger_counter")||name.getPath().equals("dosimeter"));});
   float dose=HbmExposure.data(player).radBuffer()*HbmExposure.radiationModifier(player);
   if(!ownCounter&&dose>1E-5){int[] samples=GeigerClicks.samples(dose);int sound=samples[world.random.nextInt(samples.length)];if(sound>0)sound(world,player,"item.geiger"+sound,1,1);}
  }
  if(fullSet(player,true)&&!player.getAbilities().instabuild)armor.dischargeBattery(stack,armor.sourceDrain());
 }
 private static void jump(LivingEvent.LivingJumpEvent event){if(event.getEntity()instanceof Player player&&!player.level().isClientSide&&fullSet(player,true)&&player.getItemBySlot(EquipmentSlot.CHEST).getItem()instanceof FullSetArmor armor&&armor.fsbJumpSound()!=null)sound(player.level(),player,armor.fsbJumpSound(),.5F,1);}
 private static void fall(LivingFallEvent event){if(event.getEntity()instanceof Player player)performFall(player,event.getDistance());}
 private static void flyFall(net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent event){performFall(event.getEntity(),event.getDistance());}
 private static void performFall(Player player,float distance){if(player.level().isClientSide||!fullSet(player,true)||!(player.getItemBySlot(EquipmentSlot.CHEST).getItem()instanceof FullSetArmor armor))return;
  if(armor.fsbHardLanding()&&distance>10){var source=new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,T51Content.id("t51_landing"))),player);
   for(var entity:player.level().getEntities(player,player.getBoundingBox().inflate(3,0,3))){if(entity instanceof ItemEntity)continue;double x=player.getX()-entity.getX(),z=player.getZ()-entity.getZ(),length=Math.sqrt(x*x+z*z);if(length<3){double intensity=3-length;entity.setDeltaMovement(entity.getDeltaMovement().add(x*intensity*-2,.1*intensity,z*intensity*-2));entity.hurtMarked=true;entity.hurt(source,(float)(intensity*10));}}
  }
  if(distance>.5&&armor.fsbFallSound()!=null)sound(player.level(),player,armor.fsbFallSound(),.5F,1);
 }
 private static void sound(Level level,Player player,String name,float volume,float pitch){level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get(name),SoundSource.PLAYERS,volume,pitch);}
 private static void login(PlayerEvent.PlayerLoggedInEvent event){if(event.getEntity()instanceof ServerPlayer player)T51Network.sync(player);}
 private static void respawn(PlayerEvent.PlayerRespawnEvent event){if(event.getEntity()instanceof ServerPlayer player)T51Network.sync(player);}
 private static void clonePlayer(PlayerEvent.Clone event){event.getEntity().getPersistentData().putBoolean("enableHUD",T51Network.enabled(event.getOriginal()));}
 private T51Runtime(){}
}
