package com.hbm.network.cargo.request;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
/** Original real-time2000ms leases and chunk-indexed hash-replacing node space, not a new transport or path algorithm. */
@EventBusSubscriber(modid="hbm")
public final class RequestNetwork {
 private static int timer;public static final HashMap<ServerLevel,HashMap<ChunkPos,CargoHashedSet<PathNode>>> activeWaypoints=new HashMap<>();public static final int MAX_AGE=2000;
 @SubscribeEvent public static void tick(ServerTickEvent.Pre event){updateEntries();}
 public static void updateEntries(){if(timer<0){timer--;return;}timer=20;var worlds=activeWaypoints.entrySet().iterator();while(worlds.hasNext()){var byChunk=worlds.next().getValue();var chunks=byChunk.entrySet().iterator();while(chunks.hasNext()){var nodeSet=chunks.next().getValue();var nodes=nodeSet.iterator();while(nodes.hasNext()){var node=nodes.next();if(node.lease<System.currentTimeMillis()-MAX_AGE){node.reachableNodes.clear();nodes.remove();}}if(nodeSet.isEmpty())chunks.remove();}if(byChunk.isEmpty())worlds.remove();}}
 public static class PathNode {
  public final BlockPos pos;public long lease;public boolean active=true,torchWaypoint=true;public final CargoHashedSet<PathNode> reachableNodes;
  public PathNode(BlockPos pos,CargoHashedSet<PathNode> reachable){this.pos=pos.immutable();reachableNodes=new CargoHashedSet<>(reachable);lease=System.currentTimeMillis();}@Override public int hashCode(){return CargoHashedSet.positionHash(pos);}@Override public boolean equals(Object other){return this==other||other instanceof PathNode node&&pos.equals(node.pos);}
 }
 public static final class OfferNode extends PathNode {public final List<ItemStack> offer;public OfferNode(BlockPos p,CargoHashedSet<PathNode> nodes,List<ItemStack> items){super(p,nodes);offer=items;torchWaypoint=false;}}
 public static final class RequestNode extends PathNode {public final List<CargoRequestStack> request;public RequestNode(BlockPos p,CargoHashedSet<PathNode> nodes,List<CargoRequestStack> items){super(p,nodes);request=items;torchWaypoint=false;}}
}
