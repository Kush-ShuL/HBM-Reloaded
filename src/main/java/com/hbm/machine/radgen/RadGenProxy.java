package com.hbm.machine.radgen;

import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.EnergyReceiver;
import com.hbm.energy.SidedEnergyConnector;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.multiblock.MuffledMachine;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Actual ProxyCombo(true,true,false). Its receiver delegation rejects a provider-only core, as source does. */
public final class RadGenProxy extends BlockEntity implements WorldlyContainer,EnergyReceiver,SidedEnergyConnector,MachineView,IRORValueProvider,MuffledMachine,com.hbm.energy.EnergyConductor,com.hbm.fluid.network.FluidReceiver,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public RadGenProxy(BlockPos pos, BlockState state) { super(RadGenContent.PROXY.get(), pos, state); }
    @Override public boolean isValidBlockState(BlockState state) { return getType().isValid(state) && DummyableBlock.isPort(state); }
    public RadGenEntity core() { if (level == null || !(getBlockState().getBlock() instanceof RadGenBlock block)) return null; var pos = block.findCore(level, worldPosition); return pos != null && level.getBlockEntity(pos) instanceof RadGenEntity core ? core : null; }
    @Override public boolean isLoaded() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();} return !isRemoved() && core() != null; }
    @Override public boolean canConnect(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(side);} return side != null && core() != null; }
    @Override public long getPower() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPower();} return 0; }
    @Override public long getMaxPower() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPower();} return 0; }
    @Override public void setPower(long power) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPower(power);return;}}
    @Override public void setPowerExact(BigInteger power) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPowerExact(power);return;}}
    @Override public long transferPower(long amount) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPower(amount);} return amount; }
    @Override public BigInteger transferPowerExact(BigInteger amount) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPowerExact(amount);} return amount; }
    @Override public long energyStored() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();} var core = core(); return core == null ? 0 : core.getPower(); }
    @Override public long energyCapacity() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();} return RadGenEntity.MAX_POWER; }
    @Override public BigInteger energyStoredExact() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStoredExact();} var core = core(); return core == null ? BigInteger.ZERO : core.getPowerExact(); }
    @Override public BigInteger energyCapacityExact() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacityExact();} return BigInteger.valueOf(RadGenEntity.MAX_POWER); }
    @Override public List<ProgressView> progressBars() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();} var core = core(); return core == null ? List.of() : core.progressBars(); }
    @Override public String[] getFunctionInfo() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getFunctionInfo();} var core = core(); return core == null ? new String[0] : core.getFunctionInfo(); }
    @Override public String provideRORValue(String name) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.provideRORValue(name);} var core = core(); return core == null ? null : core.provideRORValue(name); }
    @Override public int getContainerSize() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();} return 24; }
    @Override public boolean isEmpty() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();} var core = core(); return core == null || core.isEmpty(); }
    @Override public ItemStack getItem(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);} var core = core(); return core == null ? ItemStack.EMPTY : core.getItem(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,amount);} var core = core(); return core == null ? ItemStack.EMPTY : core.removeItem(slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);} var core = core(); return core == null ? ItemStack.EMPTY : core.removeItemNoUpdate(slot); }
    @Override public void setItem(int slot, ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,stack);return;} var core = core(); if (core != null) core.setItem(slot, stack); }
    @Override public void clearContent() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;} var core = core(); if (core != null) core.clearContent(); }
    @Override public boolean stillValid(Player player) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);} var core = core(); return core != null && core.stillValid(player); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);} var core = core(); return core != null && core.canPlaceItem(slot, stack); }
    @Override public int[] getSlotsForFace(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(side);} var core = core(); return core == null ? new int[0] : core.getSlotsForFace(side); }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,side);} return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,side);} var core = core(); return core != null && core.canTakeItemThroughFace(slot, stack, side); }
    @Override public boolean isMuffled() { var core = core(); return core != null && core.isMuffled(); }
    @Override public void setMuffled(boolean value) { var core = core(); if (core != null) core.setMuffled(value); }

 @Override public int getMaxStackSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxStackSize();}return WorldlyContainer.super.getMaxStackSize();}

 @Override public java.math.BigInteger getPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPowerExact();}return EnergyReceiver.super.getPowerExact();}

 @Override public java.math.BigInteger getMaxPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPowerExact();}return EnergyReceiver.super.getMaxPowerExact();}

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

 @Override public String runRORFunction(String name,String[] args){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).runRORFunction(name,args);}

 @Override public java.util.List<com.hbm.api.machine.MachineView.TankView> tanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();}return MachineView.super.tanks();}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
