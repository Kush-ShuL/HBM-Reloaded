package com.hbm.item.surveyscanner;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original AuxParticlePacketNT marker fields, delivered only to the player who actually scanned. */
@EventBusSubscriber(modid = "hbm", bus = EventBusSubscriber.Bus.MOD)
public record SurveyScannerMarker(BlockPos position, int color, int expires, double distance, String labelKey) implements CustomPacketPayload {
    public static Consumer<SurveyScannerMarker> CLIENT = packet -> { };
    public static final Type<SurveyScannerMarker> TYPE = new Type<>(ResourceLocation.parse("hbm:survey_scanner_marker"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SurveyScannerMarker> CODEC = StreamCodec.of((out, value) -> {
        out.writeBlockPos(value.position()); out.writeInt(value.color()); out.writeInt(value.expires()); out.writeDouble(value.distance()); out.writeUtf(value.labelKey());
    }, in -> new SurveyScannerMarker(in.readBlockPos(), in.readInt(), in.readInt(), in.readDouble(), in.readUtf()));
    @Override public Type<SurveyScannerMarker> type() { return TYPE; }
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (packet, context) -> context.enqueueWork(() -> CLIENT.accept(packet)));
    }
    public static void send(ServerPlayer player, BlockPos position, int color, int expires, double distance, String labelKey) {
        PacketDistributor.sendToPlayer(player, new SurveyScannerMarker(position, color, expires, distance, labelKey));
    }
}
