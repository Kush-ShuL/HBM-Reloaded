package com.hbm.machine.reactor.iter.mhdt;
import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.computer.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.fusion.plasma.*;
import com.hbm.machine.reactor.iter.utilities.FusionUtilityEntity;
import com.hbm.multiblock.DummyableBlock;
import java.math.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Exact source MHDT tick/cooling behavior, real plasma receiver and private HE provider. */
public final class MhdtEntity extends FusionUtilityEntity implements EnergyProvider,EnergyConductor,StandardFluidTransceiver,FusionPowerReceiver,ComputerControl,IRORValueProvider {
 public static final int COOLANT_USE=50;public static final double PLASMA_EFFICIENCY=1.35D;
 public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("PERFLUOROMETHYL_COLD"),4000),new FluidTank(HbmFluids.fromName("PERFLUOROMETHYL"),4000)};
 public long plasmaEnergy,plasmaEnergySync,minimumPlasma=MhdtConfig.minimumPlasma;public float rotor,prevRotor,rotorSpeed;
 private BigInteger power=BigInteger.ZERO;private PowerNode powerNode;private FluidPorts fluidPorts;private PlasmaNode plasmaNode;
 public static Consumer<MhdtEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
 public MhdtEntity(BlockPos pos,BlockState state){super(MhdtContent.ENTITY.get(),pos,state);}
 public static NodeConnection[] portConnections(BlockPos core,Direction dir){var side=dir.getClockWise();var points=new BlockPos[]{core.relative(dir,4).relative(side,3),core.relative(dir,4).relative(side,-3),core.relative(dir,7).above()};var faces=new Direction[]{side,side.getOpposite(),dir};var result=new NodeConnection[3];for(int i=0;i<3;i++)result[i]=new NodeConnection(points[i].relative(faces[i]),faces[i]);return result;}
 public boolean hasMinimumPlasma(){return plasmaEnergy>=minimumPlasma;}public boolean isCool(){return tanks[0].getFill()>=COOLANT_USE&&tanks[1].getFill()+COOLANT_USE<=tanks[1].getMaxFill();}
 public static BigInteger generation(long plasma,boolean full){double original=Math.floor(plasma*PLASMA_EFFICIENCY);BigInteger result=Double.isFinite(original)&&original>=-0x1.0p63&&original<0x1.0p63?BigInteger.valueOf((long)original):BigDecimal.valueOf(plasma).multiply(BigDecimal.valueOf(PLASMA_EFFICIENCY)).setScale(0,RoundingMode.FLOOR).toBigInteger();return full?result:result.divide(BigInteger.TWO);}
 public static void tick(Level level,BlockPos pos,BlockState state,MhdtEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else{machine.rotorSpeed+=machine.plasmaEnergy>0&&machine.isCool()?.125F:-.125F;machine.rotorSpeed=net.minecraft.util.Mth.clamp(machine.rotorSpeed,0,machine.hasMinimumPlasma()?15:10);machine.prevRotor=machine.rotor;machine.rotor+=machine.rotorSpeed;if(machine.rotor>=360){machine.rotor-=360;machine.prevRotor-=360;}CLIENT_TICK.accept(machine);}}
 private void serverTick(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());var connections=portConnections(worldPosition,facing);var positions=Arrays.stream(connections).map(NodeConnection::origin).toArray(BlockPos[]::new);if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(positions).setConnections(connections);HbmEnergy.nodespace(server).createNode(powerNode);}if(powerNode.hasValidNet())powerNode.addProvider(this);if(fluidPorts==null)fluidPorts=new FluidPorts(positions,connections);fluidPorts.update(server,this);minimumPlasma=MhdtConfig.minimumPlasma;plasmaEnergySync=plasmaEnergy;
  // Source consumes coolant even at zero heat, and preserves remaining HE when cooling stops.
  if(isCool()){power=generation(plasmaEnergy,hasMinimumPlasma());tanks[0].setFill(tanks[0].getFill()-COOLANT_USE);tanks[1].setFill(tanks[1].getFill()+COOLANT_USE);}var dir=facing.getOpposite();var space=PlasmaNetworks.nodespace(server);if(plasmaNode==null||plasmaNode.isExpired()){var origin=worldPosition.relative(dir,6).above(2);plasmaNode=space.getNode(origin);if(plasmaNode==null){plasmaNode=new PlasmaNode(origin).setConnections(new NodeConnection(origin.relative(dir),dir));space.createNode(plasmaNode);}}if(plasmaNode.hasValidNet())plasmaNode.addReceiver(this);sync(150);plasmaEnergy=0;
 }
 @Override public boolean receivesFusionPower(){return true;}@Override public void receiveFusionPower(long fusionPower,double neutronPower,float r,float g,float b){plasmaEnergy=fusionPower;}
 private static long bounded(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}@Override public long getPower(){return bounded(power);}@Override public long getMaxPower(){return getPower();}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return power;}@Override public BigInteger getProviderSpeedExact(){return power;}@Override public void setPower(long value){power=BigInteger.valueOf(value);}@Override public void setPowerExact(BigInteger value){power=value;}@Override public void usePower(long value){usePowerExact(BigInteger.valueOf(value));}@Override public void usePowerExact(BigInteger value){if(value.signum()<0||value.compareTo(power)>0)throw new IllegalArgumentException("Unavailable HE requested");power=power.subtract(value);}
 @Override public PowerNode energyNode(){return powerNode;}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean isLoaded(){return level!=null&&!isRemoved()&&level.hasChunkAt(worldPosition);}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}@Override public BigInteger energyStoredExact(){return power;}@Override public BigInteger energyCapacityExact(){return power;}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
 @Override protected void saveMachine(CompoundTag tag){for(int i=0;i<2;i++)tanks[i].writeToNBT(tag,"t"+i);}@Override protected void loadMachine(CompoundTag tag){for(int i=0;i<2;i++)tanks[i].readFromNBT(tag,"t"+i);}
 @Override protected void writeSync(CompoundTag tag){super.writeSync(tag);tag.putLong("plasmaEnergy",plasmaEnergySync);tag.putLong("minimumPlasma",minimumPlasma);}@Override protected void readSync(CompoundTag tag){super.readSync(tag);plasmaEnergy=tag.getLong("plasmaEnergy");plasmaEnergySync=plasmaEnergy;minimumPlasma=tag.getLong("minimumPlasma");}
 private void disconnect(){if(level instanceof ServerLevel server){if(powerNode!=null)HbmEnergy.nodespace(server).destroyNode(powerNode);if(fluidPorts!=null)fluidPorts.remove(server);if(plasmaNode!=null)PlasmaNetworks.nodespace(server).destroyNode(plasmaNode);}powerNode=null;fluidPorts=null;plasmaNode=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 private Object computerPower(){return power.bitLength()<64?power.longValue():power.toString();}@Override public String computerType(){return "ntm_fusion_mhdt";}@Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getPlasmaEnergy","getCoolant","getInfo"};}@Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getEnergyInfo"->new Object[]{computerPower()};case "getPlasmaEnergy"->new Object[]{plasmaEnergySync};case "getCoolant"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};case "getInfo"->new Object[]{computerPower(),plasmaEnergySync,tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};default->throw new ComputerException("No such method: "+method);};}
 @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"output"};}@Override public String provideRORValue(String name){return (PREFIX_VALUE+"output").equals(name)?power.toString():null;}
}
