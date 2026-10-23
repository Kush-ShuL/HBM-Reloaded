package com.hbm.block.light.floodlight;
import com.hbm.api.machine.MachineView;
import com.hbm.block.light.SpotlightBlock;
import com.hbm.energy.*;
import com.hbm.multiblock.MuffledMachine;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Exact source fifteen endpoint state machine and old long5000 capacity, attached only to the private arbitrary-precision HE graph. */
public final class FloodlightBlockEntity extends BlockEntity implements EnergyReceiver,EnergyConductor,MachineView,MuffledMachine {
 public static final long MAX_POWER=5000;public long power;public float rotation;public int delay;public boolean isOn,muffled,tilted;public final BlockPos[] lightPos=new BlockPos[15];private PowerNode node;private Direction port;private boolean loaded=true;
 public FloodlightBlockEntity(BlockPos pos,BlockState state){super(FloodlightContent.ENTITY.get(),pos,state);}
 private Direction port(){if(port==null)port=Direction.from3DDataValue(getBlockState().getValue(SpotlightBlock.META)%6).getOpposite();return port;}
 public static void tick(Level level,BlockPos pos,BlockState state,FloodlightBlockEntity lamp){if(level instanceof ServerLevel server)lamp.serverTick(server);}
 private void serverTick(ServerLevel server){if(node==null||node.isExpired()){node=new PowerNode(worldPosition).setConnections(new NodeConnection(worldPosition.relative(port()),port()));HbmEnergy.nodespace(server).createNode(node);}if(node.hasValidNet())node.addReceiver(this);if(delay>0){delay--;return;}if(power>=100){power-=100;if(!isOn){isOn=true;for(int i=0;i<15;i++)castLight(i);setChanged();sync();}else{long time=server.getGameTime();if(time%5==0)castLight((int)Math.abs(time/5%15));}}else if(isOn){isOn=false;delay=60;destroyLights();setChanged();sync();}}
 private void castLight(int index){var next=getRayEndpoint(index);var previous=lightPos[index];lightPos[index]=null;if(next==null||!next.equals(previous))if(previous!=null&&level.getBlockEntity(previous)instanceof FloodlightBeamEntity beam&&beam.cache==this)level.setBlock(previous,Blocks.AIR.defaultBlockState(),2);if(next==null)return;if(level.getBlockState(next).isAir()){level.setBlock(next,FloodlightContent.BEAM.get().defaultBlockState(),2);if(level.getBlockEntity(next)instanceof FloodlightBeamEntity beam)beam.setSource(this,index);lightPos[index]=next;}if(level.getBlockState(next).is(FloodlightContent.BEAM.get()))lightPos[index]=next;}
 public BlockPos getRayEndpoint(int index){if(index<0||index>=15)return null;int meta=getBlockState().getValue(SpotlightBlock.META);double x=1,y=0,z=0;float pitch=rotation;if(meta==1||meta==7||meta==6)pitch=180-pitch;float a=(float)(pitch/180D*Math.PI)+(((index/3)-2)*7.5F)/180F*(float)Math.PI,c=Mth.cos(a),s=Mth.sin(a);double nx=x*c+y*s,ny=y*c-x*s;x=nx;y=ny;
  if(meta==6||meta==7||meta==2){a=(float)(Math.PI/2D);c=Mth.cos(a);s=Mth.sin(a);nx=x*c+z*s;double nz=z*c-x*s;x=nx;z=nz;}if(meta==3){a=(float)-(Math.PI/2D);c=Mth.cos(a);s=Mth.sin(a);nx=x*c+z*s;double nz=z*c-x*s;x=nx;z=nz;}if(meta==4){a=(float)Math.PI;c=Mth.cos(a);s=Mth.sin(a);nx=x*c+z*s;double nz=z*c-x*s;x=nx;z=nz;}a=(((index%3)-1)*15F)/180F*(float)Math.PI;c=Mth.cos(a);s=Mth.sin(a);nx=x*c+z*s;z=z*c-x*s;x=nx;
  for(int i=1;i<64;i++){var pos=new BlockPos((int)Math.floor(worldPosition.getX()+.5+x*i),(int)Math.floor(worldPosition.getY()+.5+y*i),(int)Math.floor(worldPosition.getZ()+.5+z*i));if(pos.equals(worldPosition))continue;if(!FloodlightOpacity.blocks(level,pos))continue;if(i>1)return new BlockPos((int)Math.floor(worldPosition.getX()+.5+x*(i-1)),(int)Math.floor(worldPosition.getY()+.5+y*(i-1)),(int)Math.floor(worldPosition.getZ()+.5+z*(i-1)));}return null;
 }
 public void destroyLights(){if(level==null)return;for(var pos:lightPos)if(pos!=null&&level.getBlockState(pos).is(FloodlightContent.BEAM.get()))level.setBlock(pos,Blocks.AIR.defaultBlockState(),2);}
 public void sync(){if(level!=null&&!level.isClientSide)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
 @Override public long getPower(){return power;}@Override public long getMaxPower(){return MAX_POWER;}@Override public void setPower(long amount){power=amount;setChanged();}@Override public long energyStored(){return power;}@Override public long energyCapacity(){return MAX_POWER;}@Override public PowerNode energyNode(){return node;}@Override public boolean canConnect(Direction side){return side!=null&&side==port();}@Override public boolean isLoaded(){return loaded&&!isRemoved()&&level!=null;}
 private void disconnect(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
 @Override public void onLoad(){super.onLoad();loaded=true;}@Override public void setRemoved(){loaded=false;disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){loaded=false;disconnect();super.onChunkUnloaded();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putFloat("rotation",rotation);tag.putLong("power",power);tag.putBoolean("isOn",isOn);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");rotation=tag.getFloat("rotation");power=tag.getLong("power");isOn=tag.getBoolean("isOn");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadAdditional(tag,lookup);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
