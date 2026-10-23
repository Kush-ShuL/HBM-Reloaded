package com.hbm.fusion.plasma;
import com.hbm.energy.NodeConnection;
import java.util.*;
import net.minecraft.core.BlockPos;
/** Original multi-position GenNode with reciprocal physical face connections. */
public final class PlasmaNode {
    final Set<BlockPos> positions=new LinkedHashSet<>();
    final Set<NodeConnection> connections=new LinkedHashSet<>();
    final Set<FusionPowerReceiver> receivers=new LinkedHashSet<>();
    final Set<Object> providers=new LinkedHashSet<>();
    PlasmaNodespace owner;
    PlasmaNetwork network;
    boolean expired;
    public PlasmaNode(BlockPos...positions){if(positions.length==0)throw new IllegalArgumentException("Empty plasma node");for(var pos:positions){this.positions.add(pos.immutable());for(var dir:net.minecraft.core.Direction.values())connections.add(new NodeConnection(pos.relative(dir),dir));}}
    public PlasmaNode setConnections(NodeConnection...connections){for(var connection:connections)if(!positions.contains(connection.origin()))throw new IllegalArgumentException("Plasma connection outside node");this.connections.clear();this.connections.addAll(Arrays.asList(connections));if(owner!=null)owner.dirty=true;return this;}
    public boolean isExpired(){return expired;}
    public boolean hasValidNet(){return network!=null&&network.isValid();}
    public PlasmaNetwork network(){return network;}
    public void addReceiver(FusionPowerReceiver receiver){receivers.add(receiver);if(hasValidNet())network.addReceiver(receiver);}
    public void addProvider(Object provider){providers.add(provider);if(hasValidNet())network.addProvider(provider);}
}
