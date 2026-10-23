package com.hbm.machine.ashpit;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.api.machine.MachineView;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Resolves the live parent each operation; unloading/replacing a core cannot leave a stale inventory. */
public final class AshpitProxy extends BlockEntity implements WorldlyContainer,MachineView,com.hbm.energy.EnergyReceiver,com.hbm.energy.EnergyConductor,com.hbm.fluid.network.FluidReceiver,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public AshpitProxy(BlockPos pos, BlockState state) { super(AshpitContent.PROXY.get(), pos, state); }
    @Override public long energyStored(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();}return 0;}
    @Override public long energyCapacity(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();}return 0;}
    @Override public List<ProgressView> progressBars(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();}return target() instanceof MachineView machine?machine.progressBars():List.of();}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&!DummyableBlock.isCore(state);}
    private Container target() {
        if (level == null || !(getBlockState().getBlock() instanceof DummyableBlock block)) return null;
        BlockPos core = block.findCore(level, worldPosition);
        return core != null && level.getBlockEntity(core) instanceof Container inventory ? inventory : null;
    }
    @Override public int getContainerSize() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();} Container target = target(); return target == null ? 0 : target.getContainerSize(); }
    @Override public boolean isEmpty() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();} Container target = target(); return target == null || target.isEmpty(); }
    @Override public ItemStack getItem(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);} Container target = target(); return target == null ? ItemStack.EMPTY : target.getItem(slot); }
    @Override public ItemStack removeItem(int slot, int count) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,count);} Container target = target(); return target == null ? ItemStack.EMPTY : target.removeItem(slot,count); }
    @Override public ItemStack removeItemNoUpdate(int slot) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);} Container target = target(); return target == null ? ItemStack.EMPTY : target.removeItemNoUpdate(slot); }
    @Override public void setItem(int slot, ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,stack);return;} Container target = target(); if (target != null) target.setItem(slot,stack); }
    @Override public void setChanged() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){super.setChanged();binding.setChanged();return;} super.setChanged(); Container target = target(); if (target != null) target.setChanged(); }
    @Override public boolean stillValid(Player player) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);} Container target = target(); return target != null && target.stillValid(player); }
    @Override public void clearContent() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;} Container target = target(); if (target != null) target.clearContent(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);} Container target = target(); return target != null && target.canPlaceItem(slot,stack); }
    @Override public int[] getSlotsForFace(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(side);} Container target = target(); return target instanceof WorldlyContainer sided ? sided.getSlotsForFace(side) : new int[0]; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,side);} Container target = target(); return target instanceof WorldlyContainer sided && sided.canPlaceItemThroughFace(slot,stack,side); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,side);} Container target = target(); return target instanceof WorldlyContainer sided && sided.canTakeItemThroughFace(slot,stack,side); }

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

 @Override public java.math.BigInteger energyStoredExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStoredExact();}return java.math.BigInteger.valueOf(energyStored());}

 @Override public java.math.BigInteger energyCapacityExact(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacityExact();}return java.math.BigInteger.valueOf(energyCapacity());}

 @Override public java.util.List<com.hbm.api.machine.MachineView.TankView> tanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();}return MachineView.super.tanks();}

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=target();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}
}
