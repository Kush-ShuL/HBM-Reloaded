package com.hbm.machine.reactor.iter.klystron;

import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.fusion.klystron.*;
import com.hbm.item.battery.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Source survival Klystron: actual HE+compressedair→KyU only, with source ramp/fan and target-sized HEbuffer. */
public final class KlystronEntity extends ThermalMachine implements EnergyReceiver,EnergyConductor,StandardFluidReceiver,ComputerControl,IRORValueProvider,IRORInteractive, com.hbm.multiblock.MuffledMachine {
    public static final long MAX_OUTPUT=1000000;public static final int AIR_CONSUMPTION=2500;
    public static Consumer<KlystronEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
    public final FluidTank compair=new FluidTank(HbmFluids.fromName("AIR"),AIR_CONSUMPTION*60);
    public long outputTarget,output,maxPower;private BigInteger power=BigInteger.ZERO;
    public float fan,prevFan,fanSpeed;public boolean muffled,tilted;
    private FluidPorts fluidPorts;private PowerNode powerNode;private KlystronNode klystronNode;
    public KlystronEntity(BlockPos pos,BlockState state){super(KlystronContent.ENTITY.get(),pos,state,1);}
    @Override protected String title(){return "container.fusionKlystron";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new KlystronMenu(id,inventory,this,worldPosition);}
    public static NodeConnection[] portConnections(BlockPos core,Direction dir){var rot=dir.getClockWise();return new NodeConnection[]{new NodeConnection(core.relative(dir,5).above(2),dir),new NodeConnection(core.relative(rot,4),rot),new NodeConnection(core.relative(rot,-4),rot.getOpposite())};}
    public static double speedScaled(double max,double fill){return max==0?0:fill>=max*.5?1:fill/max*2D;}
    public static void tick(Level level,BlockPos pos,BlockState state,KlystronEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else{double multiplier=speedScaled(machine.outputTarget,machine.output);if(machine.output>0)machine.fanSpeed+=.125F*multiplier;else machine.fanSpeed-=.125F;machine.fanSpeed=net.minecraft.util.Mth.clamp(machine.fanSpeed,0,5F*(float)multiplier);machine.prevFan=machine.fan;machine.fan+=machine.fanSpeed;if(machine.fan>=360){machine.fan-=360;machine.prevFan-=360;}CLIENT_TICK.accept(machine);}}
    private void serverTick(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());var connections=portConnections(worldPosition,facing);if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(Arrays.stream(connections).map(NodeConnection::origin).toArray(BlockPos[]::new)).setConnections(connections);HbmEnergy.nodespace(server).createNode(powerNode);}if(powerNode.hasValidNet())powerNode.addReceiver(this);if(fluidPorts==null)fluidPorts=new FluidPorts(Arrays.stream(connections).map(NodeConnection::origin).toArray(BlockPos[]::new),connections);fluidPorts.update(server,this);
        maxPower=Math.max(1000000,outputTarget*100L);var stack=slots.getFirst();if(stack.getItem() instanceof CreativeBatteryItem)power=BigInteger.valueOf(maxPower);else if(stack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(maxPower-getPower(),battery.getDischargeRate(stack)),battery.getCharge(stack));battery.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));}output=0;
        double factor=Math.min(speedScaled(maxPower,getPower()),speedScaled(compair.getMaxFill(),compair.getFill()));long powerReq=(long)Math.ceil(outputTarget*factor);int airReq=(int)Math.ceil(AIR_CONSUMPTION*factor);if(outputTarget>0&&getPower()>=powerReq&&compair.getFill()>=airReq){output=powerReq;power=power.subtract(BigInteger.valueOf(powerReq));compair.setFill(compair.getFill()-airReq);}if(output<outputTarget/50)output=0;
        var direction=facing.getOpposite();var nodePos=worldPosition.relative(direction,4).above(2);var space=KlystronNetworks.nodespace(server);if(klystronNode==null||klystronNode.isExpired()){klystronNode=space.getNode(nodePos);if(klystronNode==null){klystronNode=new KlystronNode(nodePos).setConnections(new NodeConnection(nodePos.relative(direction),direction));space.createNode(klystronNode);}}if(klystronNode.hasValidNet()){klystronNode.addProvider(this);for(var receiver:klystronNode.network().receivers)if(receiver.acceptsKlystron()){receiver.receiveKlystron(output);break;}}sync(100);
    }
    public void target(long requested){outputTarget=Math.max(0,Math.min(MAX_OUTPUT,requested));setChanged();}
    @Override public long getPower(){return power.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}@Override public long getMaxPower(){return maxPower;}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(maxPower);}@Override public void setPower(long value){power=BigInteger.valueOf(value);}@Override public void setPowerExact(BigInteger value){power=value;}
    /** Target reductions may leave storedpower above the new sourcecapacity: reject additional HE, don't erase it or throw. */
    @Override public BigInteger transferPowerExact(BigInteger amount){if(amount.signum()<0)throw new IllegalArgumentException("Negative HE transfer");var accepted=BigInteger.valueOf(maxPower).subtract(power).max(BigInteger.ZERO).min(amount);power=power.add(accepted);return amount.subtract(accepted);}@Override public long transferPower(long amount){return transferPowerExact(BigInteger.valueOf(amount)).longValueExact();}
    @Override public PowerNode energyNode(){return powerNode;}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return maxPower;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{compair};}@Override public FluidTank[] getReceivingTanks(){return getAllTanks();}@Override public List<TankView> tanks(){return List.of(new TankView(compair.getTankType(),compair.getFill(),compair.getMaxFill()));}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItem(int slot,net.minecraft.world.item.ItemStack stack){return slot==0;}
    @Override protected void saveMachine(CompoundTag tag){tag.putString("power",power.toString());tag.putLong("maxPower",maxPower);tag.putLong("outputTarget",outputTarget);compair.writeToNBT(tag,"t");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));maxPower=tag.getLong("maxPower");outputTarget=tag.getLong("outputTarget");compair.readFromNBT(tag,"t");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveMachine(tag);tag.putLong("output",output);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadMachine(tag);output=tag.getLong("output");}
    private void disconnect(){if(level instanceof ServerLevel server){if(powerNode!=null&&!powerNode.isExpired())HbmEnergy.nodespace(server).destroyNode(powerNode);if(fluidPorts!=null)fluidPorts.remove(server);KlystronNetworks.nodespace(server).destroyNode(klystronNode);}powerNode=null;fluidPorts=null;klystronNode=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public String computerType(){return "ntm_fusion_klystron";}@Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getAir","getOutput","setOutput","getInfo"};}@Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getEnergyInfo"->new Object[]{getPower(),maxPower};case "getAir"->new Object[]{compair.getFill(),compair.getMaxFill()};case "getOutput"->new Object[]{output,outputTarget};case "setOutput"->{target((long)Math.max(0,Math.min(MAX_OUTPUT,args.checkDouble(0))));yield new Object[]{};}case "getInfo"->new Object[]{getPower(),maxPower,compair.getFill(),compair.getMaxFill(),output,outputTarget};default->throw new ComputerException("No such method: "+method);};}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"output",PREFIX_VALUE+"target",PREFIX_FUNCTION+"settarget"+NAME_SEPARATOR+"target"};}@Override public String provideRORValue(String name){return switch(name){case "VAL:output"->""+output;case "VAL:target"->""+outputTarget;default->null;};}@Override public String runRORFunction(String name,String[] parameters){if(name.equals(PREFIX_FUNCTION+"settarget")&&parameters.length==1){outputTarget=IRORInteractive.parseInt(parameters[0],0,(int)MAX_OUTPUT);setChanged();}return null;}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
