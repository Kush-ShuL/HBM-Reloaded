package com.hbm.armor.mods;

import java.util.Map;
import java.util.WeakHashMap;
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
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source enableMagnet=true player flag and rising-edge Z control; never accepts client motion/range. */
public final class MagnetNetwork {
    private static final Map<Player, Boolean> PRESSED = new WeakHashMap<>();
    public static Consumer<State> CLIENT_STATE = state -> { };
    private static ResourceLocation id(String name) { return ResourceLocation.fromNamespaceAndPath("hbm", name); }
    public static boolean enabled(Player player) {
        return !player.getPersistentData().contains("enableMagnet") || player.getPersistentData().getBoolean("enableMagnet");
    }
    public record Controls(boolean pressed) implements CustomPacketPayload {
        public static final Type<Controls> TYPE = new Type<>(id("armor_magnet_controls"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Controls> CODEC = StreamCodec.of(
                (out, value) -> out.writeBoolean(value.pressed()), in -> new Controls(in.readBoolean()));
        @Override public Type<Controls> type() { return TYPE; }
    }
    public record State(boolean enabled, boolean inform) implements CustomPacketPayload {
        public static final Type<State> TYPE = new Type<>(id("armor_magnet_state"));
        public static final StreamCodec<RegistryFriendlyByteBuf, State> CODEC = StreamCodec.of(
                (out, value) -> { out.writeBoolean(value.enabled()); out.writeBoolean(value.inform()); },
                in -> new State(in.readBoolean(), in.readBoolean()));
        @Override public Type<State> type() { return TYPE; }
    }
    public static void register(IEventBus bus) {
        bus.addListener(MagnetNetwork::payloads);
        NeoForge.EVENT_BUS.addListener(MagnetNetwork::login);
        NeoForge.EVENT_BUS.addListener(MagnetNetwork::respawn);
        NeoForge.EVENT_BUS.addListener(MagnetNetwork::clonePlayer);
        NeoForge.EVENT_BUS.addListener(MagnetNetwork::dimension);
        NeoForge.EVENT_BUS.addListener(MagnetNetwork::logout);
    }
    private static void sync(Player player) {
        PRESSED.remove(player);
        if (player instanceof ServerPlayer server) PacketDistributor.sendToPlayer(server, new State(enabled(server), false));
    }
    private static void login(PlayerEvent.PlayerLoggedInEvent event) { sync(event.getEntity()); }
    private static void respawn(PlayerEvent.PlayerRespawnEvent event) { sync(event.getEntity()); }
    private static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) { sync(event.getEntity()); }
    private static void logout(PlayerEvent.PlayerLoggedOutEvent event) { PRESSED.remove(event.getEntity()); }
    private static void clonePlayer(PlayerEvent.Clone event) {
        event.getEntity().getPersistentData().putBoolean("enableMagnet", enabled(event.getOriginal()));
    }
    private static void payloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(Controls.TYPE, Controls.CODEC, (packet, context) -> context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (packet.pressed() && !PRESSED.getOrDefault(player, false)) {
                boolean enabled = !enabled(player); player.getPersistentData().putBoolean("enableMagnet", enabled);
                PacketDistributor.sendToPlayer(player, new State(enabled, true));
            }
            PRESSED.put(player, packet.pressed());
        }));
        registrar.playToClient(State.TYPE, State.CODEC, (packet, context) -> context.enqueueWork(() -> CLIENT_STATE.accept(packet)));
    }
    private MagnetNetwork() { }
}
