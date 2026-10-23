package com.hbm.multiblock.proxy;
import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.HeatSource;
import com.hbm.machine.foundry.FoundryAcceptor;
import com.hbm.material.MaterialStack;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Real source TileEntityProxyCombo on the research reactor's original6..11 parts. No inventory/HE/fluid storage. */
public class SourceStandaloneProxy extends BlockEntity implements SourceRebindableProxy,WorldlyContainer,EnergyReceiver,EnergyConductor,FluidReceiver,HeatSource,FoundryAcceptor,ComputerControl,IRORValueProvider,IRORInteractive,MachineView,com.hbm.multiblock.MuffledMachine {
 private boolean muffled,tilted;
 public SourceStandaloneProxy(BlockPos pos,BlockState state){this(SourceProxyContent.COMBO.get(),pos,state);}
 protected SourceStandaloneProxy(net.minecraft.world.level.block.entity.BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&com.hbm.multiblock.DummyableBlock.isPort(state);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
 @Override public void setChanged(){super.setChanged();SourceProxyBindings.delegate(this).setChanged();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);SourceProxyBindings.save(this,tag);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");SourceProxyBindings.load(this,tag);}
 @Override public int getContainerSize(){return SourceProxyBindings.delegate(this).getContainerSize();}
 @Override public boolean isEmpty(){return SourceProxyBindings.delegate(this).isEmpty();}
 @Override public int getMaxStackSize(){return SourceProxyBindings.delegate(this).getMaxStackSize();}
 @Override public ItemStack getItem(int slot){return SourceProxyBindings.delegate(this).getItem(slot);}
 @Override public ItemStack removeItem(int slot,int count){return SourceProxyBindings.delegate(this).removeItem(slot,count);}
 @Override public ItemStack removeItemNoUpdate(int slot){return SourceProxyBindings.delegate(this).removeItemNoUpdate(slot);}
 @Override public void setItem(int slot,ItemStack stack){SourceProxyBindings.delegate(this).setItem(slot,stack);}
 @Override public void clearContent(){SourceProxyBindings.delegate(this).clearContent();}
 @Override public boolean stillValid(Player player){return SourceProxyBindings.delegate(this).stillValid(player);}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return SourceProxyBindings.delegate(this).canPlaceItem(slot,stack);}
 @Override public int[] getSlotsForFace(Direction side){return SourceProxyBindings.delegate(this).getSlotsForFace(side);}
 @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return SourceProxyBindings.delegate(this).canPlaceItemThroughFace(slot,stack,side);}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return SourceProxyBindings.delegate(this).canTakeItemThroughFace(slot,stack,side);}
 @Override public long getPower(){return SourceProxyBindings.delegate(this).getPower();}
 @Override public long getMaxPower(){return SourceProxyBindings.delegate(this).getMaxPower();}
 @Override public void setPower(long value){SourceProxyBindings.delegate(this).setPower(value);}
 @Override public BigInteger getPowerExact(){return SourceProxyBindings.delegate(this).getPowerExact();}
 @Override public BigInteger getMaxPowerExact(){return SourceProxyBindings.delegate(this).getMaxPowerExact();}
 @Override public void setPowerExact(BigInteger value){SourceProxyBindings.delegate(this).setPowerExact(value);}
 @Override public long transferPower(long value){return SourceProxyBindings.delegate(this).transferPower(value);}
 @Override public BigInteger transferPowerExact(BigInteger value){return SourceProxyBindings.delegate(this).transferPowerExact(value);}
 @Override public long getReceiverSpeed(){return SourceProxyBindings.delegate(this).getReceiverSpeed();}
 @Override public BigInteger getReceiverSpeedExact(){return SourceProxyBindings.delegate(this).getReceiverSpeedExact();}
 @Override public boolean isLoaded(){return SourceProxyBindings.delegate(this).isLoaded();}
 @Override public boolean canConnect(Direction side){return SourceProxyBindings.delegate(this).canConnect(side);}
 @Override public PowerNode energyNode(){return SourceProxyBindings.delegate(this).energyNode();}
 @Override public boolean canConnect(HbmFluidType type,Direction side){return SourceProxyBindings.delegate(this).canConnect(type,side);}
 @Override public FluidTank[] getAllTanks(){return SourceProxyBindings.delegate(this).getAllTanks();}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){return SourceProxyBindings.delegate(this).transferFluid(type,pressure,amount);}
 @Override public long getDemand(HbmFluidType type,int pressure){return SourceProxyBindings.delegate(this).getDemand(type,pressure);}
 @Override public int[] getReceivingPressureRange(HbmFluidType type){return SourceProxyBindings.delegate(this).getReceivingPressureRange(type);}
 @Override public long getReceiverSpeed(HbmFluidType type,int pressure){return SourceProxyBindings.delegate(this).getReceiverSpeed(type,pressure);}
 @Override public int getHeatStored(){return SourceProxyBindings.delegate(this).getHeatStored();}
 @Override public void useUpHeat(int amount){SourceProxyBindings.delegate(this).useUpHeat(amount);}
 @Override public boolean canAcceptPartialFlow(Direction side,MaterialStack stack){return SourceProxyBindings.delegate(this).canAcceptPartialFlow(side,stack);}
 @Override public MaterialStack flow(Direction side,MaterialStack stack){return SourceProxyBindings.delegate(this).flow(side,stack);}
 @Override public boolean canAcceptPartialPour(Vec3 hit,Direction side,MaterialStack stack){return SourceProxyBindings.delegate(this).canAcceptPartialPour(hit,side,stack);}
 @Override public MaterialStack pour(Vec3 hit,Direction side,MaterialStack stack){return SourceProxyBindings.delegate(this).pour(hit,side,stack);}
 @Override public String computerType(){return SourceProxyBindings.delegate(this).computerType();}
 @Override public String[] computerMethods(){return SourceProxyBindings.delegate(this).computerMethods();}
 @Override public Object[] callComputer(String method,ComputerArguments args){return SourceProxyBindings.delegate(this).callComputer(method,args);}
 @Override public boolean canComputerConnect(Direction side){return SourceProxyBindings.delegate(this).canComputerConnect(side);}
 @Override public String[] getFunctionInfo(){return SourceProxyBindings.delegate(this).getFunctionInfo();}
 @Override public String provideRORValue(String name){return SourceProxyBindings.delegate(this).provideRORValue(name);}
 @Override public String runRORFunction(String name,String[] args){return SourceProxyBindings.delegate(this).runRORFunction(name,args);}
 @Override public long energyStored(){return SourceProxyBindings.delegate(this).energyStored();}
 @Override public long energyCapacity(){return SourceProxyBindings.delegate(this).energyCapacity();}
 @Override public BigInteger energyStoredExact(){return SourceProxyBindings.delegate(this).energyStoredExact();}
 @Override public BigInteger energyCapacityExact(){return SourceProxyBindings.delegate(this).energyCapacityExact();}
 @Override public List<TankView> tanks(){return SourceProxyBindings.delegate(this).tanks();}
 @Override public List<ProgressView> progressBars(){return SourceProxyBindings.delegate(this).progressBars();}
}
