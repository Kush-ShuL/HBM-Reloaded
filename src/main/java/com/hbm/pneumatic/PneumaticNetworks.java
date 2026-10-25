package com.hbm.pneumatic;

import api.hbm.ntl.IPneumaticConnector;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** World-scoped real connected-component graph; never chunk-loads or exposes an external item/fluid storage. */
@EventBusSubscriber(modid="hbm")
public final class PneumaticNetworks {
 private static final Map<Level,Space> WORLDS=new WeakHashMap<>();
 private static final class Space{final Map<BlockPos,PneumaticNode> nodes=new LinkedHashMap<>();final Set<PneumaticNetwork> networks=new LinkedHashSet<>();boolean dirty;}
 private static int mask(BlockEntity owner){if(!(owner instanceof IPneumaticConnector connector))return 0;int result=0;for(var side:Direction.values())if(connector.canConnectPneumatic(side))result|=1<<side.get3DDataValue();return result;}
 private static PneumaticNode restore(Space space,BlockEntity owner){var node=space.nodes.get(owner.getBlockPos());if(node!=null&&node.owner!=owner){if(node.unloaded&&node.owner.getBlockState().getBlock()==owner.getBlockState().getBlock())node.owner=owner;else{remove(space,node);node=null;}}if(node!=null)node.unloaded=false;return node;}
 public static PneumaticNode loaded(BlockEntity owner){var world=owner.getLevel();if(world==null||world.isClientSide)return null;var space=WORLDS.get(world);return space==null?null:restore(space,owner);}
 public static void unloaded(BlockEntity owner){var space=WORLDS.get(owner.getLevel());if(space==null)return;var node=space.nodes.get(owner.getBlockPos());if(node!=null&&node.owner==owner)node.unloaded=true;}
 public static PneumaticNode connect(BlockEntity owner){var world=owner.getLevel();if(world==null||world.isClientSide||owner.isRemoved())return null;var space=WORLDS.computeIfAbsent(world,w->new Space());int mask=mask(owner);var node=restore(space,owner);if(node==null){node=new PneumaticNode(owner,mask);space.nodes.put(owner.getBlockPos().immutable(),node);space.dirty=true;}else if(node.mask!=mask){node.mask=mask;space.dirty=true;}return node;}
 public static void disconnect(BlockEntity owner){var world=owner.getLevel();var space=WORLDS.get(world);if(space==null)return;var node=space.nodes.get(owner.getBlockPos());if(node!=null&&node.owner==owner)remove(space,node);}
 private static void remove(Space space,PneumaticNode node){space.nodes.remove(node.position(),node);if(node.network!=null)node.network.destroy();node.network=null;node.expired=true;space.dirty=true;}
 public static void link(BlockEntity owner,BlockPos target){var node=connect(owner);if(node!=null&&node.remote.add(target.immutable()))WORLDS.get(owner.getLevel()).dirty=true;}
 public static void unlink(BlockEntity owner,BlockPos target){var node=connect(owner);if(node!=null&&node.remote.remove(target))WORLDS.get(owner.getLevel()).dirty=true;}
 @SubscribeEvent public static void tick(LevelTickEvent.Post event){if(event.getLevel().isClientSide())return;var space=WORLDS.get(event.getLevel());if(space==null)return;for(var node:List.copyOf(space.nodes.values()))if(!node.unloaded&&node.owner.isRemoved())remove(space,node);if(space.dirty)rebuild(space);for(var network:space.networks)network.update();}
 @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())return;var space=WORLDS.remove(event.getLevel());if(space==null)return;for(var network:space.networks)network.destroy();for(var node:space.nodes.values()){node.expired=true;node.network=null;}}
 private static void rebuild(Space space){for(var network:space.networks)network.destroy();space.networks.clear();var remaining=new LinkedHashSet<>(space.nodes.values());var queue=new ArrayDeque<PneumaticNode>();while(!remaining.isEmpty()){var first=remaining.iterator().next();remaining.remove(first);queue.add(first);var network=new PneumaticNetwork();space.networks.add(network);while(!queue.isEmpty()){var node=queue.removeFirst();node.network=network;network.nodes.add(node);for(var side:Direction.values()){if((node.mask&(1<<side.get3DDataValue()))==0)continue;var next=space.nodes.get(node.position().relative(side));if(next!=null&&(next.mask&(1<<side.getOpposite().get3DDataValue()))!=0&&remaining.remove(next))queue.addLast(next);}for(var pos:node.remote){var next=space.nodes.get(pos);if(next!=null&&next.remote.contains(node.position())&&remaining.remove(next))queue.addLast(next);}}}space.dirty=false;}
 private PneumaticNetworks(){}
 /** Expire the original pneumatic graph/cache handles; actual inventories remain on their owners. */
 public static void clearServer(net.minecraft.server.MinecraftServer server){
  WORLDS.entrySet().removeIf(entry->{
   if(!(entry.getKey() instanceof net.minecraft.server.level.ServerLevel level)||level.getServer()!=server)return false;
   var space=entry.getValue();
   for(var network:space.networks)network.destroy();
   for(var node:space.nodes.values()){node.expired=true;node.network=null;}
   space.nodes.clear();space.networks.clear();space.dirty=false;
   return true;
  });
 }
}
