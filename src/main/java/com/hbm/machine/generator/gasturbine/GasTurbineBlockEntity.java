package com.hbm.machine.generator.gasturbine;

import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.pollution.*;
import com.hbm.registry.HbmSounds;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Full source CCGT state machine. Mechanical source algorithm bodies live in GasTurbineOperation. */
public final class GasTurbineBlockEntity extends GasTurbineOperation implements EnergyProvider,EnergyConductor,StandardFluidTransceiver,IFluidCopiable,ComputerControl,IRORValueProvider,IRORInteractive {
    public static Consumer<GasTurbineBlockEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
    public final FluidTank smoke=new FluidTank(HbmFluids.SMOKE,150),leaded=new FluidTank(HbmFluids.SMOKE_LEADED,150),poison=new FluidTank(HbmFluids.SMOKE_POISON,150);
    private BigInteger powerBeforeNet=BigInteger.ZERO;private PowerNode node;private FluidPorts fluidPorts;
    public GasTurbineBlockEntity(BlockPos pos,BlockState state){super(pos,state);}
    @Override protected String title(){return "container.turbinegas";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new GasTurbineMenu(id,inventory,this,worldPosition);}
    public static BlockPos powerPort(BlockPos core,Direction facing){return core.relative(facing.getClockWise(),4).above();}
    public static NodeConnection[] fluidConnections(BlockPos core,Direction facing){var r=facing.getClockWise();return new NodeConnection[]{new NodeConnection(core.relative(facing,-2).relative(r),facing.getOpposite()),new NodeConnection(core.relative(facing,2).relative(r),facing),new NodeConnection(core.relative(facing,-2).relative(r,-4),facing.getOpposite()),new NodeConnection(core.relative(facing,2).relative(r,-4),facing),new NodeConnection(core.relative(r,-6).above(),r.getOpposite())};}
    public static void tick(Level level,BlockPos pos,BlockState state,GasTurbineBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
    private void serverTick(ServerLevel server){
        var facing=DummyableBlock.facing(getBlockState());var port=powerPort(worldPosition,facing);var outward=facing.getClockWise();if(node==null||node.isExpired()){node=new PowerNode(port).setConnections(new NodeConnection(port.relative(outward),outward));HbmEnergy.nodespace(server).createNode(node);}if(node.hasValidNet())node.addProvider(this);
        if(fluidPorts==null){var connections=fluidConnections(worldPosition,facing);fluidPorts=new FluidPorts(Arrays.stream(connections).map(NodeConnection::origin).toArray(BlockPos[]::new),connections);}fluidPorts.update(server,this);
        waterToBoil=0;throttle=powerSliderPos*100/60;
        if(slots.get(1).getItem() instanceof FluidIdentifier identifier){var fluid=identifier.getType(level,worldPosition,slots.get(1));var trait=fluid.getTrait(FluidTraits.Combustible.class);if(trait!=null&&trait.getGrade()==FluidTraits.FuelGrade.GAS)tanks[0].setTankType(fluid);}
        if(autoMode){int target=tanks[0].getFill()*10>tanks[0].getMaxFill()?60-(int)(60*getPower()/maxPower):(int)(tanks[0].getFill()*.0001*(60-(int)(60*getPower()/maxPower)));if(target>powerSliderPos)powerSliderPos++;else if(target<powerSliderPos)powerSliderPos--;}
        switch(state){case 0->shutdown();case -1->{stopIfNotReady();startup();}case 1->{stopIfNotReady();run();}default->{}}
        powerBeforeNet=power.min(BigInteger.valueOf(maxPower));chargeItem();power=power.min(BigInteger.valueOf(maxPower));sync(150);
    }
    private void chargeItem(){var stack=slots.get(0);if(stack.getItem() instanceof CreativeBatteryItem){power=BigInteger.ZERO;return;}if(stack.getItem() instanceof HbmBattery battery){long gap=Math.min(battery.getChargeRate(stack),battery.getMaxCharge(stack)-battery.getCharge(stack));long amount=power.min(BigInteger.valueOf(gap)).longValueExact();battery.chargeBattery(stack,amount);power=power.subtract(BigInteger.valueOf(amount));}}
    @Override protected void pollute(HbmFluidType type,float multiplier){if(!(level instanceof ServerLevel server))return;var trait=type.getTrait(FluidTraits.Polluting.class);if(trait==null)return;trait.burnMap().forEach((kind,rate)->{var buffer=kind==PollutionType.SOOT?smoke:kind==PollutionType.HEAVYMETAL?leaded:poison;buffer.setFill(buffer.getFill()+(int)Math.ceil(rate*multiplier*100));if(buffer.getFill()>150){int overflow=buffer.getFill()-150;buffer.setFill(150);PollutionSystem.increment(server,worldPosition,kind,overflow/100F);if(server.random.nextInt(3)==0)server.playSound(null,worldPosition,SoundEvents.LAVA_EXTINGUISH,SoundSource.BLOCKS,.1F,1.5F);}});}
    @Override protected void sourceSound(double x,double y,double z,String sound,float volume,float pitch){if(level instanceof ServerLevel server)server.playSound(null,x,y,z,HbmSounds.get(sound),SoundSource.BLOCKS,volume,pitch);}
    public void control(int action,int value){if(action==0){if(value>=-1&&value<=1)state=value;}else if(action==1)autoMode=value!=0;else if(action==2&&value>=0&&value<=60)powerSliderPos=value;setChanged();}
    private static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getPower(){return legacy(power);}@Override public long getMaxPower(){return maxPower;}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(maxPower);}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public void usePower(long amount){usePowerExact(BigInteger.valueOf(amount));}@Override public void usePowerExact(BigInteger amount){if(amount.signum()<0||amount.compareTo(power)>0)throw new IllegalArgumentException("Unavailable HE");power=power.subtract(amount);setChanged();}
    @Override public PowerNode energyNode(){return node;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null&&side!=Direction.DOWN;}@Override public boolean canConnect(HbmFluidType type,Direction side){return canConnect(side);}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return maxPower;}
    /** Same source EnergyControl data contract, available to real overlay/instrument consumers. */
    public void provideExtraInfo(CompoundTag data){data.putBoolean("active",state==1);data.putDouble("heat",Math.max(20D,temp));data.putDouble("turbine",powerSliderPos*100D/60D);data.putInt("speed",rpm);data.putDouble("output",instantPowerOutput);data.putDouble("consumption",waterToBoil);data.putDouble("outputmb",waterToBoil*10);}
    @Override public List<TankView> tanks(){var result=new ArrayList<TankView>();for(var tank:tanks)result.add(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));for(var tank:getSmokeTanks())result.add(new TankView(tank.getTankType(),tank.getFill(),150));return result;}
    public FluidTank[] getSmokeTanks(){return new FluidTank[]{smoke,leaded,poison};}@Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0],tanks[1],tanks[2]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[3],smoke,leaded,poison};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItem(int slot,net.minecraft.world.item.ItemStack stack){return false;}
    private void saveFluids(CompoundTag tag){String[] keys={"gas","lube","water","densesteam"};for(int i=0;i<4;i++)tanks[i].writeToNBT(tag,keys[i]);smoke.writeToNBT(tag,"smoke0");leaded.writeToNBT(tag,"smoke1");poison.writeToNBT(tag,"smoke2");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    private void loadFluids(CompoundTag tag){String[] keys={"gas","lube","water","densesteam"};for(int i=0;i<4;i++)tanks[i].readFromNBT(tag,keys[i]);smoke.readFromNBT(tag,"smoke0");leaded.readFromNBT(tag,"smoke1");poison.readFromNBT(tag,"smoke2");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    private static BigInteger readPower(CompoundTag tag){return tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));}
    @Override protected void saveMachine(CompoundTag tag){saveFluids(tag);tag.putString("power",power.toString());tag.putBoolean("automode",autoMode);tag.putInt("state",state==1?1:0);tag.putInt("rpm",state==1?rpm:0);tag.putInt("temperature",state==1?temp:20);tag.putInt("slidPos",state==1?powerSliderPos:0);tag.putInt(state==1?"instPwr":"instpwr",state==1?instantPowerOutput:0);tag.putInt("counter",state==1?225:0);}
    @Override protected void loadMachine(CompoundTag tag){loadFluids(tag);power=readPower(tag);autoMode=tag.getBoolean("automode");state=tag.getInt("state");rpm=tag.getInt("rpm");temp=tag.getInt("temperature");powerSliderPos=tag.getInt("slidPos");instantPowerOutput=tag.getInt("instPwr");counter=tag.getInt("counter");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveFluids(tag);tag.putString("power",powerBeforeNet.toString());tag.putInt("rpm",rpm);tag.putInt("temperature",temp);tag.putInt("state",state);tag.putBoolean("automode",autoMode);tag.putInt("throttle",throttle);tag.putInt("slidPos",powerSliderPos);if(state==1)tag.putInt("instPwr",instantPowerOutput);else tag.putInt("counter",counter);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadFluids(tag);power=readPower(tag);rpm=tag.getInt("rpm");temp=tag.getInt("temperature");state=tag.getInt("state");autoMode=tag.getBoolean("automode");throttle=tag.getInt("throttle");powerSliderPos=tag.getInt("slidPos");if(state==1)instantPowerOutput=tag.getInt("instPwr");else counter=tag.getInt("counter");}
    private void disconnect(){if(level instanceof ServerLevel server){if(node!=null&&!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}node=null;fluidPorts=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public String computerType(){return "ntm_gas_turbine";}@Override public String[] computerMethods(){return new String[]{"getFluid","getType","getPower","getThrottle","getState","getAuto","setThrottle","setAuto","start","stop","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getFluid"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),16000,tanks[2].getFill(),16000,tanks[3].getFill(),160000};case "getType"->new Object[]{tanks[0].getTankType().getName()};case "getPower"->new Object[]{getPower()};case "getThrottle"->new Object[]{throttle};case "getState"->new Object[]{state};case "getAuto"->new Object[]{autoMode};case "setThrottle"->{double input=args.checkInteger(0)*60D/100D;if(input<0||input>100)yield new Object[]{null,"Input out of range."};powerSliderPos=(int)input;setChanged();yield new Object[]{true};}case "setAuto"->{autoMode=args.checkBoolean(0);setChanged();yield new Object[]{};}case "start"->{if(state==0)state=-1;setChanged();yield new Object[]{};}case "stop"->{if(state==1)state=0;setChanged();yield new Object[]{};}case "getInfo"->new Object[]{throttle,state,tanks[0].getFill(),100000,tanks[1].getFill(),16000,tanks[2].getFill(),16000,tanks[3].getFill(),160000};default->throw new ComputerException("No such method: "+method);};}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"turbinepercent",PREFIX_VALUE+"turbinespeed",PREFIX_VALUE+"output",PREFIX_VALUE+"state",PREFIX_VALUE+"automode",PREFIX_VALUE+"temp",PREFIX_VALUE+"power",PREFIX_VALUE+"fuel",PREFIX_VALUE+"lubricant",PREFIX_VALUE+"water",PREFIX_VALUE+"steam",PREFIX_FUNCTION+"setauto"+NAME_SEPARATOR+"auto",PREFIX_FUNCTION+"setthrottle"+NAME_SEPARATOR+"percent",PREFIX_FUNCTION+"setstate"+NAME_SEPARATOR+"state"};}
    @Override public String provideRORValue(String name){return switch(name){case "VAL:turbinepercent"->""+(int)(powerSliderPos*100D/60D);case "VAL:turbinespeed"->""+rpm;case "VAL:output"->""+(int)(instantPowerOutput*20);case "VAL:state"->""+state;case "VAL:automode"->autoMode?"1":"0";case "VAL:temp"->""+temp;case "VAL:power"->power.toString();case "VAL:fuel"->""+tanks[0].getFill();case "VAL:lubricant"->""+tanks[1].getFill();case "VAL:water"->""+tanks[2].getFill();case "VAL:steam"->""+tanks[3].getFill();default->null;};}
    @Override public String runRORFunction(String name,String[] params){if(params.length>0)try{int value=Integer.parseInt(params[0]);if(name.equals(PREFIX_FUNCTION+"setauto"))autoMode=value==1;else if(name.equals(PREFIX_FUNCTION+"setthrottle"))powerSliderPos=Math.max(0,Math.min(100,value))*60/100;else if(name.equals(PREFIX_FUNCTION+"setstate")){if(value==1&&state==0)state=-1;else if(value==0&&state==1)state=0;}else return null;setChanged();}catch(NumberFormatException ignored){}return null;}
}
