package com.hbm.item.rttyhandheld;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original ID_PAGER_DYN1000+slot and5000ms notices, using the actual existing shared PlayerInform inbox. */
public record RttyPagerNotice(String message, int id) implements CustomPacketPayload {
    public static final Type<RttyPagerNotice> TYPE = new Type<>(ResourceLocation.parse("hbm:rtty_pager_notice"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RttyPagerNotice> CODEC = StreamCodec.of(
        (buffer, packet) -> { buffer.writeUtf(packet.message); buffer.writeInt(packet.id); },
        buffer -> new RttyPagerNotice(buffer.readUtf(), buffer.readInt()));
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (packet, context) -> context.enqueueWork(() ->
            com.hbm.item.settings.client.LegacyInfoSystem.push(Component.literal(packet.message), 5000, packet.id)));
    }
    public static void send(ServerPlayer player, String message, int id) { PacketDistributor.sendToPlayer(player, new RttyPagerNotice(message, id)); }
}
