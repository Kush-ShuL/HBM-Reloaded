package com.hbm.machine.purex;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidReceiver;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original TileEntityProxyCombo.inventory().power().fluid(); outputs send through core ports. */
public final class PurexPort extends BlockEntity implements WorldlyContainer,MachineView,EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver,com.hbm.energy.EnergyConductor,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public PurexPort(BlockPos pos,BlockState state) { super(PurexContent.PORT.get(),pos,state); }
    @Override public boolean isValidBlockState(BlockState state) { return super.isValidBlockState(state)&&DummyableBlock.isPort(state); }
    private PurexBlockEntity core() {
        if (level==null || !(getBlockState().getBlock() instanceof PurexBlock block)) return null;
        var pos=block.findCore(level,worldPosition);
        return pos!=null && level.getBlockEntity(pos) instanceof PurexBlockEntity machine ? machine : null;
    }
    @Override public boolean isLoaded() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();} return !isRemoved()&&core()!=null; }
    @Override public boolean canConnect(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(side);} var machine=core(); return machine!=null&&machine.canConnect(side); }
    @Override public boolean canConnect(HbmFluidType type,Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(type,side);} var machine=core(); return machine!=null&&machine.canConnect(type,side); }
    @Override public long getPower() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPower();} var machine=core(); return machine==null?0:machine.getPower(); }
    @Override public long getMaxPower() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPower();} var machine=core(); return machine==null?0:machine.getMaxPower(); }
    @Override public void setPower(long value) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPower(value);return;} var machine=core(); if(machine!=null)machine.setPower(value); }
    @Override public long transferPower(long value) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPower(value);} var machine=core(); return machine==null?value:machine.transferPower(value); }
    @Override public FluidTank[] getAllTanks() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getAllTanks();} var machine=core(); return machine==null?FluidTank.EMPTY_ARRAY:machine.getAllTanks(); }
    @Override public FluidTank[] getReceivingTanks() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingTanks();} var machine=core(); return machine==null?FluidTank.EMPTY_ARRAY:machine.getReceivingTanks(); }
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferFluid(type,pressure,amount);} var machine=core(); return machine==null?amount:machine.transferFluid(type,pressure,amount); }
    @Override public long energyStored() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();} return getPower(); }
    @Override public long energyCapacity() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();} return getMaxPower(); }
    @Override public List<TankView> tanks() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();} var machine=core(); return machine==null?List.of():machine.tanks(); }
    @Override public List<ProgressView> progressBars(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();}var machine=core();return machine==null?List.of():machine.progressBars();}
    @Override public int getContainerSize() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();} return core()==null?0:13; }
    @Override public boolean isEmpty() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();} var machine=core(); return machine==null||machine.isEmpty(); }
    @Override public ItemStack getItem(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);} var machine=core(); return machine==null?ItemStack.EMPTY:machine.getItem(slot); }
    @Override public ItemStack removeItem(int slot,int amount) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,amount);} var machine=core(); return machine==null?ItemStack.EMPTY:machine.removeItem(slot,amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);} var machine=core(); return machine==null?ItemStack.EMPTY:machine.removeItemNoUpdate(slot); }
    @Override public void setItem(int slot,ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,stack);return;} var machine=core(); if(machine!=null)machine.setItem(slot,stack); }
    @Override public void setChanged() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){super.setChanged();binding.setChanged();return;} super.setChanged(); var machine=core(); if(machine!=null)machine.setChanged(); }
    @Override public boolean stillValid(Player player) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);} var machine=core(); return machine!=null&&machine.stillValid(player); }
    @Override public void clearContent() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;} var machine=core(); if(machine!=null)machine.clearContent(); }
    @Override public boolean canPlaceItem(int slot,ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);} var machine=core(); return machine!=null&&machine.canPlaceItem(slot,stack); }
    @Override public int[] getSlotsForFace(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(side);} return new int[]{4,5,6,7,8,9,10,11,12}; }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,side);} var machine=core(); return machine!=null&&machine.canPlaceItemThroughFace(slot,stack,side); }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,side);} var machine=core(); return machine!=null&&machine.canTakeItemThroughFace(slot,stack,side); }

 @Override public int getMaxStackSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxStackSize();}return WorldlyContainer.super.getMaxStackSize();}

 @Override public java.math.BigInteger getPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getPowerExact();}return EnergyReceiver.super.getPowerExact();}

 @Override public java.math.BigInteger getMaxPowerExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getMaxPowerExact();}return EnergyReceiver.super.getMaxPowerExact();}

 @Override public void setPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setPowerExact(value);return;}EnergyReceiver.super.setPowerExact(value);}

 @Override public java.math.BigInteger transferPowerExact(java.math.BigInteger value){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferPowerExact(value);}return EnergyReceiver.super.transferPowerExact(value);}

 @Override public long getReceiverSpeed(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed();}return EnergyReceiver.super.getReceiverSpeed();}

 @Override public java.math.BigInteger getReceiverSpeedExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeedExact();}return EnergyReceiver.super.getReceiverSpeedExact();}

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

 @Override public java.math.BigInteger energyStoredExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStoredExact();}return MachineView.super.energyStoredExact();}

 @Override public java.math.BigInteger energyCapacityExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacityExact();}return MachineView.super.energyCapacityExact();}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}
}
