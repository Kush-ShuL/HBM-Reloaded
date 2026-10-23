package com.hbm.machine.reactor.iter.utilities;
import com.hbm.api.computer.*;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.fusion.plasma.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original fusion boiler, immediate heat callback and fixed water→superhotsteam buffers; TU is not HE or fluid plasma. */
public final class FusionBoilerEntity extends FusionUtilityEntity implements StandardFluidTransceiver,FusionPowerReceiver,ComputerControl {
 public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("WATER"),32000),new FluidTank(HbmFluids.fromName("SUPERHOTSTEAM"),32000)};
 public long plasmaEnergy,plasmaEnergySync;private PlasmaNode plasmaNode;private FluidPorts fluidPorts;
 public FusionBoilerEntity(BlockPos pos,BlockState state){super(FusionUtilityContent.BOILER_ENTITY.get(),pos,state);}
 public static NodeConnection[] portConnections(BlockPos core,Direction dir){var side=dir.getClockWise();var points=new BlockPos[]{core.relative(dir,-1).relative(side),core.relative(dir,-1).relative(side,-1),core.relative(dir,2).relative(side),core.relative(dir,2).relative(side,-1)};var faces=new Direction[]{side,side.getOpposite(),side,side.getOpposite()};var result=new NodeConnection[4];for(int i=0;i<result.length;i++)result[i]=new NodeConnection(points[i].relative(faces[i]),faces[i]);return result;}
 public static void tick(Level level,BlockPos pos,BlockState state,FusionBoilerEntity machine){if(!(level instanceof ServerLevel server))return;var connections=portConnections(pos,DummyableBlock.facing(state));if(machine.fluidPorts==null)machine.fluidPorts=new FluidPorts(Arrays.stream(connections).map(NodeConnection::origin).toArray(BlockPos[]::new),connections);machine.fluidPorts.update(server,machine);machine.plasmaEnergySync=machine.plasmaEnergy;machine.plasmaEnergy=0;var direction=DummyableBlock.facing(state).getOpposite();var space=PlasmaNetworks.nodespace(server);if(machine.plasmaNode==null||machine.plasmaNode.isExpired()){var origin=pos.relative(direction,4).above(2);machine.plasmaNode=space.getNode(origin);if(machine.plasmaNode==null){machine.plasmaNode=new PlasmaNode(origin).setConnections(new NodeConnection(origin.relative(direction),direction));space.createNode(machine.plasmaNode);}}if(machine.plasmaNode.hasValidNet())machine.plasmaNode.addReceiver(machine);machine.sync(50);}
 @Override public boolean receivesFusionPower(){return true;}@Override public void receiveFusionPower(long fusionPower,double neutronPower,float r,float g,float b){plasmaEnergy=fusionPower;int waterCycles=Math.min(tanks[0].getFill(),tanks[1].getMaxFill()-tanks[1].getFill());int steamCycles=(int)Math.min(fusionPower/tanks[0].getTankType().getTrait(FluidTraits.Heatable.class).getFirstStep().heatReq(),waterCycles);if(steamCycles>0){tanks[0].setFill(tanks[0].getFill()-steamCycles);tanks[1].setFill(tanks[1].getFill()+steamCycles);if(level instanceof ServerLevel server&&server.random.nextInt(200)==0)server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+2,worldPosition.getZ()+.5,HbmSounds.get("block.boilerGroan"),SoundSource.BLOCKS,2.5F,1F);setChanged();}}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public boolean isLoaded(){return level!=null&&!isRemoved()&&level.hasChunkAt(worldPosition);}
 @Override protected void saveMachine(CompoundTag tag){tanks[0].writeToNBT(tag,"t0");tanks[1].writeToNBT(tag,"t1");}@Override protected void loadMachine(CompoundTag tag){tanks[0].readFromNBT(tag,"t0");tanks[1].readFromNBT(tag,"t1");}@Override protected void writeSync(CompoundTag tag){super.writeSync(tag);tag.putLong("plasmaEnergy",plasmaEnergySync);}@Override protected void readSync(CompoundTag tag){super.readSync(tag);plasmaEnergy=tag.getLong("plasmaEnergy");plasmaEnergySync=plasmaEnergy;}
 private void disconnect(){if(level instanceof ServerLevel server){if(fluidPorts!=null)fluidPorts.remove(server);if(plasmaNode!=null)PlasmaNetworks.nodespace(server).destroyNode(plasmaNode);}fluidPorts=null;plasmaNode=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override public String computerType(){return "ntm_fusion_boiler";}@Override public String[] computerMethods(){return new String[]{"getPlasmaEnergy","getFluid","getInfo"};}@Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getPlasmaEnergy"->new Object[]{plasmaEnergySync};case "getFluid"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};case "getInfo"->new Object[]{plasmaEnergySync,tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};default->throw new ComputerException("No such method: "+method);};}
}
