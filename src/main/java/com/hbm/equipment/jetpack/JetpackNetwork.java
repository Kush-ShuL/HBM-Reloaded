package com.hbm.equipment.jetpack;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original enableBackpack persistent flag / two source key edges, no client-supplied motion/fuel. */
public final class JetpackNetwork {
    // Entity equality compares numeric ids, shared by the integrated server and its client.
    // Keep each actual player's input separate; lifecycle hooks release these strong keys.
    private static final Map<Player, Keys> KEYS = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final class Keys { volatile boolean jump, toggle; }
    private static Keys keys(Player player) { synchronized (KEYS) { return KEYS.computeIfAbsent(player, ignored -> new Keys()); } }
    public static Consumer<State> CLIENT_STATE = state -> { };
    public static BiConsumer<Integer, Integer> CLIENT_EXHAUST = (player, mode) -> { };
    private static ResourceLocation id(String name) { return ResourceLocation.fromNamespaceAndPath("hbm", name); }
    public record Controls(boolean jump, boolean toggle) implements CustomPacketPayload {
        public static final Type<Controls> TYPE = new Type<>(id("source_jetpack_controls"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Controls> CODEC = StreamCodec.of((out, value) -> {
            out.writeBoolean(value.jump()); out.writeBoolean(value.toggle());
        }, in -> new Controls(in.readBoolean(), in.readBoolean()));
        @Override public Type<Controls> type() { return TYPE; }
    }
    public record State(boolean enabled, boolean inform) implements CustomPacketPayload {
        public static final Type<State> TYPE = new Type<>(id("source_jetpack_state"));
        public static final StreamCodec<RegistryFriendlyByteBuf, State> CODEC = StreamCodec.of((out, value) -> {
            out.writeBoolean(value.enabled()); out.writeBoolean(value.inform());
        }, in -> new State(in.readBoolean(), in.readBoolean()));
        @Override public Type<State> type() { return TYPE; }
    }
    public record Exhaust(int player, int mode) implements CustomPacketPayload {
        public static final Type<Exhaust> TYPE = new Type<>(id("source_jetpack_exhaust"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Exhaust> CODEC = StreamCodec.of((out, value) -> {
            out.writeInt(value.player()); out.writeByte(value.mode());
        }, in -> new Exhaust(in.readInt(), in.readUnsignedByte()));
        @Override public Type<Exhaust> type() { return TYPE; }
    }
    public static boolean enabled(Player player) { return !player.getPersistentData().contains("enableBackpack") || player.getPersistentData().getBoolean("enableBackpack"); }
    public static boolean jump(Player player) { var keys = KEYS.get(player); return keys != null && keys.jump; }
    public static void localKeys(Player player, boolean jump, boolean toggle) { var keys = keys(player); keys.jump = jump; keys.toggle = toggle; }
    public static void clearKeys(Player player) { KEYS.remove(player); }
    public static void exhaust(ServerPlayer player, int mode) {
        PacketDistributor.sendToPlayersNear(player.serverLevel(), null, player.getX(), player.getY(), player.getZ(), 100, new Exhaust(player.getId(), mode));
    }
    public static void login(PlayerEvent.PlayerLoggedInEvent event) { if (event.getEntity() instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player, new State(enabled(player), false)); }
    public static void respawn(PlayerEvent.PlayerRespawnEvent event) { if (event.getEntity() instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player, new State(enabled(player), false)); }
    public static void clonePlayer(PlayerEvent.Clone event) {
        clearKeys(event.getOriginal()); clearKeys(event.getEntity());
        event.getEntity().getPersistentData().putBoolean("enableBackpack", enabled(event.getOriginal()));
    }
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // A held toggle is not a new press when crossing a dimension boundary.
        keys(event.getEntity()).jump = false;
        if (event.getEntity() instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player, new State(enabled(player), false));
    }
    private static void logout(PlayerEvent.PlayerLoggedOutEvent event) { clearKeys(event.getEntity()); }
    private static void stopped(ServerStoppedEvent event) {
        synchronized (KEYS) { KEYS.keySet().removeIf(player -> player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() == event.getServer()); }
    }
    public static void register(IEventBus bus) {
        bus.addListener(JetpackNetwork::payloads);
        NeoForge.EVENT_BUS.addListener(JetpackNetwork::logout);
        NeoForge.EVENT_BUS.addListener(JetpackNetwork::stopped);
    }
    private static void payloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(Controls.TYPE, Controls.CODEC, (packet, context) -> context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            var keys = keys(player);
            if (packet.toggle() && !keys.toggle) {
                boolean enabled = !enabled(player); player.getPersistentData().putBoolean("enableBackpack", enabled);
                PacketDistributor.sendToPlayer(player, new State(enabled, true));
            }
            keys.jump = packet.jump(); keys.toggle = packet.toggle();
        }));
        registrar.playToClient(State.TYPE, State.CODEC, (packet, context) -> context.enqueueWork(() -> CLIENT_STATE.accept(packet)));
        registrar.playToClient(Exhaust.TYPE, Exhaust.CODEC, (packet, context) -> context.enqueueWork(() -> CLIENT_EXHAUST.accept(packet.player(), packet.mode())));
    }
    private JetpackNetwork() { }
}
