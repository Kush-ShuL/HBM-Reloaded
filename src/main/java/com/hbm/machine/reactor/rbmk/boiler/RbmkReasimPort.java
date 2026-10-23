package com.hbm.machine.reactor.rbmk.boiler;
import com.hbm.api.machine.MachineView;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original inlet/outlet32k buffer, six private network faces and source N/S/W/E column transfer order. */
public final class RbmkReasimPort extends BlockEntity implements StandardFluidTransceiver,MachineView,net.minecraft.world.WorldlyContainer,com.hbm.energy.EnergyReceiver,com.hbm.energy.EnergyConductor,com.hbm.heat.HeatSource,com.hbm.machine.foundry.FoundryAcceptor,com.hbm.api.computer.ComputerControl,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public final FluidTank tank;private FluidPorts ports;
    public RbmkReasimPort(BlockPos pos,BlockState state){super(RbmkBoilerContent.REASIM_PORT.get(),pos,state);tank=new FluidTank(((RbmkReasimPortBlock)state.getBlock()).output?HbmFluids.SUPERHOTSTEAM:HbmFluids.WATER,32000);}
    public boolean output(){return ((RbmkReasimPortBlock)getBlockState().getBlock()).output;}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkReasimPort machine){if(level instanceof ServerLevel world)machine.serverTick(world);}
    private void serverTick(ServerLevel world){if(ports==null)ports=new FluidPorts(worldPosition);ports.update(world,this);if(RbmkDials.getReasimBoilers(world))for(var direction:new Direction[]{Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST}){var pos=worldPosition.relative(direction);if(!(world.getBlockState(pos).getBlock() instanceof RbmkColumnBlock block))continue;var column=block.core(world,pos);if(column==null)continue;if(output()){int provided=Math.min(tank.getMaxFill()-tank.getFill(),column.reasimSteam);column.reasimSteam-=provided;tank.setFill(tank.getFill()+provided);}else{int provided=Math.min(16000-column.reasimWater,tank.getFill());column.reasimWater+=provided;tank.setFill(tank.getFill()-provided);}column.setChanged();}setChanged();}
    private void disconnect(){if(level instanceof ServerLevel world&&ports!=null)ports.remove(world);ports=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();}return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(HbmFluidType type,Direction side){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(type,side);}return side!=null&&type==tank.getTankType();}
    @Override public FluidTank[] getAllTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getAllTanks();}return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingTanks();}return output()?FluidTank.EMPTY_ARRAY:new FluidTank[]{tank};}@Override public FluidTank[] getSendingTanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getSendingTanks();}return output()?new FluidTank[]{tank}:FluidTank.EMPTY_ARRAY;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.transferFluid(type,pressure,amount);}long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){binding.useUpFluid(type,pressure,amount);return;}StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tank.writeToNBT(tag,"tank");com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);tank.readFromNBT(tag,"tank");com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}@Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tank.writeToNBT(tag,"tank");return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){tank.readFromNBT(tag,"tank");}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public long energyStored(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyStored();}return 0;}@Override public long energyCapacity(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyCapacity();}return 0;}@Override public List<TankView> tanks(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.tanks();}return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}

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

 @Override public boolean canConnect(net.minecraft.core.Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canConnect(side);}

 @Override public com.hbm.energy.PowerNode energyNode(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).energyNode();}

 @Override public long getDemand(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getDemand(type,pressure);}return StandardFluidTransceiver.super.getDemand(type,pressure);}

 @Override public int[] getReceivingPressureRange(com.hbm.fluid.HbmFluidType type){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceivingPressureRange(type);}return StandardFluidTransceiver.super.getReceivingPressureRange(type);}

 @Override public long getReceiverSpeed(com.hbm.fluid.HbmFluidType type,int pressure){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.getReceiverSpeed(type,pressure);}return StandardFluidTransceiver.super.getReceiverSpeed(type,pressure);}

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

 @Override public java.util.List<com.hbm.api.machine.MachineView.ProgressView> progressBars(){var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.progressBars();}return MachineView.super.progressBars();}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
