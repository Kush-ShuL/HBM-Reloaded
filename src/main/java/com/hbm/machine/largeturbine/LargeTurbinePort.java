package com.hbm.machine.largeturbine;
import com.hbm.api.computer.*;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.*;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source ProxyCombo(false,true,true): deliberately no inventory interface. */
public final class LargeTurbinePort extends BlockEntity implements EnergyProvider,SidedEnergyConnector,StandardFluidTransceiver,MachineView,IFluidCopiable,ComputerControl,MuffledMachine,net.minecraft.world.WorldlyContainer,com.hbm.energy.EnergyReceiver,com.hbm.energy.EnergyConductor,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
 public LargeTurbinePort(BlockPos pos,BlockState state){super(LargeTurbineContent.PORT.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isPort(state);}
 public LargeTurbineEntity core(){if(level==null||!(getBlockState().getBlock() instanceof LargeTurbineBlock block))return null;var p=block.findCore(level,worldPosition);return p!=null&&level.getBlockEntity(p) instanceof LargeTurbineEntity t?t:null;}
 @Override public boolean isLoaded(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();}return !isRemoved()&&core()!=null;}@Override public boolean canConnect(Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(side);}return side!=null&&core()!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(type,side);}return side!=null&&core()!=null;}
 @Override public long getPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPower();}var t=core();return t==null?0:t.getPower();}@Override public long getMaxPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPower();}return LargeTurbineEntity.maxPower;}@Override public void setPower(long value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPower(value);return;}var t=core();if(t!=null)t.setPower(value);}@Override public void usePower(long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.usePower(amount);return;}var t=core();if(t!=null)t.usePower(amount);}
 @Override public FluidTank[] getAllTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getAllTanks();}var t=core();return t==null?FluidTank.EMPTY_ARRAY:t.tanks;}@Override public FluidTank[] getReceivingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingTanks();}var t=core();return t==null?FluidTank.EMPTY_ARRAY:t.getReceivingTanks();}@Override public FluidTank[] getSendingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSendingTanks();}var t=core();return t==null?FluidTank.EMPTY_ARRAY:t.getSendingTanks();}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferFluid(type,pressure,amount);}var t=core();return t==null?amount:t.transferFluid(type,pressure,amount);}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.useUpFluid(type,pressure,amount);return;}var t=core();if(t!=null)t.useUpFluid(type,pressure,amount);}@Override public FluidTank getTankToPaste(){return null;}
 @Override public long energyStored(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();}return getPower();}@Override public long energyCapacity(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();}return getMaxPower();}@Override public List<TankView> tanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();}var t=core();return t==null?List.of():t.tanks();}
 @Override public String computerType(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.computerType();}return "ntm_turbine";}@Override public String[] computerMethods(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.computerMethods();}var t=core();return t==null?new String[0]:t.computerMethods();}@Override public Object[] callComputer(String method,ComputerArguments args){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.callComputer(method,args);}var t=core();if(t==null)throw new ComputerException("Machine is no longer available");return t.callComputer(method,args);}
 @Override public boolean isMuffled(){var t=core();return t!=null&&t.isMuffled();}@Override public void setMuffled(boolean value){var t=core();if(t!=null)t.setMuffled(value);}

 @Override public int getContainerSize(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getContainerSize();}

 @Override public boolean isEmpty(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).isEmpty();}

 @Override public int getMaxStackSize(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getMaxStackSize();}

 @Override public net.minecraft.world.item.ItemStack getItem(int slot){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getItem(slot);}

 @Override public net.minecraft.world.item.ItemStack removeItem(int slot,int count){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).removeItem(slot,count);}

 @Override public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).removeItemNoUpdate(slot);}

 @Override public void setItem(int slot,net.minecraft.world.item.ItemStack stack){com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).setItem(slot,stack);}

 @Override public void clearContent(){com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).clearContent();}

 @Override public boolean stillValid(net.minecraft.world.entity.player.Player player){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).stillValid(player);}

 @Override public boolean canPlaceItem(int slot,net.minecraft.world.item.ItemStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canPlaceItem(slot,stack);}

 @Override public int[] getSlotsForFace(net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getSlotsForFace(side);}

 @Override public boolean canPlaceItemThroughFace(int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canPlaceItemThroughFace(slot,stack,side);}

 @Override public boolean canTakeItemThroughFace(int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canTakeItemThroughFace(slot,stack,side);}

 @Override public java.math.BigInteger getPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPowerExact();}return EnergyProvider.super.getPowerExact();}

 @Override public java.math.BigInteger getMaxPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPowerExact();}return EnergyProvider.super.getMaxPowerExact();}

 @Override public void setPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPowerExact(value);return;}EnergyProvider.super.setPowerExact(value);}

 @Override public long transferPower(long value){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).transferPower(value);}

 @Override public java.math.BigInteger transferPowerExact(java.math.BigInteger value){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).transferPowerExact(value);}

 @Override public long getReceiverSpeed(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceiverSpeed();}

 @Override public java.math.BigInteger getReceiverSpeedExact(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceiverSpeedExact();}

 @Override public long getProviderSpeed(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getProviderSpeed();}return EnergyProvider.super.getProviderSpeed();}

 @Override public java.math.BigInteger getProviderSpeedExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getProviderSpeedExact();}return EnergyProvider.super.getProviderSpeedExact();}

 @Override public void usePowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.usePowerExact(value);return;}EnergyProvider.super.usePowerExact(value);}

 @Override public com.hbm.energy.PowerNode energyNode(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).energyNode();}

 @Override public long getDemand(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getDemand(type,pressure);}return StandardFluidTransceiver.super.getDemand(type,pressure);}

 @Override public int[] getReceivingPressureRange(com.hbm.fluid.HbmFluidType type){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingPressureRange(type);}return StandardFluidTransceiver.super.getReceivingPressureRange(type);}

 @Override public long getReceiverSpeed(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed(type,pressure);}return StandardFluidTransceiver.super.getReceiverSpeed(type,pressure);}

 @Override public int getHeatStored(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getHeatStored();}

 @Override public void useUpHeat(int amount){com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).useUpHeat(amount);}

 @Override public boolean canAcceptPartialFlow(net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canAcceptPartialFlow(side,stack);}

 @Override public com.hbm.material.MaterialStack flow(net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).flow(side,stack);}

 @Override public boolean canAcceptPartialPour(net.minecraft.world.phys.Vec3 hit,net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canAcceptPartialPour(hit,side,stack);}

 @Override public com.hbm.material.MaterialStack pour(net.minecraft.world.phys.Vec3 hit,net.minecraft.core.Direction side,com.hbm.material.MaterialStack stack){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).pour(hit,side,stack);}

 @Override public boolean canComputerConnect(net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canComputerConnect(side);}return ComputerControl.super.canComputerConnect(side);}

 @Override public String[] getFunctionInfo(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getFunctionInfo();}

 @Override public String provideRORValue(String name){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).provideRORValue(name);}

 @Override public String runRORFunction(String name,String[] args){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).runRORFunction(name,args);}

 @Override public java.math.BigInteger energyStoredExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStoredExact();}return MachineView.super.energyStoredExact();}

 @Override public java.math.BigInteger energyCapacityExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacityExact();}return MachineView.super.energyCapacityExact();}

 @Override public java.util.List<com.hbm.api.machine.MachineView.ProgressView> progressBars(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();}return MachineView.super.progressBars();}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
