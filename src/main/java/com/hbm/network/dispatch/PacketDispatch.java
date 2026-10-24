package com.hbm.network.dispatch;

import com.hbm.commands.CommandPacketInfo;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Source packet work is grouped before handoff to Minecraft's unchanged channel pipeline. */
public final class PacketDispatch {
    private static final ConcurrentHashMap<MinecraftServer, ServerState> SERVERS = new ConcurrentHashMap<>();
    private static final ThreadLocal<Group> CURRENT = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> NATIVE_HANDOFF = ThreadLocal.withInitial(() -> false);

    private PacketDispatch() {}

    public static void register(ModContainer container) {
        PacketDispatchConfig.register(container);
        NeoForge.EVENT_BUS.addListener(CommandPacketInfo::register);
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            var state = new ServerState(event.getServer());
            var previous = SERVERS.putIfAbsent(event.getServer(), state);
            if (previous != null) {
                state.pool.stop();
                throw new IllegalStateException("Packet dispatcher already exists for this server");
            }
        });
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ServerTickEvent.Post event) -> {
            var state = SERVERS.get(event.getServer());
            if (state == null || !state.accepting) return;
            try {
                state.pool.tickEnd();
            } finally {
                state.flushTouched();
            }
        });
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> {
            var state = SERVERS.remove(event.getServer());
            if (state == null) return;
            state.accepting = false;
            state.epoch.incrementAndGet();
            state.pool.stop();
            state.touched.clear();
        });
    }

    /** Commands can inspect an existing server pool, but cannot recreate one during shutdown. */
    public static PacketDispatchPool pool(MinecraftServer server) {
        var state = SERVERS.get(server);
        if (state == null || !state.accepting) throw new IllegalStateException("Packet dispatcher is not running");
        return state.pool;
    }

    public static Scope beginPayloadGroup(MinecraftServer server, CustomPacketPayload payload) {
        return beginGroup(server, new ClientboundCustomPayloadPacket(payload));
    }

    public static Scope beginGroup(MinecraftServer server, Packet<?> packet) {
        var state = SERVERS.get(server);
        if (state == null || !state.accepting) return new Scope(null, null, false);
        var previous = CURRENT.get();
        if (previous != null && previous.state == state) {
            previous.snapshot(packet);
            return new Scope(previous, previous, false);
        }
        var group = new Group(state);
        group.snapshot(packet);
        CURRENT.set(group);
        return new Scope(group, previous, true);
    }

    public static boolean isAttachmentUpdate(Packet<?> packet) {
        return AttachmentFanoutScope.isUpdatePacket(packet);
    }

    /** Called at the native send boundary before source work is considered. */
    public static void beforeNativeSend(Connection connection, Packet<?> packet) {
        if (changesContext(packet)) invalidate(connection);
    }

    private static boolean changesContext(Packet<?> packet) {
        if (packet.isTerminal() || packet instanceof ClientboundStartConfigurationPacket
                || packet instanceof ClientboundRespawnPacket || packet instanceof ClientboundLoginPacket) return true;
        // A PLAY fluid publication can replace IDs used by already-captured machine states.
        if (packet instanceof ClientboundCustomPayloadPacket custom
                && custom.payload() instanceof com.hbm.fluid.sync.FluidDefinitionSync.Snapshot) return true;
        if (packet instanceof BundlePacket<?> bundle) {
            for (var child : bundle.subPackets()) if (changesContext(child)) return true;
        }
        return false;
    }

    public static void invalidate(Connection connection) {
        if (connection instanceof DispatchConnection access) access.hbm$invalidateDispatch();
    }

    /** Capture only source-equivalent PLAY requests; native callbacks retain their native scheduling. */
    public static boolean intercept(Connection connection, Packet<?> packet, PacketSendListener listener, boolean flush) {
        if (NATIVE_HANDOFF.get() || listener != null || connection.getSending() != PacketFlow.CLIENTBOUND
                || !connection.isConnected() || !(connection instanceof DispatchConnection access)
                || !(connection.getPacketListener() instanceof ServerGamePacketListenerImpl game)
                || !game.isAcceptingMessages()) return false;
        var server = game.player.serverLevel().getServer();
        var state = SERVERS.get(server);
        if (state == null || !state.accepting) return false;
        var current = CURRENT.get();
        if (current != null && current.state == state) {
            var frozen = current.snapshot(packet);
            if (frozen == null) return false;
            current.deliveries.add(new Delivery(connection, access.hbm$dispatchEpoch(), frozen, listener, flush));
            return true;
        }
        var group = new Group(state);
        var frozen = group.snapshot(packet);
        if (frozen == null) return false;
        group.deliveries.add(new Delivery(connection, access.hbm$dispatchEpoch(), frozen, listener, flush));
        group.publish();
        return true;
    }

    public static final class Scope implements AutoCloseable {
        private final Group group;
        private final Group previous;
        private final boolean owner;
        private boolean committed;
        private boolean closed;

        private Scope(Group group, Group previous, boolean owner) {
            this.group = group;
            this.previous = previous;
            this.owner = owner;
        }

        public void commit() { committed = true; }

        @Override
        public void close() {
            if (closed) return;
            closed = true;
            if (group == null) return;
            if (!committed) group.failed = true;
            if (!owner) return;
            if (previous == null) CURRENT.remove();
            else CURRENT.set(previous);
            try {
                if (committed && !group.failed && group.prepared) group.publish();
            } finally {
                group.snapshots.clear();
                group.deliveries.clear();
            }
        }
    }

    private static final class Group {
        final ServerState state;
        final long serverEpoch;
        final IdentityHashMap<Object, Packet<?>> snapshots = new IdentityHashMap<>();
        final ArrayList<Delivery> deliveries = new ArrayList<>();
        boolean prepared;
        boolean failed;

        Group(ServerState state) {
            this.state = state;
            this.serverEpoch = state.epoch.get();
        }

        Packet<?> snapshot(Packet<?> packet) {
            // PacketDistributor wraps the same typed payload separately for each recipient.
            // Reuse its snapshot only inside this explicit logical group. Attachments retain
            // exact packet identity because initial and update records can otherwise look alike.
            Object key = packet instanceof ClientboundCustomPayloadPacket custom
                    && !(custom.payload() instanceof net.neoforged.neoforge.network.payload.SyncAttachmentsPayload)
                    ? custom.payload() : packet;
            if (snapshots.containsKey(key)) return snapshots.get(key);
            try {
                var frozen = SourcePacketSnapshot.freeze(state.server, packet);
                snapshots.put(key, frozen);
                prepared |= frozen != null;
                return frozen;
            } catch (RuntimeException | Error error) {
                failed = true;
                throw error;
            }
        }

        void publish() {
            if (!state.accepting || state.epoch.get() != serverEpoch) return;
            var requests = List.copyOf(deliveries);
            var currentState = state;
            long epoch = serverEpoch;
            currentState.pool.prepare(() -> currentState.handoff(epoch, requests));
        }
    }

    private record Delivery(Connection connection, long connectionEpoch, Packet<?> packet,
                            PacketSendListener listener, boolean flush) {}

    private static final class ServerState {
        final MinecraftServer server;
        final PacketDispatchPool pool;
        final AtomicLong epoch = new AtomicLong();
        final Set<Connection> touched = ConcurrentHashMap.newKeySet();
        volatile boolean accepting = true;

        ServerState(MinecraftServer server) {
            this.server = server;
            this.pool = new PacketDispatchPool(server);
        }

        boolean valid(long expectedServerEpoch, Delivery request) {
            return accepting && epoch.get() == expectedServerEpoch && request.connection().isConnected()
                    && request.connection() instanceof DispatchConnection access
                    && access.hbm$dispatchEpoch() == request.connectionEpoch()
                    && request.connection().getPacketListener() instanceof ServerGamePacketListenerImpl game
                    && game.isAcceptingMessages();
        }

        void handoff(long expectedServerEpoch, List<Delivery> requests) {
            for (var request : requests) {
                if (!valid(expectedServerEpoch, request)) continue;
                var connection = request.connection();
                touched.add(connection);
                // The guard and bypass must run on the channel thread which actually calls send.
                connection.channel().eventLoop().execute(() -> {
                    if (!valid(expectedServerEpoch, request)) return;
                    boolean previous = NATIVE_HANDOFF.get();
                    NATIVE_HANDOFF.set(true);
                    try {
                        connection.send(request.packet(), request.listener(), request.flush());
                    } finally {
                        if (previous) NATIVE_HANDOFF.set(true);
                        else NATIVE_HANDOFF.remove();
                    }
                });
            }
        }

        void flushTouched() {
            for (var connection : touched.toArray(Connection[]::new)) {
                if (!touched.remove(connection)) continue;
                if (accepting && connection.isConnected()) connection.flushChannel();
            }
        }
    }
}
