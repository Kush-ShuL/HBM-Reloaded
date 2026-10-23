package com.hbm.machine.silex;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source void button; inventory/tank remain untouched. */
public record SilexControl(int containerId) implements CustomPacketPayload {
 public static final Type<SilexControl> TYPE=new Type<>(SilexContent.id("silex_void"));public static final StreamCodec<RegistryFriendlyByteBuf,SilexControl> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.containerId),b->new SilexControl(b.readVarInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof SilexMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player)&&menu.machine instanceof SilexBlockEntity machine)machine.voidContents();}));}
}
