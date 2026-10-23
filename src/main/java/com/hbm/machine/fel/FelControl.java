package com.hbm.machine.fel;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source meta2 toggle, authenticated by the actual usable open machine menu. */
public record FelControl(int containerId) implements CustomPacketPayload {
 public static final Type<FelControl> TYPE=new Type<>(FelContent.id("fel_toggle"));public static final StreamCodec<RegistryFriendlyByteBuf,FelControl> CODEC=StreamCodec.of((buf,value)->buf.writeVarInt(value.containerId),buf->new FelControl(buf.readVarInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof FelMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player)&&menu.machine instanceof FelBlockEntity machine)machine.toggle();}));}
}
