package com.hbm.client.digamma;

import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** X5846 keeps and decays BlockAshes.ashes; both overlay render subscriptions are commented out in the source. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class AshClientState {
 public static int ashes;
 public static void accumulate(){
  var player=Minecraft.getInstance().player;if(player==null)return;var helmet=player.getItemBySlot(EquipmentSlot.HEAD);
  boolean glasses=BuiltInRegistries.ITEM.getKey(helmet.getItem()).toString().equals("hbm:ashglasses");
  var protection=HazmatRegistry.protection(helmet,player);float limit=256*(glasses?.25F:protection.contains(Hazard.SAND)||protection.contains(Hazard.LIGHT)?.75F:.95F);
  if(ashes<limit)ashes++;
 }
 @SubscribeEvent public static void tick(ClientTickEvent.Pre event){var client=Minecraft.getInstance();if(client.level!=null&&client.player!=null)ashes=Math.max(0,Math.min(256,ashes)-2);}
 @SubscribeEvent public static void login(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn event){ashes=0;}
 @SubscribeEvent public static void clonePlayer(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone event){ashes=0;}
 private AshClientState(){}
}
