package com.hbm.machine.oilburner;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record OilburnerControl(int containerId)implements CustomPacketPayload {
 public static final Type<OilburnerControl> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","oilburner_toggle"));public static final StreamCodec<RegistryFriendlyByteBuf,OilburnerControl> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.containerId),b->new OilburnerControl(b.readVarInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof OilburnerMenu menu&&menu.containerId==packet.containerId&&menu.stillValid(player)&&player.distanceToSqr(menu.position.getX(),menu.position.getY(),menu.position.getZ())<=256)menu.toggle();}));}
}
