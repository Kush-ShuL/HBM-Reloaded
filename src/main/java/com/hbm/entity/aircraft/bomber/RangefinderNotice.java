package com.hbm.entity.aircraft.bomber;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original PlayerInformPacket's fancy text,5000ms lifetime and shared ID_DETONATOR8. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record RangefinderNotice(String text,boolean polarized) implements CustomPacketPayload {
 public static final Type<RangefinderNotice> TYPE=new Type<>(ResourceLocation.parse("hbm:rangefinder_notice"));
 public static final StreamCodec<RegistryFriendlyByteBuf,RangefinderNotice> CODEC=StreamCodec.of((out,p)->{out.writeUtf(p.text,64);out.writeBoolean(p.polarized);},in->new RangefinderNotice(in.readUtf(64),in.readBoolean()));
 @Override public Type<RangefinderNotice> type(){return TYPE;}
 @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var text=Component.literal(p.text);if(p.polarized)text.withStyle(ChatFormatting.LIGHT_PURPLE);com.hbm.item.settings.client.LegacyInfoSystem.push(text,5000,8);}));}
 public static void send(ServerPlayer player,String text,boolean polarized){PacketDistributor.sendToPlayer(player,new RangefinderNotice(text,polarized));}
}
