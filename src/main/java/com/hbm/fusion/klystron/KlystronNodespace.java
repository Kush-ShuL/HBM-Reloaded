package com.hbm.fusion.klystron;
import com.hbm.energy.NodeConnection;
import java.util.*;
import net.minecraft.core.BlockPos;
/** Source UNINOS klystron connected components. No HE/NeoForge-fluid bridge or chunk loading is involved. */
public final class KlystronNodespace {
    private final Map<BlockPos,KlystronNode> positions=new LinkedHashMap<>();
    private final Set<KlystronNode> nodes=new LinkedHashSet<>();
    private final Set<KlystronNetwork> networks=new LinkedHashSet<>();
    boolean dirty;
    public KlystronNode getNode(BlockPos pos){return positions.get(pos);}
    public void createNode(KlystronNode node){if(nodes.contains(node))return;for(var pos:node.positions)if(positions.containsKey(pos))throw new IllegalArgumentException("Overlapping klystron node at "+pos);node.owner=this;node.expired=false;nodes.add(node);for(var pos:node.positions)positions.put(pos,node);dirty=true;}
    public void destroyNode(KlystronNode node){if(node==null||!nodes.remove(node))return;if(node.network!=null)node.network.invalidate();for(var pos:node.positions)positions.remove(pos,node);node.owner=null;node.network=null;node.expired=true;dirty=true;}
    public void tick(){if(!dirty)return;for(var network:networks)network.invalidate();networks.clear();var remaining=new LinkedHashSet<>(nodes);var queue=new ArrayDeque<KlystronNode>();while(!remaining.isEmpty()){var first=remaining.iterator().next();remaining.remove(first);queue.add(first);var network=new KlystronNetwork();networks.add(network);while(!queue.isEmpty()){var current=queue.removeFirst();network.addNode(current);for(var connection:current.connections){var next=positions.get(connection.target());if(next==null||next==current||!remaining.contains(next))continue;var reverse=new NodeConnection(connection.origin(),connection.direction().getOpposite());if(next.connections.contains(reverse)){remaining.remove(next);queue.addLast(next);}}}}dirty=false;}
    public void clear(){for(var network:networks)network.invalidate();for(var node:nodes){node.owner=null;node.network=null;node.expired=true;}nodes.clear();positions.clear();networks.clear();dirty=false;}
}
