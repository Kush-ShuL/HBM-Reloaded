package com.hbm.fluid.sync;

import com.hbm.HbmReloaded;
import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.HbmFluids;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Required configuration barrier plus acknowledged PLAY snapshots for the one private-fluid registry. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class FluidDefinitionSync {
    private static final int MAX_JSON = 1_048_576;
    private static final int MAX_CONTAINERS = 131072;
    private static final long ACK_TIMEOUT = TimeUnit.SECONDS.toNanos(30);
    private static final AtomicLong SEQUENCE = new AtomicLong();
    private static final ConfigurationTask.Type TASK = new ConfigurationTask.Type(HbmReloaded.id("fluid_definitions"));
    // All entries and completions live on the server thread. The client has separate state in its own class.
    private static final Map<Connection, ConfigurationPending> CONFIGURATION = new IdentityHashMap<>();
    private static final Map<Connection, Map<Long, Pending>> PLAY = new IdentityHashMap<>();

    public record Definitions(long sequence, String json) implements CustomPacketPayload {
        public static final Type<Definitions> TYPE = new Type<>(HbmReloaded.id("fluid_definitions_configuration"));
        public static final StreamCodec<FriendlyByteBuf, Definitions> CODEC = StreamCodec.of(
                (buffer, value) -> { buffer.writeLong(value.sequence()); buffer.writeUtf(value.json(), MAX_JSON); },
                buffer -> new Definitions(buffer.readLong(), buffer.readUtf(MAX_JSON)));
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record Snapshot(long sequence, String json, boolean containerCompat, List<FluidContainerRegistry.ContainerSnapshot> containers)
            implements CustomPacketPayload {
        public Snapshot { containers = List.copyOf(containers); }
        public static final Type<Snapshot> TYPE = new Type<>(HbmReloaded.id("fluid_definitions_play"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Snapshot> CODEC = StreamCodec.of((buffer, value) -> {
            buffer.writeLong(value.sequence());
            buffer.writeUtf(value.json(), MAX_JSON);
            buffer.writeBoolean(value.containerCompat());
            buffer.writeVarInt(value.containers().size());
            for (var row : value.containers()) {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, row.fullContainer());
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, row.emptyContainer());
                buffer.writeInt(row.fluidId());
                buffer.writeInt(row.content());
            }
        }, buffer -> {
            long sequence = buffer.readLong();
            String json = buffer.readUtf(MAX_JSON);
            boolean containerCompat = buffer.readBoolean();
            int count = buffer.readVarInt();
            if (count < 0 || count > MAX_CONTAINERS) throw new IllegalArgumentException("Invalid fluid container count");
            var containers = new ArrayList<FluidContainerRegistry.ContainerSnapshot>(count);
            for (int index = 0; index < count; index++) {
                var full = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
                var empty = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
                // Never call fromID in this codec: definitions are installed later, on MAIN.
                containers.add(new FluidContainerRegistry.ContainerSnapshot(full, empty, buffer.readInt(), buffer.readInt()));
            }
            return new Snapshot(sequence, json, containerCompat, containers);
        });
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record Ack(long sequence) implements CustomPacketPayload {
        public static final Type<Ack> TYPE = new Type<>(HbmReloaded.id("fluid_definitions_ack"));
        public static final StreamCodec<FriendlyByteBuf, Ack> CODEC = StreamCodec.of(
                (buffer, value) -> buffer.writeLong(value.sequence()), buffer -> new Ack(buffer.readLong()));
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // The default MAIN handlers enqueue without blocking the network thread.
        var registrar = event.registrar("fluid-definitions-1");
        registrar.configurationToClient(Definitions.TYPE, Definitions.CODEC, (packet, context) -> ClientFluidDefinitionSync.definitions(packet, context));
        registrar.playToClient(Snapshot.TYPE, Snapshot.CODEC, (packet, context) -> ClientFluidDefinitionSync.snapshot(packet, context));
        registrar.commonToServer(Ack.TYPE, Ack.CODEC, FluidDefinitionSync::acknowledge);
    }

    @SubscribeEvent
    public static void configuration(RegisterConfigurationTasksEvent event) {
        var listener = event.getListener();
        event.register(new ICustomConfigurationTask() {
            @Override public void run(Consumer<CustomPacketPayload> sender) {
                long sequence = SEQUENCE.incrementAndGet();
                CONFIGURATION.put(listener.getConnection(), new ConfigurationPending(sequence, System.nanoTime(), listener));
                sender.accept(new Definitions(sequence, HbmFluids.definitionSnapshot().toString()));
            }
            @Override public ConfigurationTask.Type type() { return TASK; }
        });
    }

    /** Completes only after every currently connected target has applied definitions and containers on MAIN. */
    public static CompletableFuture<Void> sendToAll(MinecraftServer server) {
        if (!server.isSameThread()) {
            var result = new CompletableFuture<Void>();
            server.execute(() -> sendToAll(server).whenComplete((ignored, error) -> {
                if (error == null) result.complete(null); else result.completeExceptionally(error);
            }));
            return result;
        }
        var players = List.copyOf(server.getPlayerList().getPlayers());
        if (players.isEmpty()) return CompletableFuture.completedFuture(null);
        var packet = capture();
        var waits = new ArrayList<CompletableFuture<Void>>();
        for (var player : players) waits.add(send(player, packet));
        return CompletableFuture.allOf(waits.toArray(CompletableFuture[]::new));
    }

    private static Snapshot capture() {
        return new Snapshot(SEQUENCE.incrementAndGet(), HbmFluids.definitionSnapshot().toString(), FluidContainerRegistry.containerCompat(), FluidContainerRegistry.snapshot());
    }

    private static CompletableFuture<Void> send(ServerPlayer player, Snapshot packet) {
        var connection = player.connection.getConnection();
        var completed = new CompletableFuture<Void>();
        var pending = new Pending(System.nanoTime(), completed, player.connection);
        PLAY.computeIfAbsent(connection, ignored -> new LinkedHashMap<>()).put(packet.sequence(), pending);
        try { player.connection.send(packet); }
        catch (RuntimeException error) {
            remove(connection, packet.sequence());
            completed.completeExceptionally(error);
        }
        return completed;
    }

    private static void acknowledge(Ack packet, IPayloadContext context) {
        var connection = context.connection();
        if (context.protocol() == ConnectionProtocol.CONFIGURATION) {
            var pending = CONFIGURATION.get(connection);
            if (pending == null || pending.sequence() != packet.sequence()) {
                context.disconnect(Component.translatable("disconnect.hbm.fluid_sync.invalid_ack"));
                return;
            }
            CONFIGURATION.remove(connection);
            context.finishCurrentTask(TASK);
        } else {
            var pending = remove(connection, packet.sequence());
            if (pending != null) pending.completed().complete(null);
        }
    }

    private static Pending remove(Connection connection, long sequence) {
        var requests = PLAY.get(connection);
        if (requests == null) return null;
        var result = requests.remove(sequence);
        if (requests.isEmpty()) PLAY.remove(connection);
        return result;
    }

    private record ConfigurationPending(long sequence, long sentAt, ICommonPacketListener listener) {}
    private record Pending(long sentAt, CompletableFuture<Void> completed, ICommonPacketListener listener) {}

    @EventBusSubscriber(modid = HbmReloaded.MOD_ID)
    public static final class Events {
        @SubscribeEvent
        public static void loginSnapshot(OnDatapackSyncEvent event) {
            // CONFIGURATION already installed fluid IDs before any initial PLAY recipe decoding.
            // PLAY carries registry-aware ItemStack components, including dynamic holder components.
            if (event.getPlayer() != null) {
                send(event.getPlayer(), capture()).whenComplete((ignored, error) -> {
                    if (error != null) HbmReloaded.LOGGER.warn("HBM login fluid snapshot was not acknowledged", error);
                });
            }
        }

        @SubscribeEvent
        public static void expire(ServerTickEvent.Post event) {
            long now = System.nanoTime();
            var expiredConfiguration = new ArrayList<ConfigurationPending>();
            CONFIGURATION.entrySet().removeIf(entry -> {
                boolean expired = now - entry.getValue().sentAt() > ACK_TIMEOUT;
                if (expired && entry.getKey().isConnected()) expiredConfiguration.add(entry.getValue());
                return expired || !entry.getKey().isConnected();
            });
            expiredConfiguration.forEach(pending -> pending.listener().disconnect(Component.translatable("disconnect.hbm.fluid_sync.timeout")));
            // Complete futures after map iteration: callers can schedule another synchronization.
            var failed = new ArrayList<CompletableFuture<Void>>();
            var disconnect = java.util.Collections.newSetFromMap(new IdentityHashMap<ICommonPacketListener, Boolean>());
            PLAY.entrySet().removeIf(entry -> {
                entry.getValue().entrySet().removeIf(request -> {
                    boolean expired = !entry.getKey().isConnected() || now - request.getValue().sentAt() > ACK_TIMEOUT;
                    if (expired) {
                        failed.add(request.getValue().completed());
                        if (entry.getKey().isConnected()) disconnect.add(request.getValue().listener());
                    }
                    return expired;
                });
                return entry.getValue().isEmpty();
            });
            disconnect.forEach(listener -> listener.disconnect(Component.translatable("disconnect.hbm.fluid_sync.timeout")));
            failed.forEach(future -> future.completeExceptionally(new IllegalStateException("HBM fluid snapshot ACK timed out or client disconnected")));
        }

        @SubscribeEvent
        public static void stopped(ServerStoppedEvent event) {
            var failed = PLAY.values().stream().flatMap(requests -> requests.values().stream()).map(Pending::completed).toList();
            PLAY.clear(); CONFIGURATION.clear();
            failed.forEach(future -> future.completeExceptionally(new IllegalStateException("Server stopped during HBM fluid synchronization")));
        }
    }

    private FluidDefinitionSync() {}
}
