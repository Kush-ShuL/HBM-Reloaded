package com.hbm.machine.reactor.zirnox;
import com.hbm.machine.reactor.ReactorInventory;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidTransceiver;
import com.hbm.api.machine.MachineView;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source TileEntityProxyInventory: no duplicate inventory or power/fluid buffer. */
public final class ZirnoxPort extends BlockEntity implements WorldlyContainer,MachineView,StandardFluidTransceiver,com.hbm.energy.EnergyReceiver,com.hbm.energy.EnergyConductor,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public ZirnoxPort(BlockPos pos,BlockState state){super(ZirnoxContent.PORT_ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&!DummyableBlock.isCore(state);}
    private ZirnoxBlockEntity core(){if(level==null||!(getBlockState().getBlock() instanceof DummyableBlock block))return null;var p=block.findCore(level,worldPosition);return p!=null&&level.getBlockEntity(p) instanceof ZirnoxBlockEntity machine?machine:null;}
    @Override public int getContainerSize(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getContainerSize();}var c=core();return c==null?0:c.getContainerSize();}@Override public boolean isEmpty(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isEmpty();}var c=core();return c==null||c.isEmpty();}@Override public ItemStack getItem(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getItem(slot);}var c=core();return c==null?ItemStack.EMPTY:c.getItem(slot);}@Override public ItemStack removeItem(int slot,int amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItem(slot,amount);}var c=core();return c==null?ItemStack.EMPTY:c.removeItem(slot,amount);}@Override public ItemStack removeItemNoUpdate(int slot){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.removeItemNoUpdate(slot);}var c=core();return c==null?ItemStack.EMPTY:c.removeItemNoUpdate(slot);}@Override public void setItem(int slot,ItemStack stack){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.setItem(slot,stack);return;}var c=core();if(c!=null)c.setItem(slot,stack);}@Override public void clearContent(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.clearContent();return;}var c=core();if(c!=null)c.clearContent();}@Override public void setChanged(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){super.setChanged();binding.setChanged();return;}super.setChanged();var c=core();if(c!=null)c.setChanged();}@Override public boolean stillValid(Player player){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.stillValid(player);}var c=core();return c!=null&&c.stillValid(player);}@Override public boolean canPlaceItem(int slot,ItemStack stack){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItem(slot,stack);}var c=core();return c!=null&&c.canPlaceItem(slot,stack);}@Override public int[] getSlotsForFace(Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSlotsForFace(side);}var c=core();return c==null?new int[0]:c.getSlotsForFace(side);}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canPlaceItemThroughFace(slot,stack,side);}var c=core();return c!=null&&c.canPlaceItemThroughFace(slot,stack,side);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canTakeItemThroughFace(slot,stack,side);}var c=core();return c!=null&&c.canTakeItemThroughFace(slot,stack,side);}
    @Override public FluidTank[] getAllTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getAllTanks();}var c=core();return c==null?FluidTank.EMPTY_ARRAY:c.getAllTanks();}@Override public FluidTank[] getReceivingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingTanks();}var c=core();return c==null?FluidTank.EMPTY_ARRAY:c.getReceivingTanks();}@Override public FluidTank[] getSendingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSendingTanks();}var c=core();return c==null?FluidTank.EMPTY_ARRAY:c.getSendingTanks();}
    @Override public long getDemand(HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getDemand(type,pressure);}var c=core();return c==null?0:c.getDemand(type,pressure);}@Override public long getFluidAvailable(HbmFluidType type,int pressure){var c=core();return c==null?0:c.getFluidAvailable(type,pressure);}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferFluid(type,pressure,amount);}var c=core();return c==null?amount:c.transferFluid(type,pressure,amount);}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.useUpFluid(type,pressure,amount);return;}var c=core();if(c!=null)c.useUpFluid(type,pressure,amount);}@Override public long getProviderSpeed(HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getProviderSpeed(type,pressure);}var c=core();return c==null?0:c.getProviderSpeed(type,pressure);}@Override public long getReceiverSpeed(HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed(type,pressure);}var c=core();return c==null?0:c.getReceiverSpeed(type,pressure);}
    @Override public List<TankView> tanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();}var c=core();return c==null?List.of():c.tanks();}
    @Override public long energyStored(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();}return 0;}@Override public long energyCapacity(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();}return 0;}@Override public List<ProgressView> progressBars(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();}var c=core();return c==null?List.of():c.progressBars();}

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

 @Override public boolean isLoaded(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();}return StandardFluidTransceiver.super.isLoaded();}

 @Override public boolean canConnect(net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canConnect(side);}

 @Override public com.hbm.energy.PowerNode energyNode(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).energyNode();}

 @Override public boolean canConnect(com.hbm.fluid.HbmFluidType type,net.minecraft.core.Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(type,side);}return StandardFluidTransceiver.super.canConnect(type,side);}

 @Override public int[] getReceivingPressureRange(com.hbm.fluid.HbmFluidType type){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingPressureRange(type);}return StandardFluidTransceiver.super.getReceivingPressureRange(type);}

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

 @Override public net.minecraft.world.level.block.entity.BlockEntity sourceProxyFallbackTarget(){Object actual=core();return actual instanceof net.minecraft.world.level.block.entity.BlockEntity block?block:null;}

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}
}
