package com.hbm.satellite.link;
import com.hbm.multiblock.DummyableBlock;import com.hbm.api.computer.*;import api.hbm.redstoneoverradio.*;import net.minecraft.core.*;import net.minecraft.world.level.block.entity.BlockEntity;import net.minecraft.world.level.block.state.BlockState;
/** Actual original ProxyCombo control forwarding; no invented energy/fluid/inventory storage or capability. */
public final class SatelliteLinkPort extends BlockEntity implements ComputerControl,IRORValueProvider,IRORInteractive,net.minecraft.world.WorldlyContainer,com.hbm.energy.EnergyReceiver,com.hbm.energy.EnergyConductor,com.hbm.fluid.network.FluidReceiver,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.multiblock.proxy.SourceRebindableProxy {
 public SatelliteLinkPort(BlockPos pos,BlockState state){super(SatelliteLinkContent.PORT.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isPort(state);}public SatelliteLinkBlockEntity core(){if(level==null||!(getBlockState().getBlock() instanceof SatelliteLinkBlock block))return null;var pos=block.findCore(level,worldPosition);return pos!=null&&level.getBlockEntity(pos) instanceof SatelliteLinkBlockEntity core?core:null;}
 @Override public String computerType(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.computerType();}return "ntm_satlink";}@Override public String[] computerMethods(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.computerMethods();}var core=core();return core==null?new String[0]:core.computerMethods();}@Override public Object[] callComputer(String method,ComputerArguments args){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.callComputer(method,args);}var core=core();if(core==null)throw new ComputerException("Machine is no longer available");return core.callComputer(method,args);}@Override public boolean canComputerConnect(Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canComputerConnect(side);}var core=core();if(core==null)return false;if(side==null)return true;var neighbor=worldPosition.relative(side);if(!level.hasChunkAt(neighbor))return true;var be=level.getBlockEntity(neighbor);return be!=core&&!(be instanceof SatelliteLinkPort other&&other.core()==core);}
 @Override public String[] getFunctionInfo(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getFunctionInfo();}var core=core();return core==null?new String[0]:core.getFunctionInfo();}@Override public String provideRORValue(String name){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.provideRORValue(name);}var core=core();return core==null?null:core.provideRORValue(name);}@Override public String runRORFunction(String name,String[] params){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.runRORFunction(name,params);}var core=core();return core==null?null:core.runRORFunction(name,params);}

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

 @Override public long getPower(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getPower();}

 @Override public long getMaxPower(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getMaxPower();}

 @Override public void setPower(long value){com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).setPower(value);}

 @Override public java.math.BigInteger getPowerExact(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getPowerExact();}

 @Override public java.math.BigInteger getMaxPowerExact(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getMaxPowerExact();}

 @Override public void setPowerExact(java.math.BigInteger value){com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).setPowerExact(value);}

 @Override public long transferPower(long value){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).transferPower(value);}

 @Override public java.math.BigInteger transferPowerExact(java.math.BigInteger value){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).transferPowerExact(value);}

 @Override public long getReceiverSpeed(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceiverSpeed();}

 @Override public java.math.BigInteger getReceiverSpeedExact(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getReceiverSpeedExact();}

 @Override public boolean isLoaded(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).isLoaded();}

 @Override public boolean canConnect(net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canConnect(side);}

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

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
