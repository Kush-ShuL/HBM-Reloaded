package com.hbm.machine.compressor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record CompressorControl(int containerId,int pressure)implements CustomPacketPayload {
 public static final Type<CompressorControl> TYPE=new Type<>(ResourceLocation.parse("hbm:compressor_control"));public static final StreamCodec<RegistryFriendlyByteBuf,CompressorControl> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.containerId);b.writeByte(p.pressure);},b->new CompressorControl(b.readVarInt(),b.readUnsignedByte()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof CompressorMenu menu&&menu.containerId==packet.containerId&&menu.stillValid(player)&&packet.pressure>=0&&packet.pressure<=4)menu.control(packet.pressure);}));}
}
