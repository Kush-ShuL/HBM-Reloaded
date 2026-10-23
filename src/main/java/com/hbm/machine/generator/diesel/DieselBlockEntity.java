package com.hbm.machine.generator.diesel;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.pollution.*;
import java.math.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original finite diesel generation: constant consumption, independent on/off and redstone inhibition. */
public final class DieselBlockEntity extends ThermalMachine implements EnergyProvider,EnergyConductor,StandardFluidTransceiver,IFluidCopiable, com.hbm.multiblock.MuffledMachine {
    public static Consumer<DieselBlockEntity> clientTick=engine->{},clientRemoved=engine->{};
    public final FluidTank tank=new FluidTank(HbmFluids.fromName("DIESEL"),DieselConfig.fuelCapacity),smoke=new FluidTank(HbmFluids.SMOKE,100),leaded=new FluidTank(HbmFluids.SMOKE_LEADED,100),poison=new FluidTank(HbmFluids.SMOKE_POISON,100);
    private BigInteger power=BigInteger.ZERO;public BigInteger powerCapacity=DieselConfig.maxPower;public boolean isOn,wasOn,muffled,tilted;
    private PowerNode node;private FluidPorts ports;
    public DieselBlockEntity(BlockPos pos,BlockState state){super(DieselContent.ENTITY.get(),pos,state,4);}
    @Override public boolean isValidBlockState(BlockState state){return state.is(DieselContent.BLOCK.get());}
    @Override protected String title(){return "container.machineDiesel";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new DieselMenu(id,inventory,this,worldPosition);}
    public static BigInteger fuelEnergy(HbmFluidType type){var trait=type.getTrait(FluidTraits.Combustible.class);if(trait==null||trait.getGrade()==FluidTraits.FuelGrade.LOW)return BigInteger.ZERO;long base=trait.getCombustionEnergy()/1000L;double efficiency=DieselConfig.efficiency.getOrDefault(trait.getGrade(),0D),source=base*efficiency;return Double.isFinite(source)&&source>=-0x1.0p63&&source<0x1.0p63?BigInteger.valueOf((long)source):BigDecimal.valueOf(base).multiply(BigDecimal.valueOf(efficiency)).toBigInteger();}
    public boolean hasAcceptableFuel(){return fuelEnergy(tank.getTankType()).signum()>0;}
    public static void tick(Level level,BlockPos pos,BlockState state,DieselBlockEntity engine){if(level instanceof ServerLevel server)engine.serverTick(server);else clientTick.accept(engine);}
    private void serverTick(ServerLevel server){if(node==null||node.isExpired()){node=new PowerNode(worldPosition);HbmEnergy.nodespace(server).createNode(node);}if(node.hasValidNet())node.addProvider(this);if(ports==null)ports=new FluidPorts(worldPosition);ports.update(server,this);wasOn=false;var items=slots.toArray(ItemStack[]::new);tank.setType(3,items);tank.loadTank(0,1,items);for(int i=0;i<items.length;i++)slots.set(i,items[i]==null?ItemStack.EMPTY:items[i]);chargeBattery();if(isOn)generate(server);sync(50);}
    private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(powerCapacity)>0){power=powerCapacity;return;}if(slots.get(2).getItem() instanceof CreativeBatteryItem){power=BigInteger.ZERO;return;}if(slots.get(2).getItem() instanceof HbmBattery battery){long available=Math.min(battery.getChargeRate(slots.get(2)),battery.getMaxCharge(slots.get(2))-battery.getCharge(slots.get(2)));long charged=power.min(BigInteger.valueOf(available)).longValueExact();power=power.subtract(BigInteger.valueOf(charged));battery.chargeBattery(slots.get(2),charged);}}
    private void generate(ServerLevel server){if(!isOn||server.hasNeighborSignal(worldPosition)||!hasAcceptableFuel()||tank.getFill()<=0)return;wasOn=true;tank.setFill(Math.max(0,tank.getFill()-1));if(server.getGameTime()%5==0){var trait=tank.getTankType().getTrait(FluidTraits.Polluting.class);if(trait!=null)trait.burnMap().forEach((type,rate)->pollute(server,type,rate*5F));}power=power.add(fuelEnergy(tank.getTankType())).min(powerCapacity);}
    private void pollute(ServerLevel level,PollutionType type,float amount){var output=type==PollutionType.SOOT?smoke:type==PollutionType.HEAVYMETAL?leaded:poison;output.setFill(output.getFill()+(int)Math.ceil(amount*100));if(output.getFill()>output.getMaxFill()){int overflow=output.getFill()-output.getMaxFill();output.setFill(output.getMaxFill());PollutionSystem.increment(level,worldPosition,type,overflow/100F);if(level.random.nextInt(3)==0)level.playSound(null,worldPosition,SoundEvents.LAVA_EXTINGUISH,SoundSource.BLOCKS,.1F,1.5F);}}
    public void toggle(){isOn=!isOn;setChanged();}
    private static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getPower(){return legacy(power);}@Override public long getMaxPower(){return legacy(DieselConfig.maxPower);}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return DieselConfig.maxPower;}@Override public BigInteger getProviderSpeedExact(){return DieselConfig.maxPower;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public void usePower(long value){usePowerExact(BigInteger.valueOf(value));}@Override public void usePowerExact(BigInteger value){if(value.signum()<0||value.compareTo(power)>0)throw new IllegalArgumentException("Unavailable HE requested");power=power.subtract(value);setChanged();}@Override public PowerNode energyNode(){return node;}@Override public boolean isLoaded(){return !isRemoved()&&hasLevel()&&level.hasChunkAt(worldPosition);}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return legacy(powerCapacity);}@Override public BigInteger energyStoredExact(){return power;}@Override public BigInteger energyCapacityExact(){return powerCapacity;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{smoke,leaded,poison};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()),new TankView(smoke.getTankType(),smoke.getFill(),smoke.getMaxFill()),new TankView(leaded.getTankType(),leaded.getFill(),leaded.getMaxFill()),new TankView(poison.getTankType(),poison.getFill(),poison.getMaxFill()));}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0?FluidContainerRegistry.getFluidContent(stack,tank.getTankType())>0:slot==2&&stack.getItem() instanceof HbmBattery;}
    @Override public int[] getSlotsForFace(Direction side){return side==Direction.DOWN?new int[]{1,2}:side==Direction.UP?new int[]{0}:new int[]{2};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){String id=BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();if(slot==1)return id.equals("hbm:canister_empty")||id.equals("hbm:tank_steel");return slot==2&&stack.getItem() instanceof HbmBattery battery&&battery.getCharge(stack)==battery.getMaxCharge(stack);}
    private static BigInteger readEnergy(CompoundTag tag,String key){return tag.contains(key,Tag.TAG_STRING)?new BigInteger(tag.getString(key)):BigInteger.valueOf(tag.getLong(key));}
    @Override protected void saveMachine(CompoundTag tag){tag.putBoolean("isOn",isOn);tag.putString("powerTime",power.toString());tag.putString("powerCap",powerCapacity.toString());tank.writeToNBT(tag,"fuel");smoke.writeToNBT(tag,"smoke0");leaded.writeToNBT(tag,"smoke1");poison.writeToNBT(tag,"smoke2");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){isOn=tag.getBoolean("isOn");wasOn=tag.getBoolean("wasOn");power=readEnergy(tag,"powerTime");powerCapacity=readEnergy(tag,"powerCap");tank.readFromNBT(tag,"fuel");smoke.readFromNBT(tag,"smoke0");leaded.readFromNBT(tag,"smoke1");poison.readFromNBT(tag,"smoke2");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("wasOn",wasOn);return tag;}
    private void disconnect(){if(level instanceof ServerLevel server){if(node!=null&&!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);if(ports!=null)ports.remove(server);}node=null;ports=null;if(level!=null&&level.isClientSide)clientRemoved.accept(this);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
