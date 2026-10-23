package com.hbm.machine.pneumaticstorage;

import api.hbm.ntl.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.pneumatic.*;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Real compressed AIR4000/pressure1 storage base; monitors reference the actual inventory. */
public abstract class PneumaticStorageBase extends PneumaticInventory implements StandardFluidReceiver,ISlotMonitorProvider {
    public final FluidTank compair=new FluidTank(HbmFluids.AIR,4000).withPressure(1);
    public final SlotMonitor[] monitors;
    protected PneumaticNode node;
    private FluidPorts fluidPorts;
    protected boolean wasAvailable;
    protected PneumaticStorageBase(PneumaticKind kind,BlockPos pos,BlockState state){super(kind,pos,state);monitors=new SlotMonitor[slots.length];for(int i=0;i<monitors.length;i++)monitors[i]=new SlotMonitor(i,this);}
    @Override protected void updateServer(ServerLevel server){
        if(fluidPorts==null)fluidPorts=new FluidPorts(worldPosition);fluidPorts.update(server,this);
        boolean available=isAvailable();if(available!=wasAvailable){wasAvailable=available;for(var monitor:monitors)monitor.availabilityHasChanged();}
        node=PneumaticNetworks.connect(this);if(node!=null&&node.isValid()&&node.network()!=null)node.network().storages.add(this);
        if(compair.getFill()>0){int consumption=compair.getFill()*9/compair.getMaxFill()+1;compair.setFill(Math.max(0,compair.getFill()-consumption));setChanged();}
        updateMonitors();synchronize(server);
    }
    public boolean isAvailable(){return level!=null&&!isRemoved()&&compair.getFill()>0;}
    public void cyclePressure(){int next=compair.getPressure()+1;if(next>5)next=1;compair.setTankType(HbmFluids.AIR);compair.withPressure(next);for(var monitor:monitors)monitor.availabilityHasChanged();setChanged();}
    private void disconnect(){for(var monitor:monitors){for(var cache:List.copyOf(monitor.viewedBy))cache.removeMonitor(monitor);monitor.viewedBy.clear();}if(node!=null&&node.network()!=null)node.network().storages.remove(this);PneumaticNetworks.disconnect(this);if(level instanceof ServerLevel server&&fluidPorts!=null)fluidPorts.remove(server);fluidPorts=null;node=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return true;}
    @Override public SlotMonitor[] getMonitors(){return monitors;}
    @Override public ItemStack getSlotAt(int index){return slots[index];}
    @Override public PneumaticNetwork getRelevantNetwork(){return node!=null&&node.isValid()?node.network():null;}
    @Override public boolean isAvailableToCache(StackCache cache){if(!isAvailable())return false;int range=PneumaticNetwork.getRangeFromPressure(compair.getPressure()),x=worldPosition.getX()-cache.x,y=worldPosition.getY()-cache.y,z=worldPosition.getZ()-cache.z;return x*x+y*y+z*z<=range*range;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{compair};}
    @Override public FluidTank[] getReceivingTanks(){return getAllTanks();}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return type==HbmFluids.AIR;}
    @Override public boolean isLoaded(){return level!=null&&!isRemoved();}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remaining=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(remaining!=amount)setChanged();return remaining;}
    @Override public List<TankView> tanks(){return List.of(new TankView(compair.getTankType(),compair.getFill(),compair.getMaxFill()));}
    @Override protected void saveFields(CompoundTag tag){super.saveFields(tag);compair.writeToNBT(tag,"tank");}
    @Override protected void loadFields(CompoundTag tag){super.loadFields(tag);compair.readFromNBT(tag,"tank");}
}
