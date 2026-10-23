package com.hbm.machine.radar;
import com.hbm.api.machine.MachineView;import com.hbm.energy.*;import com.hbm.multiblock.DummyableBlock;import net.minecraft.core.*;import net.minecraft.world.level.block.entity.BlockEntity;import net.minecraft.world.level.block.state.BlockState;
/** Source large-radar Combo.power only. */
public final class RadarPort extends BlockEntity implements EnergyReceiver,SidedEnergyConnector,MachineView,net.minecraft.world.WorldlyContainer,com.hbm.energy.EnergyConductor,com.hbm.fluid.network.FluidReceiver,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {public RadarPort(BlockPos p,BlockState s){super(RadarContent.PORT.get(),p,s);}@Override public boolean isValidBlockState(BlockState s){return s.is(RadarContent.LARGE.get())&&DummyableBlock.isPort(s);}public RadarBlockEntity core(){if(level==null)return null;var p=RadarContent.LARGE.get().findCore(level,worldPosition);return p!=null&&level.getBlockEntity(p) instanceof RadarBlockEntity r?r:null;}@Override public boolean isLoaded(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();}return !isRemoved()&&core()!=null;}@Override public boolean canConnect(Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(side);}return side!=null;}@Override public long getPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPower();}var r=core();return r==null?0:r.power;}@Override public void setPower(long v){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPower(v);return;}var r=core();if(r!=null)r.setPower(v);}@Override public long getMaxPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPower();}return RadarConfig.maxPower;}@Override public long energyStored(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();}return getPower();}@Override public long energyCapacity(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();}return getMaxPower();}
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

 @Override public java.math.BigInteger getPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPowerExact();}return EnergyReceiver.super.getPowerExact();}

 @Override public java.math.BigInteger getMaxPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPowerExact();}return EnergyReceiver.super.getMaxPowerExact();}

 @Override public void setPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPowerExact(value);return;}EnergyReceiver.super.setPowerExact(value);}

 @Override public long transferPower(long value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPower(value);}return EnergyReceiver.super.transferPower(value);}

 @Override public java.math.BigInteger transferPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPowerExact(value);}return EnergyReceiver.super.transferPowerExact(value);}

 @Override public long getReceiverSpeed(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed();}return EnergyReceiver.super.getReceiverSpeed();}

 @Override public java.math.BigInteger getReceiverSpeedExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeedExact();}return EnergyReceiver.super.getReceiverSpeedExact();}

 @Override public com.hbm.energy.PowerNode energyNode(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).energyNode();}

 @Override public boolean canConnect(com.hbm.fluid.HbmFluidType type,net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canConnect(type,side);}

 @Override public com.hbm.fluid.FluidTank[] getAllTanks(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getAllTanks();}

 @Override public long transferFluid(com.hbm.fluid.HbmFluidType type,int pressure,long amount){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).transferFluid(type,pressure,amount);}

 @Override public long getDemand(com.hbm.fluid.HbmFluidType type,int pressure){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getDemand(type,pressure);}

 @Override public int[] getReceivingPressureRange(com.hbm.fluid.HbmFluidType type){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceivingPressureRange(type);}

 @Override public long getReceiverSpeed(com.hbm.fluid.HbmFluidType type,int pressure){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceiverSpeed(type,pressure);}

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

 @Override public java.math.BigInteger energyStoredExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStoredExact();}return MachineView.super.energyStoredExact();}

 @Override public java.math.BigInteger energyCapacityExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacityExact();}return MachineView.super.energyCapacityExact();}

 @Override public java.util.List<com.hbm.api.machine.MachineView.TankView> tanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();}return MachineView.super.tanks();}

 @Override public java.util.List<com.hbm.api.machine.MachineView.ProgressView> progressBars(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();}return MachineView.super.progressBars();}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
