package com.hbm.network.cargo;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.network.rtty.RadioTorchBlock;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
public final class DroneWaypointEntity extends BlockEntity implements DroneLinkable,MuffledMachine {
 public int height=5,nextX=-1,nextY=-1,nextZ=-1;public boolean muffled,tilted;
 public DroneWaypointEntity(BlockPos pos,BlockState state){super(CargoDroneContent.WAYPOINT_ENTITY.get(),pos,state);}
 public BlockPos getPoint(){var direction=RadioTorchBlock.outward(getBlockState());return direction==null?worldPosition:worldPosition.relative(direction,height);}
 public void setNextTarget(int x,int y,int z){nextX=x;nextY=y;nextZ=z;setChanged();}public void addHeight(int amount){height=net.minecraft.util.Mth.clamp(height+amount,1,15);setChanged();}
 public static void tick(Level level,BlockPos pos,BlockState state,DroneWaypointEntity tile){var point=tile.getPoint();if(!level.isClientSide){if(tile.nextY!=-1)for(var drone:level.getEntitiesOfClass(PatrolDrone.class,new AABB(point)))if(CargoDroneBase.sourceSpeed(drone.getDeltaMovement())<.05)drone.setTarget(tile.nextX+.5,tile.nextY,tile.nextZ+.5);var packet=tile.getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(((ServerLevel)level).getServer(),packet)){for(var player:((ServerLevel)level).players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<225)player.connection.send(packet);dispatchScope.commit();}}else if(tile.nextY!=-1&&level.getGameTime()%2==0){CargoVisuals.dust.accept(point);CargoVisuals.line.add(point.getX()+.5,point.getY()+.5,point.getZ()+.5,tile.nextX-point.getX(),tile.nextY-point.getY(),tile.nextZ-point.getZ(),0x0000ff);}}
 private void read(CompoundTag tag){height=tag.getInt("height");var p=tag.getIntArray("pos");if(p.length>=3){nextX=p[0];nextY=p[1];nextZ=p[2];}muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 private CompoundTag write(CompoundTag tag){tag.putInt("height",height);tag.putIntArray("pos",new int[]{nextX,nextY,nextZ});tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);return tag;}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);read(tag);}@Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);write(tag);}@Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return write(new CompoundTag());}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){read(tag);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection c,ClientboundBlockEntityDataPacket p,HolderLookup.Provider lookup){read(p.getTag());}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
}
