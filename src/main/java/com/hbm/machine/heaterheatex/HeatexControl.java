package com.hbm.machine.heaterheatex;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source GUI's toCool/delay edits, bounded by actual open menu and source machine permission. */
public record HeatexControl(int containerId,int field,int value)implements CustomPacketPayload {
 public static final Type<HeatexControl> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","heater_heatex_control"));public static final StreamCodec<RegistryFriendlyByteBuf,HeatexControl> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.containerId);b.writeByte(p.field);b.writeVarInt(p.value);},b->new HeatexControl(b.readVarInt(),b.readByte(),b.readVarInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof HeatexMenu menu&&menu.containerId==packet.containerId&&menu.stillValid(player)&&player.distanceToSqr(menu.position.getX()+.5,menu.position.getY()+.5,menu.position.getZ()+.5)<256)menu.control(packet.field,packet.value);}));}
}
