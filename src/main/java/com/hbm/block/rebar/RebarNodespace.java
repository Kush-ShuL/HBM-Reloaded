package com.hbm.block.rebar;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
/** Source separate RebarNetwork has no distribution tick; it only tracks six-face connected components. */
public final class RebarNodespace {
 private static final Map<ServerLevel,Space> WORLDS=new WeakHashMap<>();
 private static Space space(ServerLevel level){return WORLDS.computeIfAbsent(level,k->new Space());}
 static void register(ServerLevel level,RebarBlockEntity machine){var space=space(level);if(space.nodes.put(machine.getBlockPos(),machine)!=machine)space.components.clear();}
 static void remove(ServerLevel level,RebarBlockEntity machine){var space=WORLDS.get(level);if(space!=null&&space.nodes.remove(machine.getBlockPos(),machine))space.components.clear();}
 static List<RebarBlockEntity> component(ServerLevel level,RebarBlockEntity origin){var space=space(level);if(space.nodes.get(origin.getBlockPos())!=origin)return List.of();var cached=space.components.get(origin.getBlockPos());if(cached!=null)return cached;var queue=new ArrayDeque<BlockPos>();var visited=new LinkedHashSet<BlockPos>();var nodes=new ArrayList<RebarBlockEntity>();queue.add(origin.getBlockPos());visited.add(origin.getBlockPos());while(!queue.isEmpty()){var pos=queue.removeFirst();var machine=space.nodes.get(pos);if(machine==null||machine.isRemoved()||!level.hasChunkAt(pos))continue;nodes.add(machine);for(Direction direction:Direction.values()){var next=pos.relative(direction);if(space.nodes.containsKey(next)&&visited.add(next))queue.add(next);}}var result=List.copyOf(nodes);for(var machine:nodes)space.components.put(machine.getBlockPos(),result);return result;}
 private static final class Space {final Map<BlockPos,RebarBlockEntity> nodes=new LinkedHashMap<>();final Map<BlockPos,List<RebarBlockEntity>> components=new HashMap<>();}
 private RebarNodespace(){}
 /** Drop only this server's node registry and its transient connected-component cache. */
 public static void clearServer(net.minecraft.server.MinecraftServer server){
  WORLDS.entrySet().removeIf(entry->{
   if(entry.getKey().getServer()!=server)return false;
   var space=entry.getValue();
   space.nodes.values().forEach(RebarBlockEntity::expireNetworkNode);
   space.components.clear();space.nodes.clear();
   return true;
  });
 }
}
