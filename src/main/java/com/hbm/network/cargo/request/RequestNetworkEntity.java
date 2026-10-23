package com.hbm.network.cargo.request;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.network.cargo.CargoEffectPacket;
import com.hbm.bomb.nuclear.LegacyVector;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
/** Full original discover5/rescanall/24blockbidirectional LOS and2chunk neighborhood. */
public abstract class RequestNetworkEntity extends BlockEntity implements MuffledMachine {
 public final CargoHashedSet<RequestNetwork.PathNode> reachableNodes=new CargoHashedSet<>(),knownNodes=new CargoHashedSet<>();public static final int MAX_RANGE=24;public boolean muffled,tilted;
 protected RequestNetworkEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 public void networkTick(){if(!(level instanceof ServerLevel server)||server.getGameTime()%20!=0)return;var pos=getCoord();var newNode=createNode(pos);newNode.active=!server.hasNeighborSignal(worldPosition);push(server,newNode);var localNodes=getAllLocalNodes(server,worldPosition.getX(),worldPosition.getZ(),2);localNodes.remove(pos);var known=knownNodes.iterator();while(known.hasNext()){var node=known.next();if(!localNodes.contains(node)){reachableNodes.remove(node);known.remove();}}
  for(var node:knownNodes)if(reachableNodes.contains(node))CargoEffectPacket.send(server,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,(node.pos.getX()-pos.getX())/2D,(node.pos.getY()-pos.getY())/2D,(node.pos.getZ()-pos.getZ())/2D,0x00ff00);
  for(var node:knownNodes){if(hasPath(server,pos,node.pos))reachableNodes.add(node);else reachableNodes.remove(node);}int limit=5;for(var node:localNodes){if(!areNodesConnectable(node,newNode))continue;if(!knownNodes.contains(node)&&!node.equals(pos)){limit--;knownNodes.add(node);if(hasPath(server,pos,node.pos))reachableNodes.add(node);}if(limit<=0)break;}
 }
 public abstract RequestNetwork.PathNode createNode(BlockPos pos);public BlockPos getCoord(){return worldPosition.above();}public static boolean areNodesConnectable(RequestNetwork.PathNode a,RequestNetwork.PathNode b){return a.torchWaypoint||b.torchWaypoint;}
 public static boolean hasPath(ServerLevel level,BlockPos a,BlockPos b){var av=Vec3.atCenterOf(a);var bv=Vec3.atCenterOf(b);if(LegacyVector.createVectorHelper(av.x-bv.x,av.y-bv.y,av.z-bv.z).lengthVector()>MAX_RANGE)return false;var first=level.clip(new ClipContext(av,bv,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,net.minecraft.world.phys.shapes.CollisionContext.empty()));var second=level.clip(new ClipContext(bv,av,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,net.minecraft.world.phys.shapes.CollisionContext.empty()));return first.getType()==HitResult.Type.MISS&&second.getType()==HitResult.Type.MISS;}
 public static void push(ServerLevel level,RequestNetwork.PathNode node){var byChunk=RequestNetwork.activeWaypoints.computeIfAbsent(level,key->new HashMap<>());byChunk.computeIfAbsent(new ChunkPos(node.pos),key->new CargoHashedSet<>()).add(node);}
 public static CargoHashedSet<RequestNetwork.PathNode> getAllLocalNodes(ServerLevel level,int x,int z,int range){var result=new CargoHashedSet<RequestNetwork.PathNode>();var positions=new CargoHashedSet<BlockPos>();x>>=4;z>>=4;var byChunk=RequestNetwork.activeWaypoints.get(level);if(byChunk==null)return result;for(int i=-range;i<=range;i++)for(int j=-range;j<=range;j++){var list=byChunk.get(new ChunkPos(x+i,z+j));if(list!=null)for(var node:list)if(!positions.contains(node.pos))result.add(node);}return result;}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}@Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
}
