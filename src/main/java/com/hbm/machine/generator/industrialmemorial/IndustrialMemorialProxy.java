package com.hbm.machine.generator.industrialmemorial;
import com.hbm.multiblock.DummyableBlock;import net.minecraft.core.*;import net.minecraft.world.WorldlyContainer;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.block.entity.BlockEntity;import net.minecraft.world.level.block.state.BlockState;
/** Source ordinary dummies delegate inventory; extras have inventory=false, and no core HE/fluid can accept anything. Normal source placement has no dummy at all. */
public final class IndustrialMemorialProxy extends BlockEntity implements WorldlyContainer,com.hbm.energy.EnergyReceiver,com.hbm.energy.EnergyConductor,com.hbm.fluid.network.FluidReceiver,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
 public IndustrialMemorialProxy(BlockPos pos,BlockState state){super(IndustrialMemorialContent.PROXY.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&!DummyableBlock.isCore(state);}private IndustrialMemorialBlockEntity core(){if(level==null||DummyableBlock.isPort(getBlockState())||!(getBlockState().getBlock() instanceof IndustrialMemorialBlock block))return null;var pos=block.findCore(level,worldPosition);return pos!=null&&level.getBlockEntity(pos) instanceof IndustrialMemorialBlockEntity core?core:null;}@Override public int getContainerSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();}var core=core();return core==null?0:21;}@Override public boolean isEmpty(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();}var core=core();return core==null||core.isEmpty();}@Override public ItemStack getItem(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);}var core=core();return core==null?ItemStack.EMPTY:core.getItem(slot);}@Override public ItemStack removeItem(int slot,int count){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,count);}var core=core();return core==null?ItemStack.EMPTY:core.removeItem(slot,count);}@Override public ItemStack removeItemNoUpdate(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);}var core=core();return core==null?ItemStack.EMPTY:core.removeItemNoUpdate(slot);}@Override public void setItem(int slot,ItemStack stack){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,stack);return;}var core=core();if(core!=null)core.setItem(slot,stack);}@Override public void clearContent(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;}var core=core();if(core!=null)core.clearContent();}@Override public boolean stillValid(Player player){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);}var core=core();return core!=null&&core.stillValid(player);}@Override public boolean canPlaceItem(int slot,ItemStack stack){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);}return false;}@Override public int[] getSlotsForFace(Direction face){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(face);}return new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,face);}return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,face);}return false;}

 @Override public int getMaxStackSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxStackSize();}return WorldlyContainer.super.getMaxStackSize();}

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

 @Override public String computerType(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).computerType();}

 @Override public String[] computerMethods(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).computerMethods();}

 @Override public Object[] callComputer(String method,com.hbm.api.computer.ComputerArguments args){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).callComputer(method,args);}

 @Override public boolean canComputerConnect(net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canComputerConnect(side);}

 @Override public String[] getFunctionInfo(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).getFunctionInfo();}

 @Override public String provideRORValue(String name){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).provideRORValue(name);}

 @Override public String runRORFunction(String name,String[] args){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).runRORFunction(name,args);}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
