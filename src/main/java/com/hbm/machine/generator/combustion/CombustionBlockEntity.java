package com.hbm.machine.generator.combustion;

import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.pollution.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Source engine's tenth-mB accumulator, piston-grade matrix and polluting smoke buffers. */
public final class CombustionBlockEntity extends ThermalMachine implements EnergyProvider,EnergyConductor,StandardFluidTransceiver,IFluidCopiable,ComputerControl,IRORValueProvider,IRORInteractive, com.hbm.multiblock.MuffledMachine {
    public static final long MAX_POWER=2500000;
    public static Consumer<CombustionBlockEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
    public final FluidTank tank=new FluidTank(HbmFluids.fromName("DIESEL"),24000),smoke=new FluidTank(HbmFluids.SMOKE,50),leaded=new FluidTank(HbmFluids.SMOKE_LEADED,50),poison=new FluidTank(HbmFluids.SMOKE_POISON,50);
    private BigInteger power=BigInteger.ZERO;private PowerNode node;private FluidPorts fluidPorts;
    public boolean isOn,wasOn,muffled,tilted;public int setting,tenth,playersUsing;public float doorAngle,prevDoorAngle;
    public CombustionBlockEntity(BlockPos pos,BlockState state){super(CombustionContent.ENTITY.get(),pos,state,5);}
    @Override protected String title(){return "container.combustionEngine";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CombustionMenu(id,inventory,this,worldPosition);}
    public static BlockPos[] portPositions(BlockPos core,Direction direction){Direction rotation=direction.getClockWise();return new BlockPos[]{core.relative(rotation),core.relative(rotation,-1),core.relative(direction,-1).relative(rotation),core.relative(direction,-1).relative(rotation,-1)};}
    private NodeConnection[] connections(){Direction direction=DummyableBlock.facing(getBlockState());var positions=portPositions(worldPosition,direction);return new NodeConnection[]{new NodeConnection(positions[0].relative(direction),direction),new NodeConnection(positions[1].relative(direction),direction),new NodeConnection(positions[2].relative(direction,-1),direction.getOpposite()),new NodeConnection(positions[3].relative(direction,-1),direction.getOpposite())};}
    public static void tick(Level level,BlockPos pos,BlockState state,CombustionBlockEntity engine){if(level instanceof ServerLevel server)engine.serverTick(server);else{engine.prevDoorAngle=engine.doorAngle;float speed=engine.doorAngle/10F+3;engine.doorAngle=net.minecraft.util.Mth.clamp(engine.doorAngle+(engine.playersUsing>0?speed:-speed),0,135);CLIENT_TICK.accept(engine);}}
    private void serverTick(ServerLevel server){
        var positions=portPositions(worldPosition,DummyableBlock.facing(getBlockState()));if(node==null||node.isExpired()){node=new PowerNode(positions).setConnections(connections());HbmEnergy.nodespace(server).createNode(node);}if(node.hasValidNet())node.addProvider(this);
        if(fluidPorts==null)fluidPorts=new FluidPorts(positions,connections());fluidPorts.update(server,this);
        var inventory=slots.toArray(ItemStack[]::new);tank.loadTank(0,1,inventory);if(tank.setType(4,inventory))tenth=0;for(int i=0;i<5;i++)slots.set(i,inventory[i]==null?ItemStack.EMPTY:inventory[i]);
        wasOn=false;int fill=tank.getFill()*10+tenth;var trait=tank.getTankType().getTrait(FluidTraits.Combustible.class);
        if(isOn&&setting>0&&slots.get(2).is(CombustionContent.PISTONS.get())&&fill>0&&trait!=null){double efficiency=PistonSetItem.efficiency(slots.get(2),trait.getGrade());if(efficiency>0){int toBurn=Math.min(fill,setting*2);power=power.add(BigInteger.valueOf((long)(toBurn*(trait.getCombustionEnergy()/10000D)*efficiency)));fill-=toBurn;if(server.getGameTime()%5==0&&toBurn>0)pollute(server,toBurn*.5F);wasOn=toBurn>0;tank.setFill(fill/10);tenth=fill%10;}}
        chargeItem();power=power.min(BigInteger.valueOf(MAX_POWER));sync(50);
    }
    private void chargeItem(){var stack=slots.get(3);if(stack.getItem() instanceof CreativeBatteryItem){power=BigInteger.ZERO;return;}if(stack.getItem() instanceof HbmBattery battery){long available=Math.min(battery.getChargeRate(stack),battery.getMaxCharge(stack)-battery.getCharge(stack));long amount=power.min(BigInteger.valueOf(available)).longValueExact();battery.chargeBattery(stack,amount);power=power.subtract(BigInteger.valueOf(amount));}}
    private void pollute(ServerLevel level,float multiplier){var trait=tank.getTankType().getTrait(FluidTraits.Polluting.class);if(trait==null)return;trait.burnMap().forEach((type,rate)->{var buffer=type==PollutionType.SOOT?smoke:type==PollutionType.HEAVYMETAL?leaded:poison;buffer.setFill(buffer.getFill()+(int)Math.ceil(rate*multiplier*100));if(buffer.getFill()>buffer.getMaxFill()){int overflow=buffer.getFill()-buffer.getMaxFill();buffer.setFill(buffer.getMaxFill());PollutionSystem.increment(level,worldPosition,type,overflow/100F);if(level.random.nextInt(3)==0)level.playSound(null,worldPosition,SoundEvents.LAVA_EXTINGUISH,SoundSource.BLOCKS,.1F,1.5F);}});}
    public double efficiency(){var trait=tank.getTankType().getTrait(FluidTraits.Combustible.class);return slots.get(2).is(CombustionContent.PISTONS.get())&&trait!=null?PistonSetItem.efficiency(slots.get(2),trait.getGrade()):0;}
    public boolean control(Player player,int value){if(player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)>=625)return false;if(value==-1)isOn=!isOn;else if(value>=0&&value<=30)setting=value;else return false;setChanged();return true;}
    @Override public void startOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing++;}@Override public void stopOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing--;}
    private static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getPower(){return legacy(power);}@Override public long getMaxPower(){return MAX_POWER;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(MAX_POWER);}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public void usePower(long amount){usePowerExact(BigInteger.valueOf(amount));}@Override public void usePowerExact(BigInteger amount){if(amount.signum()<0||amount.compareTo(power)>0)throw new IllegalArgumentException("Unavailable HE");power=power.subtract(amount);setChanged();}
    @Override public PowerNode energyNode(){return node;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null&&side!=Direction.DOWN;}@Override public boolean canConnect(HbmFluidType type,Direction side){return canConnect(side);}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return MAX_POWER;}@Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()),new TankView(smoke.getTankType(),smoke.getFill(),50),new TankView(leaded.getTankType(),leaded.getFill(),50),new TankView(poison.getTankType(),poison.getFill(),50));}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{smoke,leaded,poison};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override protected void saveMachine(CompoundTag tag){tag.putInt("setting",setting);tag.putString("power",power.toString());tag.putBoolean("isOn",isOn);tag.putInt("tenth",tenth);tank.writeToNBT(tag,"tank");smoke.writeToNBT(tag,"smoke0");leaded.writeToNBT(tag,"smoke1");poison.writeToNBT(tag,"smoke2");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){setting=tag.getInt("setting");power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));isOn=tag.getBoolean("isOn");tenth=tag.getInt("tenth");tank.readFromNBT(tag,"tank");smoke.readFromNBT(tag,"smoke0");leaded.readFromNBT(tag,"smoke1");poison.readFromNBT(tag,"smoke2");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("wasOn",wasOn);tag.putInt("playersUsing",playersUsing);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);wasOn=tag.getBoolean("wasOn");playersUsing=tag.getInt("playersUsing");}
    private void disconnect(){if(level instanceof ServerLevel server){if(node!=null&&!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}node=null;fluidPorts=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putIntArray("fluidID",new int[]{tank.getTankType().getID()});tag.putBoolean("isOn",isOn);tag.putInt("burnRate",setting);return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){var ids=tag.getIntArray("fluidID");if(index>=0&&index<ids.length)tank.setTankType(HbmFluids.fromID(ids[index]));if(tag.contains("isOn"))isOn=tag.getBoolean("isOn");if(tag.contains("burnRate"))setting=tag.getInt("burnRate");setChanged();}
    @Override public String computerType(){return "ntm_combustion_engine";}@Override public String[] computerMethods(){return new String[]{"getFluid","getType","getPower","getThrottle","getState","getEfficiency","setThrottle","start","stop","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getFluid"->new Object[]{tank.getFill(),tank.getMaxFill()};case "getType"->new Object[]{tank.getTankType().getName()};case "getPower"->new Object[]{getPower()};case "getThrottle"->new Object[]{setting};case "getState"->new Object[]{isOn};case "getEfficiency"->new Object[]{efficiency()};case "getInfo"->new Object[]{setting,isOn,getPower(),efficiency(),tank.getFill(),tank.getMaxFill(),tank.getTankType().getName()};case "setThrottle"->{int requested=args.checkInteger(0);if(requested<0||requested>30)yield new Object[]{false,"Throttle request outside of range 0-30"};setting=requested;setChanged();yield new Object[]{true};}case "start","stop"->{isOn=method.equals("start");setChanged();yield new Object[]{};}default->throw new ComputerException("No such method: "+method);};}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"state",PREFIX_VALUE+"throttle",PREFIX_VALUE+"power",PREFIX_VALUE+"fuel",PREFIX_VALUE+"efficiency",PREFIX_FUNCTION+"setstate"+NAME_SEPARATOR+"state",PREFIX_FUNCTION+"setthrottle"+NAME_SEPARATOR+"throttle"};}
    @Override public String provideRORValue(String name){return switch(name){case "VAL:state"->isOn?"1":"0";case "VAL:throttle"->""+setting;case "VAL:power"->power.toString();case "VAL:fuel"->""+tank.getFill();case "VAL:efficiency"->""+(int)Math.round(efficiency()*100);default->null;};}
    @Override public String runRORFunction(String name,String[] params){if(params.length>0)try{int value=Integer.parseInt(params[0]);if(name.equals(PREFIX_FUNCTION+"setstate"))isOn=value==1;else if(name.equals(PREFIX_FUNCTION+"setthrottle"))setting=Math.max(0,Math.min(30,value));else return null;setChanged();}catch(NumberFormatException ignored){}return null;}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
