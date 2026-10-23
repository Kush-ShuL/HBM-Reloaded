package com.hbm.pneumatic.anchor;
import api.hbm.ntl.IPneumaticConnector;
import com.hbm.pneumatic.*;
import com.hbm.pneumatic.tube.PneumaticTubeContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Original small10m anchor: saved reciprocal connections and exactly one local back face. */
public final class PneumaticAnchorBlockEntity extends BlockEntity implements IPneumaticConnector {
 private final List<BlockPos> connected=new ArrayList<>();private PneumaticNode node;
 public PneumaticAnchorBlockEntity(BlockPos pos,BlockState state){super(PneumaticTubeContent.ANCHOR_ENTITY.get(),pos,state);}
 public Direction back(){return getBlockState().getValue(PneumaticAnchorBlock.FACING).getOpposite();}@Override public boolean canConnectPneumatic(Direction side){return side==back();}public List<BlockPos> getConnected(){return Collections.unmodifiableList(connected);}public Vec3 connectionPoint(){return Vec3.atCenterOf(worldPosition);}public double maxPipeLength(){return 10;}
 public static void tick(Level level,BlockPos pos,BlockState state,PneumaticAnchorBlockEntity anchor){if(!level.isClientSide){anchor.node=PneumaticNetworks.connect(anchor);for(var target:anchor.connected)PneumaticNetworks.link(anchor,target);}}
 public static int canLink(PneumaticAnchorBlockEntity first,PneumaticAnchorBlockEntity second){if(first==second)return 2;return Math.min(first.maxPipeLength(),second.maxPipeLength())>=first.connectionPoint().distanceTo(second.connectionPoint())?0:3;}
 public void addConnection(BlockPos pos){connected.add(pos.immutable());if(level!=null&&!level.isClientSide)PneumaticNetworks.link(this,pos);sync();}
 public void disconnectAll(){if(level==null||level.isClientSide)return;for(var target:connected)if(!target.equals(worldPosition)&&level.hasChunkAt(target)&&level.getBlockEntity(target) instanceof PneumaticAnchorBlockEntity other){other.connected.removeIf(worldPosition::equals);PneumaticNetworks.unlink(other,worldPosition);other.sync();}PneumaticNetworks.disconnect(this);}
 public void sync(){setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
 @Override public void setRemoved(){PneumaticNetworks.disconnect(this);super.setRemoved();}@Override public void onChunkUnloaded(){PneumaticNetworks.disconnect(this);super.onChunkUnloaded();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("conCount",connected.size());for(int i=0;i<connected.size();i++){var p=connected.get(i);tag.putIntArray("con"+i,new int[]{p.getX(),p.getY(),p.getZ()});}}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);connected.clear();for(int i=0;i<tag.getInt("conCount");i++){int[] p=tag.getIntArray("con"+i);if(p.length==3)connected.add(new BlockPos(p[0],p[1],p[2]));}}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){loadAdditional(packet.getTag(),lookup);}
}
