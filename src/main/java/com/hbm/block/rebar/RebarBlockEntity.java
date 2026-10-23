package com.hbm.block.rebar;
import com.hbm.api.machine.MachineView;
import com.hbm.block.pipe.NativePipeBlockEntity;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source concrete receiver: fill only the network's lowest layer, equalize its target, max50mB/link per transfer. */
public final class RebarBlockEntity extends BlockEntity implements FluidReceiver,MachineView {
 public int progress,prevProgress;public boolean hasConnection;private boolean nodeReady;private BlockState concrete;private FluidPorts fluidPorts;private CompoundTag lastSent;
 public RebarBlockEntity(BlockPos pos,BlockState state){super(RebarContent.ENTITY.get(),pos,state);}
 public void setup(BlockState concrete){this.concrete=concrete;setChanged();}
 public void updateConnection(){if(level==null)return;boolean connected=false;for(Direction side:Direction.values())if(level.hasChunkAt(worldPosition.relative(side))&&level.getBlockEntity(worldPosition.relative(side)) instanceof NativePipeBlockEntity){connected=true;break;}if(hasConnection!=connected){hasConnection=connected;setChanged();}}
 @Override public void onLoad(){super.onLoad();updateConnection();}
 public static void tick(Level level,BlockPos pos,BlockState state,RebarBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
 private void serverTick(ServerLevel server){if(hasConnection){if(fluidPorts==null)fluidPorts=new FluidPorts(new BlockPos[]{worldPosition},new NodeConnection[]{new NodeConnection(worldPosition.above(),Direction.UP)});fluidPorts.update(server,this);}else if(fluidPorts!=null){fluidPorts.remove(server);fluidPorts=null;}if(prevProgress!=progress){setChanged();prevProgress=progress;}if(progress>=1000){server.setBlock(worldPosition,concrete!=null&&RebarConcrete.valid(concrete)?concrete:RebarConcrete.fallback(),3);return;}if(!nodeReady){RebarNodespace.register(server,this);nodeReady=true;}var tag=getUpdateTag(server.registryAccess());if(!tag.equals(lastSent)||server.getGameTime()%20==0){lastSent=tag;var packet=ClientboundBlockEntityDataPacket.create(this,(ignored,lookup)->tag);for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);}}
 @Override public boolean canConnect(HbmFluidType type,Direction side){return side==Direction.UP&&type==HbmFluids.fromName("CONCRETE");}
 @Override public FluidTank[] getAllTanks(){var tank=new FluidTank(HbmFluids.fromName("CONCRETE"),1000);tank.setFill(progress);return new FluidTank[]{tank};}
 @Override public long getDemand(HbmFluidType type,int pressure){return 10000;}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){if(type!=HbmFluids.fromName("CONCRETE")||!nodeReady||!(level instanceof ServerLevel server))return amount;var links=RebarNodespace.component(server,this);if(links.isEmpty())return amount;var lowest=new ArrayList<RebarBlockEntity>();int lowestY=server.getMaxBuildHeight();long current=0,capacity=0;for(var node:links){int y=node.worldPosition.getY();if(y<lowestY){lowestY=y;current=0;capacity=0;lowest.clear();}if(y==lowestY){current+=node.progress;capacity+=1000;lowest.add(node);}}if(!lowest.isEmpty()&&capacity>0){long accepted=Math.min(Math.min(capacity-current,amount),50L*lowest.size());int target=(int)Math.min((current+accepted)/lowest.size(),1000);for(var rebar:lowest){if(rebar.progress>=target)continue;int delta=target-rebar.progress;if(delta>amount)continue;rebar.progress+=delta;amount-=delta;rebar.setChanged();}}return amount;}
 private void disconnect(){if(level instanceof ServerLevel server){if(fluidPorts!=null)fluidPorts.remove(server);RebarNodespace.remove(server,this);}fluidPorts=null;nodeReady=false;}
 @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("progress",progress);tag.putBoolean("hasConnection",hasConnection);if(concrete!=null){tag.put("concreteState",NbtUtils.writeBlockState(concrete));tag.putString("block",BuiltInRegistries.BLOCK.getKey(concrete.getBlock()).toString());var property=concrete.getBlock().getStateDefinition().getProperty("legacy_meta");if(property!=null)tag.putInt("meta",Integer.parseInt(value(concrete,property)));}}
 @SuppressWarnings({"rawtypes","unchecked"})private static String value(BlockState state,net.minecraft.world.level.block.state.properties.Property property){return property.getName(state.getValue(property));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);progress=tag.getInt("progress");hasConnection=tag.getBoolean("hasConnection");if(tag.contains("concreteState"))concrete=NbtUtils.readBlockState(lookup.lookupOrThrow(Registries.BLOCK),tag.getCompound("concreteState"));else if(tag.contains("block",8)){var item=new net.minecraft.world.item.ItemStack(BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(tag.getString("block"))));item.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),tag.getInt("meta"));concrete=RebarConcrete.state(item);}}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putInt("progress",progress);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){progress=tag.getInt("progress");}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){return List.of(new TankView(HbmFluids.fromName("CONCRETE"),progress,1000));}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.rebar.curing",progress,1000,0xa0a0a0));}
}
