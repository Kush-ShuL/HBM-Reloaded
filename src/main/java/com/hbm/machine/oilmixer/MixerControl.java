package com.hbm.machine.oilmixer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record MixerControl(int containerId)implements CustomPacketPayload {
 public static final Type<MixerControl> TYPE=new Type<>(ResourceLocation.parse("hbm:oil_mixer_control"));public static final StreamCodec<RegistryFriendlyByteBuf,MixerControl> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.containerId),b->new MixerControl(b.readVarInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent e){e.registrar("1").playToServer(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var player=c.player();if(player.containerMenu instanceof MixerMenu menu&&menu.containerId==p.containerId&&menu.stillValid(player))menu.cycle();}));}
}
