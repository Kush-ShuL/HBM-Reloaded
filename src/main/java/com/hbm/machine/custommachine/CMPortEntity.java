package com.hbm.machine.custommachine;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import java.math.BigInteger;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Exact source Combo.inventory().power().fluid() endpoint and cached-position persistence. No HE/fluid buffer. */
public final class CMPortEntity extends BlockEntity implements WorldlyContainer,EnergyReceiver,StandardFluidReceiver,com.hbm.multiblock.MuffledMachine,com.hbm.energy.EnergyConductor,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    private boolean muffled,tilted;
    private boolean inventory=true,power=true,fluid=true,conductor,heat,metal;
    private String componentName="";
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    private BlockPos cachedPosition;
    public CMPortEntity(BlockPos pos,BlockState state){super(CustomMachineContent.PORT.get(),pos,state);}
    public void bind(BlockPos core){cachedPosition=core.immutable();com.hbm.multiblock.proxy.SourceProxyBindings.bind(this,core);setChanged();}
    public CustomMachineEntity core(){if(level==null||cachedPosition==null)return null;var entity=level.hasChunkAt(cachedPosition)?level.getBlockEntity(cachedPosition):null;if(entity instanceof CustomMachineEntity machine)return machine;cachedPosition=null;setChanged();return null;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putBoolean("inv",inventory);tag.putBoolean("power",power);tag.putBoolean("fluid",fluid);tag.putBoolean("conductor",conductor);tag.putBoolean("heat",heat);tag.putBoolean("metal",metal);tag.putString("ocname",componentName);if(cachedPosition!=null){tag.putBoolean("hasPos",true);tag.putInt("pX",cachedPosition.getX());tag.putInt("pY",cachedPosition.getY());tag.putInt("pZ",cachedPosition.getZ());}com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");inventory=tag.getBoolean("inv");power=tag.getBoolean("power");fluid=tag.getBoolean("fluid");conductor=tag.getBoolean("conductor");heat=tag.getBoolean("heat");metal=tag.getBoolean("metal");componentName=tag.getString("ocname");cachedPosition=tag.getBoolean("hasPos")?new BlockPos(tag.getInt("pX"),tag.getInt("pY"),tag.getInt("pZ")):null;com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}
    private CustomMachineEntity powerCore(){return power?core():null;}
    private CustomMachineEntity fluidCore(){return fluid?core():null;}
    private CustomMachineEntity inventoryCore(){return inventory?core():null;}
    @Override public boolean isLoaded(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();}return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long getPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPower();}var core=powerCore();return core==null?0:core.getPower();}
    @Override public long getMaxPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPower();}var core=powerCore();return core==null?0:core.getMaxPower();}
    @Override public void setPower(long value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPower(value);return;}var core=powerCore();if(core!=null)core.setPower(value);}
    @Override public java.math.BigInteger getPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPowerExact();}var core=powerCore();return core==null?java.math.BigInteger.ZERO:core.getPowerExact();}
    @Override public java.math.BigInteger getMaxPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPowerExact();}var core=powerCore();return core==null?java.math.BigInteger.ZERO:core.getMaxPowerExact();}
    @Override public void setPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPowerExact(value);return;}var core=powerCore();if(core!=null)core.setPowerExact(value);}
    @Override public java.math.BigInteger transferPowerExact(java.math.BigInteger amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPowerExact(amount);}var core=powerCore();return core==null?amount:core.transferPowerExact(amount);}
    @Override public long transferPower(long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPower(amount);}return transferPowerExact(java.math.BigInteger.valueOf(amount)).longValueExact();}
    @Override public long getReceiverSpeed(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed();}var core=powerCore();return core==null?0:core.getReceiverSpeed();}
    @Override public java.math.BigInteger getReceiverSpeedExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeedExact();}var core=powerCore();return core==null?java.math.BigInteger.ZERO:core.getReceiverSpeedExact();}
    @Override public boolean canConnect(com.hbm.fluid.HbmFluidType type,net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(type,side);}if(!fluid)return false;var core=core();return core==null||core.canConnect(type,side);}
    @Override public com.hbm.fluid.FluidTank[] getAllTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getAllTanks();}var core=fluidCore();return core==null?com.hbm.fluid.FluidTank.EMPTY_ARRAY:core.getAllTanks();}
    @Override public com.hbm.fluid.FluidTank[] getReceivingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingTanks();}var core=fluidCore();return core==null?com.hbm.fluid.FluidTank.EMPTY_ARRAY:core.getReceivingTanks();}
    @Override public long transferFluid(com.hbm.fluid.HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferFluid(type,pressure,amount);}var core=fluidCore();return core==null?amount:core.transferFluid(type,pressure,amount);}
    @Override public int getContainerSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();}var core=inventoryCore();return core==null?0:core.getContainerSize();}
    @Override public boolean isEmpty(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();}var core=inventoryCore();return core==null||core.isEmpty();}
    @Override public net.minecraft.world.item.ItemStack getItem(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);}var core=inventoryCore();return core==null?net.minecraft.world.item.ItemStack.EMPTY:core.getItem(slot);}
    @Override public net.minecraft.world.item.ItemStack removeItem(int slot,int count){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,count);}var core=core();return core==null?net.minecraft.world.item.ItemStack.EMPTY:core.removeItem(slot,count);}
    @Override public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);}var core=inventoryCore();return core==null?net.minecraft.world.item.ItemStack.EMPTY:core.removeItemNoUpdate(slot);}
    @Override public void setItem(int slot,net.minecraft.world.item.ItemStack value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,value);return;}var core=inventoryCore();if(core!=null)core.setItem(slot,value);}
    @Override public void clearContent(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;}var core=inventoryCore();if(core!=null)core.clearContent();}
    @Override public boolean stillValid(net.minecraft.world.entity.player.Player player){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);}var core=inventoryCore();return core!=null&&core.stillValid(player);}
    @Override public boolean canPlaceItem(int slot,net.minecraft.world.item.ItemStack stack){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);}var core=inventoryCore();return core!=null&&core.canPlaceItem(slot,stack);}
    @Override public int[] getSlotsForFace(net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(side);}var core=inventoryCore();return core==null?new int[0]:core.getSlotsForFace(side);}
    @Override public boolean canPlaceItemThroughFace(int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,side);}var core=inventoryCore();return core!=null&&core.canPlaceItemThroughFace(slot,stack,side);}
    @Override public boolean canTakeItemThroughFace(int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,side);}var core=inventoryCore();return core!=null&&core.canTakeItemThroughFace(slot,stack,side);}

 @Override public int getMaxStackSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxStackSize();}var actual=inventoryCore();return actual==null?0:actual.getMaxStackSize();}

 @Override public boolean canConnect(net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canConnect(side);}

 @Override public com.hbm.energy.PowerNode energyNode(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).energyNode();}

 @Override public long getDemand(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getDemand(type,pressure);}return StandardFluidReceiver.super.getDemand(type,pressure);}

 @Override public int[] getReceivingPressureRange(com.hbm.fluid.HbmFluidType type){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingPressureRange(type);}return StandardFluidReceiver.super.getReceivingPressureRange(type);}

 @Override public long getReceiverSpeed(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed(type,pressure);}return StandardFluidReceiver.super.getReceiverSpeed(type,pressure);}

 @Override public int getHeatStored(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getHeatStored();}

 @Override public void useUpHeat(int amount){com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).useUpHeat(amount);}

 @Override public boolean canAcceptPartialFlow(net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canAcceptPartialFlow(side,stack);}

 @Override public com.hbm.material.MaterialStack flow(net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).flow(side,stack);}

 @Override public boolean canAcceptPartialPour(net.minecraft.world.phys.Vec3 hit,net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canAcceptPartialPour(hit,side,stack);}

 @Override public com.hbm.material.MaterialStack pour(net.minecraft.world.phys.Vec3 hit,net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).pour(hit,side,stack);}

 @Override public String computerType(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).computerType();}

 @Override public String[] computerMethods(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).computerMethods();}

 @Override public Object[] callComputer(String method,com.hbm.api.computer.ComputerArguments args){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).callComputer(method,args);}

 @Override public boolean canComputerConnect(net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canComputerConnect(side);}

 @Override public String[] getFunctionInfo(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getFunctionInfo();}

 @Override public String provideRORValue(String name){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).provideRORValue(name);}

 @Override public String runRORFunction(String name,String[] args){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).runRORFunction(name,args);}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
