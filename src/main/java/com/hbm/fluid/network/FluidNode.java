package com.hbm.fluid.network;
import com.hbm.energy.NodeConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/** One conductor node may occupy several multiblock positions. */
public final class FluidNode {
    private final Set<BlockPos> positions = new LinkedHashSet<>();
    private final Set<BlockPos> remoteConnections = new LinkedHashSet<>();
    private final Set<NodeConnection> connections = new LinkedHashSet<>();
    private final Set<FluidProvider> providers = new LinkedHashSet<>();
    private final Set<FluidReceiver> receivers = new LinkedHashSet<>();
    private FluidNetMK2 network;
    private FluidNodespace owner;
    private boolean expired;

    public FluidNode(BlockPos... positions) {
        if (positions.length == 0) throw new IllegalArgumentException("native fluid node needs a position");
        for (BlockPos position : positions) this.positions.add(Objects.requireNonNull(position).immutable());
        for (BlockPos position : this.positions) {
            for (Direction direction : Direction.values()) {
                connections.add(new NodeConnection(position.relative(direction), direction));
            }
        }
    }

    public FluidNode setConnections(NodeConnection... points) {
        for (var point : points) {
            if (!positions.contains(point.origin())) throw new IllegalArgumentException("native fluid connection starts outside its node");
        }
        connections.clear();
        connections.addAll(Arrays.asList(points));
        changed();
        return this;
    }

    /** UNKNOWN-direction original pipeline links require an explicit reciprocal remote point. */
    public void addRemoteConnection(BlockPos target) { if (remoteConnections.add(target.immutable())) changed(); }
    public Set<BlockPos> remoteConnections() { return Collections.unmodifiableSet(remoteConnections); }

    public Set<BlockPos> positions() { return Collections.unmodifiableSet(positions); }
    public Set<NodeConnection> connections() { return Collections.unmodifiableSet(connections); }
    public Set<FluidProvider> providers() { return Collections.unmodifiableSet(providers); }
    public Set<FluidReceiver> receivers() { return Collections.unmodifiableSet(receivers); }
    public FluidNetMK2 network() { return network; }
    public void setNetwork(FluidNetMK2 network) { this.network = network; }
    public boolean isExpired() { return expired; }
    public boolean hasValidNet() { return network != null && network.isValid(); }

    public void addProvider(FluidProvider provider) {
        boolean added = providers.add(Objects.requireNonNull(provider));
        if (added || network != null && !network.isProvider(provider)) changed();
    }
    public void removeProvider(FluidProvider provider) { if (providers.remove(provider)) changed(); }
    public void addReceiver(FluidReceiver receiver) {
        boolean added = receivers.add(Objects.requireNonNull(receiver));
        if (added || network != null && !network.isSubscribed(receiver)) changed();
    }
    public void removeReceiver(FluidReceiver receiver) { if (receivers.remove(receiver)) changed(); }

    void attach(FluidNodespace owner) {
        if (this.owner != null && this.owner != owner) throw new IllegalStateException("native fluid node is in another world");
        this.owner = owner;
        expired = false;
    }

    void detach() {
        owner = null;
        network = null;
        expired = true;
    }

    private void changed() {
        if (owner != null) owner.markDirty();
    }
}
