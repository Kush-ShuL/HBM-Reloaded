package com.hbm.entity.mob.duck;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Empty button intent and login notice: no client-selected coordinates, motion, entity, flag or permission. */
public final class DuckCallPayloads {
    public static Runnable CLIENT_REMINDER = () -> {};

    public record Request() implements CustomPacketPayload {
        public static final Type<Request> TYPE = new Type<>(ResourceLocation.parse("hbm:duck_call"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.unit(new Request());
        @Override public Type<Request> type() { return TYPE; }
    }

    public record Reminder() implements CustomPacketPayload {
        public static final Type<Reminder> TYPE = new Type<>(ResourceLocation.parse("hbm:duck_login_notice"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Reminder> CODEC = StreamCodec.unit(new Reminder());
        @Override public Type<Reminder> type() { return TYPE; }
    }

    public static void register(IEventBus bus) { bus.addListener(DuckCallPayloads::payloads); }
    private static void payloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(Request.TYPE, Request.CODEC, (packet, context) -> context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) DuckCall.summon(player);
        }));
        registrar.playToClient(Reminder.TYPE, Reminder.CODEC,
                (packet, context) -> context.enqueueWork(() -> CLIENT_REMINDER.run()));
    }
    private DuckCallPayloads() {}
}
