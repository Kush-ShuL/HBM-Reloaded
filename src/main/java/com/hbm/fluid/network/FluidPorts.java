package com.hbm.fluid.network;

import com.hbm.energy.NodeConnection;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/** Source TilePort lifecycle: a new node subscribes only after its first network forms. */
public final class FluidPorts {
    private final BlockPos[] positions;
    private final NodeConnection[] connections;
    private final Map<HbmFluidType, FluidNode> nodes = new IdentityHashMap<>();
    public FluidPorts(BlockPos origin) { this(new BlockPos[]{origin.immutable()}, Arrays.stream(Direction.values()).map(direction -> new NodeConnection(origin.relative(direction), direction)).toArray(NodeConnection[]::new)); }
    public FluidPorts(BlockPos[] positions, NodeConnection[] connections) { this.positions = positions.clone(); this.connections = connections.clone(); }
    public void update(ServerLevel level, FluidUser owner) {
        var active = java.util.Collections.newSetFromMap(new IdentityHashMap<HbmFluidType, Boolean>());
        if (owner instanceof StandardFluidSender sender) for (var tank : sender.getSendingTanks()) { if (tank.getTankType() != HbmFluids.NONE) active.add(tank.getTankType()); }
        if (owner instanceof StandardFluidReceiver receiver) for (var tank : receiver.getReceivingTanks()) { if (tank.getTankType() != HbmFluids.NONE) active.add(tank.getTankType()); }
        if (!(owner instanceof StandardFluidSender) && !(owner instanceof StandardFluidReceiver)) for (var tank : owner.getAllTanks()) { if (tank.getTankType() != HbmFluids.NONE) active.add(tank.getTankType()); }
        for (var entry : java.util.List.copyOf(nodes.entrySet())) if (!active.contains(entry.getKey())) { FluidNetworks.nodespace(level, entry.getKey()).destroyNode(entry.getValue()); nodes.remove(entry.getKey()); }
        for (var type : active) {
            var space = FluidNetworks.nodespace(level, type);
            FluidNode node = nodes.get(type);
            if (node == null || node.isExpired()) {
                node = new FluidNode(positions).setConnections(Arrays.stream(connections).filter(connection -> owner.canConnect(type, connection.direction())).toArray(NodeConnection[]::new));
                space.createNode(node); nodes.put(type, node);
            }
            if (node.hasValidNet()) {
                if (owner instanceof FluidProvider provider) {
                    if (!(owner instanceof StandardFluidSender sender) || Arrays.stream(sender.getSendingTanks()).anyMatch(tank -> tank.getTankType() == type)) node.addProvider(provider);
                    else node.removeProvider(provider);
                }
                if (owner instanceof FluidReceiver receiver) {
                    if (!(owner instanceof StandardFluidReceiver standard) || Arrays.stream(standard.getReceivingTanks()).anyMatch(tank -> tank.getTankType() == type)) node.addReceiver(receiver);
                    else node.removeReceiver(receiver);
                }
            }
        }
    }
    public void remove(ServerLevel level) { nodes.forEach((type, node) -> FluidNetworks.nodespace(level, type).destroyNode(node)); nodes.clear(); }
}
