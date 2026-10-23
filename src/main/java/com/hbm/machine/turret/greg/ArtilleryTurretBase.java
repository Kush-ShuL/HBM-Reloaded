package com.hbm.machine.turret.greg;
import com.hbm.api.computer.*;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.energy.*;
import com.hbm.machine.turret.TurretBaseEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.radar.IRadarCommandReceiver;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
/** Actual TileEntityTurretBaseArtillery: coordinate queue,sky visibility,32literal old port positions and native radar recipient category. */
public abstract class ArtilleryTurretBase extends TurretBaseEntity implements IRadarCommandReceiver {
 protected final List<Vec3> targetQueue=new ArrayList<>();private final Map<BlockPos,PowerNode> artilleryNodes=new LinkedHashMap<>();private Direction nodeFacing;
 public record Port(BlockPos position,Direction face){}
 protected ArtilleryTurretBase(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 @Override protected float baseDamage(){return 0;}protected abstract boolean doLOSCheck();
 @Override public boolean sendCommandPosition(int x,int y,int z){enqueueTarget(x+.5,y,z+.5);return true;}@Override public boolean sendCommandEntity(Entity entity){enqueueTarget(entity.getX(),entity.getY(),entity.getZ());return true;}
 public void enqueueTarget(double x,double y,double z){var p=turretPosition();if(LegacyVector.createVectorHelper(x-p.x,y-p.y,z-p.z).lengthVector()<=detectorRange())targetQueue.add(new Vec3(x,y,z));}
 @Override public boolean entityInLOS(Entity entity){if(doLOSCheck())return super.entityInLOS(entity);var p=turretPosition();var e=entityPosition(entity);double length=LegacyVector.createVectorHelper(e.x-p.x,e.y-p.y,e.z-p.z).lengthVector();if(length<detectorGrace()||length>detectorRange()*1.1)return false;int x=(int)Math.floor(entity.getX()),z=(int)Math.floor(entity.getZ()),y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z)-1;var cursor=new BlockPos.MutableBlockPos(x,y,z);for(;y>=level.getMinBuildHeight();y--){cursor.setY(y);if(level.getBlockState(cursor).getLightBlock(level,cursor)>0)break;}return y+1<entity.getY()+entity.getBbHeight();}
 /** Literal original PortDef.make coordinates/global directions,including their source odd asymmetry;do not rotate the direction constants or collapse32ports into a pass-through. */
 public static List<Port> ports(BlockPos p,Direction facing){var out=new ArrayList<Port>(32);var d=facing.getOpposite();var r=d.getClockWise();int dx=d.getStepX(),dz=d.getStepZ(),rx=r.getStepX(),rz=r.getStepZ();for(int i=0;i<2;i++)for(int j=0;j<4;j++){out.add(new Port(p.offset(dx*(-1+j)+rx* -3,i,dz*(-1+j)+rz* -3-1),Direction.SOUTH));out.add(new Port(p.offset(dx*(-1+j)+rx*2,i,dz*(-1+j)+rz*2+1),Direction.NORTH));out.add(new Port(p.offset(dx* -2+rx*(1-j)-1,i,dz* -2+rz*(1-j)),Direction.EAST));out.add(new Port(p.offset(dx*3+rx*(1-j)+1,i,dz*3+rz*(1-j)),Direction.WEST));}return out;}
 public List<Port> ports(){return ports(worldPosition,DummyableBlock.facing(getBlockState()));}
 @Override protected void connect(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());if(nodeFacing!=facing){disconnectArtilleryPorts();nodeFacing=facing;}var space=HbmEnergy.nodespace(server);for(var p:ports()){var node=artilleryNodes.get(p.position());if(node==null||node.isExpired()){node=space.getNode(p.position());if(node==null||node.isExpired()){node=new PowerNode(p.position()).setConnections(new NodeConnection(p.position().relative(p.face()),p.face()));space.createNode(node);}artilleryNodes.put(p.position(),node);}if(node.hasValidNet())node.addReceiver(this);}}
 public boolean portAt(BlockPos p,Direction side){if(side==null)return false;for(var port:ports())if(port.position().equals(p)&&port.face()==side)return true;return false;}@Override public boolean canConnect(Direction side){return portAt(worldPosition,side);}
 private void disconnectArtilleryPorts(){if(level instanceof ServerLevel server)for(var node:new LinkedHashSet<>(artilleryNodes.values()))if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);artilleryNodes.clear();}@Override public void setRemoved(){disconnectArtilleryPorts();super.setRemoved();}@Override public void onChunkUnloaded(){disconnectArtilleryPorts();super.onChunkUnloaded();}
 @Override public String computerType(){return "ntm_artillery";}
 @Override public Object[] callComputer(String method,ComputerArguments args){if(method.equals("getCurrentTarget")){if(targetQueue.isEmpty())throw new ComputerException("No queued artillery target");var p=targetQueue.getFirst();return new Object[]{p.x,p.y,p.z};}if(method.equals("getTargetDistance")){double x=worldPosition.getX()-args.checkDouble(0),y=worldPosition.getY()-args.checkDouble(1),z=worldPosition.getZ()-args.checkDouble(2);return new Object[]{Math.sqrt(x*x+y*y+z*z)};}return super.callComputer(method,args);}
 @Override public String runRORFunction(String name,String[] params){super.runRORFunction(name,params);if((PREFIX_FUNCTION+"enqueue").equals(name)&&params.length>2){try{sendCommandPosition(Integer.parseInt(params[0]),Integer.parseInt(params[1]),Integer.parseInt(params[2]));setChanged();}catch(NumberFormatException ignored){}}return null;}
}
