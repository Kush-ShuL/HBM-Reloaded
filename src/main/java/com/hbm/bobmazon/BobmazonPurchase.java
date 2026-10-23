package com.hbm.bobmazon;

import com.hbm.damage.HbmDamageSources;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source offer/currency validation and slot-ordered cap deduction; the server owns the real held catalog. */
public record BobmazonPurchase(InteractionHand hand,int offer)implements CustomPacketPayload {
 public static final Type<BobmazonPurchase> TYPE=new Type<>(ResourceLocation.parse("hbm:bobmazon_purchase"));
 public static final StreamCodec<RegistryFriendlyByteBuf,BobmazonPurchase> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeEnum(packet.hand);buffer.writeInt(packet.offer);},buffer->new BobmazonPurchase(buffer.readEnum(InteractionHand.class),buffer.readInt()));
 private static final Set<String> CAPS=Set.of("cap_fritz","cap_korl","cap_nuka","cap_quantum","cap_rad","cap_sparkle");
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{if(context.player() instanceof ServerPlayer player)purchase(player,packet);}));}
 private static boolean cap(ItemStack stack){if(stack.isEmpty())return false;var id=BuiltInRegistries.ITEM.getKey(stack.getItem());return id.getNamespace().equals("hbm")&&CAPS.contains(id.getPath());}
 private static void purchase(ServerPlayer player,BobmazonPurchase packet){
  var held=player.getItemInHand(packet.hand);if(!(held.getItem() instanceof CatalogItem catalog)){failSafe(player);return;}
  var offers=BobmazonOffers.offers(player.registryAccess(),catalog.hidden);if(packet.offer<0||packet.offer>=offers.size()){failSafe(player);return;}var offer=offers.get(packet.offer);
  if(!offer.available()){player.sendSystemMessage(Component.translatable("chat.hbm.bobmazon.unavailable"));return;}
  if(!offer.requirement().fulfilled(player)&&!player.isCreative()){player.sendSystemMessage(Component.translatable("chat.hbm.bobmazon.requirement"));return;}
  int caps=0;for(int i=0;i<player.getInventory().getContainerSize();i++){var item=player.getInventory().getItem(i);if(cap(item))caps+=item.getCount();}
  if(caps<offer.cost()&&!player.isCreative()){player.sendSystemMessage(Component.translatable("chat.hbm.bobmazon.caps"));return;}
  // Source creative bypasses eligibility/balance, but still pays any caps that are actually present.
  int remaining=offer.cost();for(int i=0;i<player.getInventory().getContainerSize()&&remaining>0;i++){var item=player.getInventory().getItem(i);if(cap(item)){int amount=Math.min(remaining,item.getCount());item.shrink(amount);remaining-=amount;}}
  player.getInventory().setChanged();player.inventoryMenu.broadcastChanges();var random=player.level().random;
  var delivery=new BobmazonDelivery(BobmazonContent.DELIVERY.get(),player.level());delivery.setPos(player.getX()+random.nextGaussian()*10,300,player.getZ()+random.nextGaussian()*10);delivery.payload=offer.product().copy();player.level().addFreshEntity(delivery);
 }
 private static void failSafe(ServerPlayer player){player.sendSystemMessage(Component.translatable("chat.hbm.bobmazon.mismatch"));player.sendSystemMessage(Component.translatable("chat.hbm.bobmazon.failsafe"));player.hurt(HbmDamageSources.source(player.level(),HbmDamageSources.nuclearBlast),1000);var movement=player.getDeltaMovement();player.setDeltaMovement(movement.x,2,movement.z);player.hurtMarked=true;}
}
