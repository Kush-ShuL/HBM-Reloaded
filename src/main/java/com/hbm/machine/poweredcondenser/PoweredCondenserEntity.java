package com.hbm.machine.poweredcondenser;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Real TileEntityCondenserPowered, including its passive-parent tick and 95% batch energy tolerance. */
public final class PoweredCondenserEntity extends BlockEntity implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable,MachineView,MuffledMachine {
 public long power;public final FluidTank[] tanks={new FluidTank(HbmFluids.SPENTSTEAM,PoweredCondenserConfig.inputTankSizeP),new FluidTank(HbmFluids.WATER,PoweredCondenserConfig.outputTankSizeP)};
 public int age,waterTimer,throughput;public float spin,lastSpin;public boolean muffled,tilted;
 public static Consumer<PoweredCondenserEntity> clientTick=machine->{};
 private List<NodeConnection> active=List.of();private final Map<BlockPos,PowerNode> powerNodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private CompoundTag lastSent;
 public PoweredCondenserEntity(BlockPos pos,BlockState state){super(PoweredCondenserContent.ENTITY.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 /** TilePortShapes.condenser: positions relative to actual core, outward target one further. */
 public static List<NodeConnection> connections(BlockPos core,Direction dir){var rot=dir.getClockWise();var up=core.above();return List.of(point(up.relative(rot,3),rot),point(up.relative(rot,-3),rot.getOpposite()),point(up.relative(dir).relative(rot,-1),dir),point(up.relative(dir).relative(rot),dir),point(up.relative(dir,-1).relative(rot,-1),dir.getOpposite()),point(up.relative(dir,-1).relative(rot),dir.getOpposite()));}
 private static NodeConnection point(BlockPos origin,Direction direction){return new NodeConnection(origin.relative(direction),direction);}
 public boolean portConnects(BlockPos pos,Direction side){return side!=null&&connections(worldPosition,DummyableBlock.facing(getBlockState())).stream().anyMatch(c->c.origin().equals(pos)&&c.direction()==side);}
 public static void tick(Level level,BlockPos pos,BlockState state,PoweredCondenserEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else machine.tickClient();}
 private void serverTick(ServerLevel server){age++;if(age>=2)age=0;if(waterTimer>0)waterTimer--;int convert=Math.min(tanks[0].getFill(),tanks[1].getMaxFill()-tanks[1].getFill());throughput=convert;
  if(power>=(convert*PoweredCondenserConfig.powerConsumption)*.95){tanks[0].setFill(tanks[0].getFill()-convert);if(convert>0)waterTimer=20;tanks[1].setFill(tanks[1].getFill()+convert);power-=convert*PoweredCondenserConfig.powerConsumption;if(power<0)power=0;}
  // Source conversion happens before updatePortPIFIFO, not after receiving this tick's resources.
  updatePorts(server);setChanged();var tag=getUpdateTag(server.registryAccess());if(!tag.equals(lastSent)||server.getGameTime()%20==0){lastSent=tag.copy();var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);dispatchScope.commit();}}
 }
 private void tickClient(){lastSpin=spin;if(waterTimer>0){spin+=30;if(spin>=360){spin-=360;lastSpin-=360;}}clientTick.accept(this);}
 private void updatePorts(ServerLevel server){var loaded=connections(worldPosition,DummyableBlock.facing(getBlockState())).stream().filter(c->server.hasChunkAt(c.origin())&&server.getBlockState(c.origin()).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(c.origin()))).toList();if(!loaded.equals(active)||powerNodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();active=List.copyOf(loaded);for(var c:loaded){var node=new PowerNode(c.origin()).setConnections(c);HbmEnergy.nodespace(server).createNode(node);powerNodes.put(c.origin(),node);}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.stream().map(NodeConnection::origin).toArray(BlockPos[]::new),loaded.toArray(NodeConnection[]::new));}if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:powerNodes.values())if(node.hasValidNet())node.addReceiver(this);}
 private void disconnect(){if(level instanceof ServerLevel server){for(var node:powerNodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}powerNodes.clear();active=List.of();fluidPorts=null;}
 @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
 @Override public long getPower(){return power;}@Override public void setPower(long value){power=value;setChanged();}@Override public long getMaxPower(){return PoweredCondenserConfig.maxPower;}@Override public long energyStored(){return power;}@Override public long energyCapacity(){return getMaxPower();}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public FluidTank getTankToPaste(){return null;}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rest=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rest!=amount)setChanged();return rest;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
 private void writeState(CompoundTag tag){tanks[0].writeToNBT(tag,"water");tanks[1].writeToNBT(tag,"steam");tag.putLong("power",power);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}private void readState(CompoundTag tag){tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");power=tag.getLong("power");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);writeState(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readState(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeState(tag);tag.putByte("waterTimer",(byte)waterTimer);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);waterTimer=tag.getByte("waterTimer");}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
