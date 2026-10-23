package com.hbm.machine.woodburner;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidReceiver;
import net.minecraft.core.*;
import net.minecraft.world.level.block.state.BlockState;
/** Port proxies delegate storage to the live core; topology is owned once by that core. */
public final class WoodBurnerPort extends WoodBurnerInventoryProxy implements EnergyProvider,EnergyConductor,StandardFluidReceiver,net.minecraft.world.WorldlyContainer,com.hbm.energy.EnergyReceiver,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public WoodBurnerPort(BlockPos pos,BlockState state) { super(WoodBurnerContent.PORT.get(),pos,state); }
    @Override public PowerNode energyNode() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyNode();} var core=core(); return core==null?null:core.energyNode(); }
    @Override public long getPower() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPower();} var core=core(); return core==null?0:core.getPower(); }
    @Override public long getMaxPower() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPower();} return WoodBurnerBlockEntity.MAX_POWER; }
    @Override public void setPower(long power) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPower(power);return;} var core=core(); if(core!=null) core.setPower(power); }
    @Override public boolean canConnect(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(side);} var core=core(); return core!=null&&core.canConnect(side); }
    @Override public boolean canConnect(HbmFluidType type,Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(type,side);} return canConnect(side); }
    @Override public boolean isLoaded() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();} return !isRemoved()&&core()!=null; }
    @Override public FluidTank[] getAllTanks() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getAllTanks();} var core=core(); return core==null?FluidTank.EMPTY_ARRAY:core.getAllTanks(); }
    @Override public FluidTank[] getReceivingTanks() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingTanks();} return getAllTanks(); }
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferFluid(type,pressure,amount);} var core=core(); return core==null?amount:core.transferFluid(type,pressure,amount); }

 @Override public int getContainerSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();}return super.getContainerSize();}

 @Override public boolean isEmpty(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();}return super.isEmpty();}

 @Override public int getMaxStackSize(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getMaxStackSize();}

 @Override public net.minecraft.world.item.ItemStack getItem(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);}return super.getItem(slot);}

 @Override public net.minecraft.world.item.ItemStack removeItem(int slot,int count){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,count);}return super.removeItem(slot,count);}

 @Override public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);}return super.removeItemNoUpdate(slot);}

 @Override public void setItem(int slot,net.minecraft.world.item.ItemStack stack){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,stack);return;}super.setItem(slot,stack);}

 @Override public void clearContent(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;}super.clearContent();}

 @Override public boolean stillValid(net.minecraft.world.entity.player.Player player){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);}return super.stillValid(player);}

 @Override public boolean canPlaceItem(int slot,net.minecraft.world.item.ItemStack stack){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);}return super.canPlaceItem(slot,stack);}

 @Override public int[] getSlotsForFace(net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(side);}return super.getSlotsForFace(side);}

 @Override public boolean canPlaceItemThroughFace(int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,side);}return super.canPlaceItemThroughFace(slot,stack,side);}

 @Override public boolean canTakeItemThroughFace(int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,side);}return super.canTakeItemThroughFace(slot,stack,side);}

 @Override public java.math.BigInteger getPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPowerExact();}return java.math.BigInteger.valueOf(getPower());}

 @Override public java.math.BigInteger getMaxPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPowerExact();}return java.math.BigInteger.valueOf(getMaxPower());}

 @Override public void setPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPowerExact(value);return;}setPower(value.longValueExact());}

 @Override public long transferPower(long value){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).transferPower(value);}

 @Override public java.math.BigInteger transferPowerExact(java.math.BigInteger value){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).transferPowerExact(value);}

 @Override public long getReceiverSpeed(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceiverSpeed();}

 @Override public java.math.BigInteger getReceiverSpeedExact(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceiverSpeedExact();}

 @Override public long getProviderSpeed(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getProviderSpeed();}return EnergyProvider.super.getProviderSpeed();}

 @Override public java.math.BigInteger getProviderSpeedExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getProviderSpeedExact();}return EnergyProvider.super.getProviderSpeedExact();}

 @Override public void usePower(long value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.usePower(value);return;}EnergyProvider.super.usePower(value);}

 @Override public void usePowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.usePowerExact(value);return;}EnergyProvider.super.usePowerExact(value);}

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

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}
}
