package com.hbm.machine.precisionassembler;

import com.hbm.energy.EnergyConductor;
import com.hbm.energy.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original inventory-only dummy proxy; no energy or fluid capability on ordinary parts. */
public class PrecisionAssemblerPort extends BlockEntity implements WorldlyContainer,com.hbm.api.machine.MachineView,com.hbm.energy.EnergyReceiver,com.hbm.energy.EnergyConductor,com.hbm.fluid.network.StandardFluidTransceiver,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public PrecisionAssemblerPort(BlockPos pos,BlockState state) { this(PrecisionAssemblerContent.PORT.get(),pos,state); }
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&com.hbm.multiblock.DummyableBlock.isPort(state)&&!com.hbm.multiblock.DummyableBlock.isCore(state);}
    protected PrecisionAssemblerPort(net.minecraft.world.level.block.entity.BlockEntityType<?> type,BlockPos pos,BlockState state) { super(type,pos,state); }
    protected PrecisionAssemblerBlockEntity core() { if(level==null||!(getBlockState().getBlock() instanceof PrecisionAssemblerBlock block)) return null;BlockPos pos=block.findCore(level,worldPosition);return pos!=null&&level.getBlockEntity(pos) instanceof PrecisionAssemblerBlockEntity core?core:null; }
    @Override public java.util.List<ProgressView> progressBars(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();}var core=core();return core==null?java.util.List.of():core.progressBars();}
    @Override public long energyStored() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();} var core=core(); return core==null?0:core.energyStored(); }
    @Override public long energyCapacity() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();} return core()==null?0:core().getMaxPower(); }
    @Override public java.util.List<TankView> tanks() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();} var core=core(); return core==null?java.util.List.of():core.tanks(); }
    @Override public int getContainerSize() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();}return core()==null?0:22;}
    @Override public boolean isEmpty() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();}var core=core();return core==null||core.isEmpty();}
    @Override public ItemStack getItem(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);}var core=core();return core==null?ItemStack.EMPTY:core.getItem(slot);}
    @Override public ItemStack removeItem(int slot,int count) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,count);}var core=core();return core==null?ItemStack.EMPTY:core.removeItem(slot,count);}
    @Override public ItemStack removeItemNoUpdate(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);}var core=core();return core==null?ItemStack.EMPTY:core.removeItemNoUpdate(slot);}
    @Override public void setItem(int slot,ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,stack);return;}var core=core();if(core!=null)core.setItem(slot,stack);}
    @Override public void setChanged() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){super.setChanged();binding.setChanged();return;}super.setChanged();var core=core();if(core!=null)core.setChanged();}
    @Override public boolean stillValid(Player player) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);}var core=core();return core!=null&&core.stillValid(player);}
    @Override public void clearContent() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;}var core=core();if(core!=null)core.clearContent();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);}var core=core();return core!=null&&core.canPlaceItem(slot,stack);}
    @Override public int[] getSlotsForFace(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(side);}return core()==null?new int[0]:new int[]{4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,side);}var core=core();return core!=null&&core.canPlaceItemThroughFace(slot,stack,side);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,side);}var core=core();return core!=null&&core.canTakeItemThroughFace(slot,stack,side);}
    @Override public PowerNode energyNode(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyNode();}var core=core();return core==null?null:core.nodeAt(worldPosition);}
    @Override public boolean canConnect(Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(side);}var core=core();return core!=null&&core.canConnect(side);}
    @Override public boolean canConnect(com.hbm.fluid.HbmFluidType type,Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(type,side);}return canConnect(side);}
    @Override public boolean isLoaded(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();}return !isRemoved()&&core()!=null;}
    @Override public long getPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPower();}var core=core();return core==null?0:core.getPower();}@Override public long getMaxPower(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPower();}var core=core();return core==null?0:core.getMaxPower();}@Override public void setPower(long value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPower(value);return;}var core=core();if(core!=null)core.setPower(value);}
    @Override public com.hbm.fluid.FluidTank[] getAllTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getAllTanks();}var core=core();return core==null?com.hbm.fluid.FluidTank.EMPTY_ARRAY:core.getAllTanks();}@Override public com.hbm.fluid.FluidTank[] getReceivingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingTanks();}var core=core();return core==null?com.hbm.fluid.FluidTank.EMPTY_ARRAY:core.getReceivingTanks();}@Override public com.hbm.fluid.FluidTank[] getSendingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSendingTanks();}var core=core();return core==null?com.hbm.fluid.FluidTank.EMPTY_ARRAY:core.getSendingTanks();}
    @Override public long transferFluid(com.hbm.fluid.HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferFluid(type,pressure,amount);}var core=core();return core==null?amount:core.transferFluid(type,pressure,amount);}

 @Override public int getMaxStackSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxStackSize();}return WorldlyContainer.super.getMaxStackSize();}

 @Override public java.math.BigInteger getPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPowerExact();}return com.hbm.energy.EnergyReceiver.super.getPowerExact();}

 @Override public java.math.BigInteger getMaxPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPowerExact();}return com.hbm.energy.EnergyReceiver.super.getMaxPowerExact();}

 @Override public void setPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPowerExact(value);return;}com.hbm.energy.EnergyReceiver.super.setPowerExact(value);}

 @Override public long transferPower(long value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPower(value);}return com.hbm.energy.EnergyReceiver.super.transferPower(value);}

 @Override public java.math.BigInteger transferPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPowerExact(value);}return com.hbm.energy.EnergyReceiver.super.transferPowerExact(value);}

 @Override public long getReceiverSpeed(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed();}return com.hbm.energy.EnergyReceiver.super.getReceiverSpeed();}

 @Override public java.math.BigInteger getReceiverSpeedExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeedExact();}return com.hbm.energy.EnergyReceiver.super.getReceiverSpeedExact();}

 @Override public long getDemand(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getDemand(type,pressure);}return com.hbm.fluid.network.StandardFluidTransceiver.super.getDemand(type,pressure);}

 @Override public int[] getReceivingPressureRange(com.hbm.fluid.HbmFluidType type){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingPressureRange(type);}return com.hbm.fluid.network.StandardFluidTransceiver.super.getReceivingPressureRange(type);}

 @Override public long getReceiverSpeed(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed(type,pressure);}return com.hbm.fluid.network.StandardFluidTransceiver.super.getReceiverSpeed(type,pressure);}

 @Override public void useUpFluid(com.hbm.fluid.HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.useUpFluid(type,pressure,amount);return;}com.hbm.fluid.network.StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);}

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

 @Override public java.math.BigInteger energyStoredExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStoredExact();}return com.hbm.api.machine.MachineView.super.energyStoredExact();}

 @Override public java.math.BigInteger energyCapacityExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacityExact();}return com.hbm.api.machine.MachineView.super.energyCapacityExact();}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}
}
