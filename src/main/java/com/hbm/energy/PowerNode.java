package com.hbm.energy;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/** One conductor node may occupy several multiblock positions. */
public final class PowerNode implements PowerLink {
    private final Set<BlockPos> positions = new LinkedHashSet<>();
    private final Set<NodeConnection> connections = new LinkedHashSet<>();
    private final Set<BlockPos> remoteConnections = new LinkedHashSet<>();
    private final Set<EnergyProvider> providers = new LinkedHashSet<>();
    private final Set<EnergyReceiver> receivers = new LinkedHashSet<>();
    private PowerNetwork network;
    private PowerNodespace owner;
    private boolean expired;
    private boolean acceptsRemote;

    public PowerNode(BlockPos... positions) {
        if (positions.length == 0) throw new IllegalArgumentException("HE node needs a position");
        for (BlockPos position : positions) this.positions.add(Objects.requireNonNull(position).immutable());
        for (BlockPos position : this.positions) {
            for (Direction direction : Direction.values()) {
                connections.add(new NodeConnection(position.relative(direction), direction));
            }
        }
    }

    public PowerNode setConnections(NodeConnection... points) {
        for (var point : points) {
            if (!positions.contains(point.origin())) throw new IllegalArgumentException("HE connection starts outside its node");
        }
        connections.clear();
        connections.addAll(Arrays.asList(points));
        changed();
        return this;
    }

    public Set<BlockPos> positions() { return Collections.unmodifiableSet(positions); }
    public Set<NodeConnection> connections() { return Collections.unmodifiableSet(connections); }
    /** Original pylon UNKNOWN-direction links, distinct from physical adjacent faces. */
    public Set<BlockPos> remoteConnections() { return Collections.unmodifiableSet(remoteConnections); }
    public boolean acceptsRemote() { return acceptsRemote; }
    public PowerNode setAcceptsRemote(boolean accepts) {
        if (acceptsRemote != accepts) { acceptsRemote = accepts; changed(); }
        return this;
    }
    public PowerNode addRemoteConnection(BlockPos target) {
        if (remoteConnections.add(Objects.requireNonNull(target).immutable())) changed();
        return this;
    }
    public void removeRemoteConnection(BlockPos target) { if (remoteConnections.remove(target)) changed(); }
    @Override public Set<EnergyProvider> providers() { return Collections.unmodifiableSet(providers); }
    @Override public Set<EnergyReceiver> receivers() { return Collections.unmodifiableSet(receivers); }
    @Override public PowerNetwork network() { return network; }
    @Override public void setNetwork(PowerNetwork network) { this.network = network; }
    public boolean isExpired() { return expired; }
    public boolean hasValidNet() { return network != null && network.isValid(); }

    public void addProvider(EnergyProvider provider) {
        boolean added = providers.add(Objects.requireNonNull(provider));
        if (added || network != null && !network.isProvider(provider)) changed();
    }
    public void removeProvider(EnergyProvider provider) { if (providers.remove(provider)) changed(); }
    public void addReceiver(EnergyReceiver receiver) {
        boolean added = receivers.add(Objects.requireNonNull(receiver));
        if (added || network != null && !network.isSubscribed(receiver)) changed();
    }
    public void removeReceiver(EnergyReceiver receiver) { if (receivers.remove(receiver)) changed(); }

    void attach(PowerNodespace owner) {
        if (this.owner != null && this.owner != owner) throw new IllegalStateException("HE node is in another world");
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
