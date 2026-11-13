package com.hbm.satellite;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** An interception is visible throughout its dimension, independently of player position. */
public record SatelliteInterceptEffect() implements CustomPacketPayload {
    public static final Type<SatelliteInterceptEffect> TYPE = new Type<>(ResourceLocation.parse("hbm:satellite_interception"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SatelliteInterceptEffect> CODEC = StreamCodec.unit(new SatelliteInterceptEffect());
    public static Runnable CLIENT = () -> { };
    @Override public Type<SatelliteInterceptEffect> type() { return TYPE; }
    public static void broadcast(ServerLevel level) { PacketDistributor.sendToPlayersInDimension(level, new SatelliteInterceptEffect()); }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (packet, context) -> context.enqueueWork(CLIENT));
    }
}
