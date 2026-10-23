package com.hbm.entity.mob.pigeon;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Exact source auxiliary sweat request: real entity ID, 3 white-wool block dust, range50. */
public final class PigeonEffects {
    public static Consumer<Sweat> CLIENT=packet->{};
    public record Sweat(int entity) implements CustomPacketPayload {
        public static final Type<Sweat> TYPE=new Type<>(ResourceLocation.parse("hbm:pigeon_sweat"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Sweat> CODEC=StreamCodec.of((buffer,value)->buffer.writeVarInt(value.entity),buffer->new Sweat(buffer.readVarInt()));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToClient(Sweat.TYPE,Sweat.CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet))));}
    static void sweat(EntityPigeon entity){if(entity.level() instanceof ServerLevel world)PacketDistributor.sendToPlayersNear(world,null,entity.getX(),entity.getY(),entity.getZ(),50,new Sweat(entity.getId()));}
    private PigeonEffects(){}
}
