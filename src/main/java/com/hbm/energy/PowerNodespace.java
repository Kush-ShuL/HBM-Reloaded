package com.hbm.energy;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;

/**
 * World-scoped HE graph. Topology changes rebuild connected components once;
 * reciprocal side connections are required, including on multiblock nodes.
 */
public final class PowerNodespace {
    private final Map<BlockPos, PowerNode> positions = new LinkedHashMap<>();
    private final Set<PowerNode> nodes = new LinkedHashSet<>();
    private final Set<PowerNetwork> networks = new LinkedHashSet<>();
    private boolean dirty;

    private final net.minecraft.server.level.ServerLevel debugLevel;
    public PowerNodespace() { this.debugLevel = null; }
    PowerNodespace(net.minecraft.server.level.ServerLevel level) { this.debugLevel = java.util.Objects.requireNonNull(level); }
    void debugPorts(PowerNode node, boolean receive) {
        if (debugLevel != null) com.hbm.network.debug.NetworkPortDebug.emit(debugLevel,node.connections(),receive,false,0xffffff);
    }

    public PowerNode getNode(BlockPos position) { return positions.get(position); }
    public Set<PowerNode> nodes() { return Collections.unmodifiableSet(nodes); }
    public Set<PowerNetwork> networks() { return Collections.unmodifiableSet(networks); }

    public void createNode(PowerNode node) {
        if (nodes.contains(node)) return;
        for (BlockPos position : node.positions()) {
            if (positions.containsKey(position)) throw new IllegalArgumentException("Overlapping HE nodes at " + position);
        }
        node.attach(this);
        nodes.add(node);
        for (BlockPos position : node.positions()) positions.put(position, node);
        markDirty();
    }

    public void destroyNode(BlockPos position) {
        PowerNode node = positions.get(position);
        if (node != null) destroyNode(node);
    }

    public void destroyNode(PowerNode node) {
        if (!nodes.remove(node)) return;
        // Match UNINOS: old references become unusable as soon as a link breaks.
        if (node.network() != null) node.network().invalidate();
        for (BlockPos position : node.positions()) positions.remove(position, node);
        node.detach();
        markDirty();
    }

    void markDirty() {
        dirty = true;
    }

    public void tick() {
        if (dirty) rebuild();
        for (PowerNetwork network : networks) network.resetTrackers();
        for (PowerNetwork network : networks) network.update();
    }

    public void clear() {
        for (PowerNetwork network : networks) network.invalidate();
        for (PowerNode node : nodes) node.detach();
        networks.clear();
        nodes.clear();
        positions.clear();
        dirty = false;
    }

    private void rebuild() {
        for (PowerNetwork network : networks) network.invalidate();
        networks.clear();
        Set<PowerNode> remaining = new LinkedHashSet<>(nodes);
        var queue = new ArrayDeque<PowerNode>();
        // An original pylon declares its remote endpoint in one direction, and
        // that endpoint's UNKNOWN port accepts it. The resulting network edge
        // is undirected, regardless of which node was registered first.
        var remoteEdges = new LinkedHashMap<PowerNode, Set<PowerNode>>();
        for (PowerNode node : nodes) remoteEdges.put(node, new LinkedHashSet<>());
        for (PowerNode node : nodes) for (BlockPos target : node.remoteConnections()) {
            PowerNode other = positions.get(target);
            if (other != null && other != node && other.acceptsRemote()) {
                remoteEdges.get(node).add(other);
                remoteEdges.get(other).add(node);
            }
        }
        while (!remaining.isEmpty()) {
            PowerNode first = remaining.iterator().next();
            remaining.remove(first);
            queue.add(first);
            var network = new PowerNetwork();
            networks.add(network);
            while (!queue.isEmpty()) {
                PowerNode current = queue.removeFirst();
                network.addNode(current);
                for (PowerNode other : remoteEdges.get(current)) {
                    if (remaining.contains(other)) {
                        remaining.remove(other);
                        queue.addLast(other);
                    }
                }
                for (NodeConnection connection : current.connections()) {
                    PowerNode other = positions.get(connection.target());
                    if (other == null || other == current || !remaining.contains(other)) continue;
                    var reverse = new NodeConnection(connection.origin(), connection.direction().getOpposite());
                    if (other.connections().contains(reverse)) {
                        remaining.remove(other);
                        queue.addLast(other);
                    }
                }
            }
        }
        dirty = false;
    }
}
