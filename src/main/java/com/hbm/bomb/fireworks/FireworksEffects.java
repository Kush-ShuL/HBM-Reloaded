package com.hbm.bomb.fireworks;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Only actual source launch vanillaExt/flame and character+50spark auxiliary requests. */
public final class FireworksEffects {
    public static Consumer<Effect> CLIENT=packet->{};
    public record Effect(boolean launch,double x,double y,double z,int color,int character) implements CustomPacketPayload {
        public static final Type<Effect> TYPE=new Type<>(ResourceLocation.parse("hbm:source_fireworks"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Effect> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBoolean(value.launch);buffer.writeDouble(value.x);buffer.writeDouble(value.y);buffer.writeDouble(value.z);buffer.writeInt(value.color);buffer.writeInt(value.character);},buffer->new Effect(buffer.readBoolean(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readInt(),buffer.readInt()));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToClient(Effect.TYPE,Effect.CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet))));}
    static void launch(Level world,double x,double y,double z){if(world instanceof ServerLevel server)PacketDistributor.sendToPlayersNear(server,null,x,y,z,100,new Effect(true,x,y,z,0,0));}
    static void burst(Level world,double x,double y,double z,int color,int character){if(world instanceof ServerLevel server)PacketDistributor.sendToPlayersNear(server,null,x,y,z,300,new Effect(false,x,y,z,color,character));}
    private FireworksEffects(){}
}
