package com.hbm.fluid.network;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.HbmFluidType;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;

/**
 * World-scoped native-fluid graph. Topology changes rebuild connected components once;
 * reciprocal side connections are required, including on multiblock nodes.
 */
public final class FluidNodespace {
    private final HbmFluidType type;
    public FluidNodespace(HbmFluidType type) { this.type = type; }
    private final Map<BlockPos, FluidNode> positions = new LinkedHashMap<>();
    private final Set<FluidNode> nodes = new LinkedHashSet<>();
    private final Set<FluidNetMK2> networks = new LinkedHashSet<>();
    private boolean dirty;

    public FluidNode getNode(BlockPos position) { return positions.get(position); }
    public Set<FluidNode> nodes() { return Collections.unmodifiableSet(nodes); }
    public Set<FluidNetMK2> networks() { return Collections.unmodifiableSet(networks); }

    public void createNode(FluidNode node) {
        if (nodes.contains(node)) return;
        for (BlockPos position : node.positions()) {
            if (positions.containsKey(position)) throw new IllegalArgumentException("Overlapping fluid nodes at " + position);
        }
        node.attach(this);
        nodes.add(node);
        for (BlockPos position : node.positions()) positions.put(position, node);
        markDirty();
    }

    public void destroyNode(BlockPos position) {
        FluidNode node = positions.get(position);
        if (node != null) destroyNode(node);
    }

    public void destroyNode(FluidNode node) {
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
        for (FluidNetMK2 network : networks) network.resetTrackers();
        for (FluidNetMK2 network : networks) network.update();
    }

    public void clear() {
        for (FluidNetMK2 network : networks) network.invalidate();
        for (FluidNode node : nodes) node.detach();
        networks.clear();
        nodes.clear();
        positions.clear();
        dirty = false;
    }

    private void rebuild() {
        for (FluidNetMK2 network : networks) network.invalidate();
        networks.clear();
        Set<FluidNode> remaining = new LinkedHashSet<>(nodes);
        var queue = new ArrayDeque<FluidNode>();
        while (!remaining.isEmpty()) {
            FluidNode first = remaining.iterator().next();
            remaining.remove(first);
            queue.add(first);
            var network = new FluidNetMK2(type);
            networks.add(network);
            while (!queue.isEmpty()) {
                FluidNode current = queue.removeFirst();
                network.addNode(current);
                for (BlockPos target : current.remoteConnections()) {
                    FluidNode other = positions.get(target);
                    if (other != null && other != current && remaining.contains(other)
                            && current.positions().stream().anyMatch(other.remoteConnections()::contains)) {
                        remaining.remove(other); queue.addLast(other);
                    }
                }
                for (NodeConnection connection : current.connections()) {
                    FluidNode other = positions.get(connection.target());
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
