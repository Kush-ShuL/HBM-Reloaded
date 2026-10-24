package com.hbm.machine.barrel;

import com.hbm.api.computer.*;
import com.hbm.api.machine.*;
import com.hbm.energy.ConnectionPriority;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.fluid.FluidTraits;
import com.hbm.saveddata.TomSaveData;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Source six-slot storage, four transfer modes and material-dependent fluid reactions. */
public class BarrelBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,StandardFluidTransceiver,IFluidCopiable,ComputerControl,MachineView,api.hbm.redstoneoverradio.IRORValueProvider,api.hbm.redstoneoverradio.IRORInteractive {
    protected final ItemStack[] slots=new ItemStack[6];
    public final FluidTank tank;
    public short mode;
    public int age;
    public byte lastRedstone;
    public boolean tilted;
    private Component customName;
    protected FluidPorts fluidPorts;
    public BarrelBlockEntity(BlockPos pos,BlockState state){this(BarrelContent.ENTITY.get(),pos,state,((BarrelBlock)state.getBlock()).variant.capacity);}
    protected BarrelBlockEntity(BlockEntityType<?> type,BlockPos pos,BlockState state,int capacity){super(type,pos,state);Arrays.fill(slots,ItemStack.EMPTY);tank=new FluidTank(HbmFluids.NONE,capacity);}
    public static void tick(Level level,BlockPos pos,BlockState state,BarrelBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    protected void serverTick(ServerLevel server){
        updatePorts(server);byte comparator=comparatorPower();if(lastRedstone!=comparator){setChanged();updateComparators(server);}lastRedstone=comparator;
        boolean changed=tank.setType(0,1,slots);changed|=tank.loadTank(2,3,slots);changed|=tank.unloadTank(4,5,slots);if(changed)setChanged();
        if(tank.getFill()>0)checkFluidInteraction(server);
        if(!isRemoved()){var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<trackingRange()*trackingRange())player.connection.send(packet);dispatchScope.commit();}}
    }
    public int trackingRange(){return 50;}
    protected void updatePorts(ServerLevel level){if(fluidPorts==null)fluidPorts=new FluidPorts(worldPosition);fluidPorts.update(level,this);}
    protected void updateComparators(ServerLevel level){for(var side:Direction.values()){level.updateNeighborsAt(worldPosition.relative(side),getBlockState().getBlock());}level.updateNeighbourForOutputSignal(worldPosition,getBlockState().getBlock());}
    protected void disconnect(){if(level instanceof ServerLevel server&&fluidPorts!=null)fluidPorts.remove(server);fluidPorts=null;}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    public byte comparatorPower(){return tank.getFill()==0?0:(byte)Mth.clamp((int)((double)tank.getFill()/tank.getMaxFill()*15)+1,0,15);}
    public void checkFluidInteraction(ServerLevel server){
        var variant=((BarrelBlock)getBlockState().getBlock()).variant;var type=tank.getTankType();
        if(variant!=BarrelContent.Variant.ANTIMATTER&&type.isAntimatter()){server.destroyBlock(worldPosition,false);explode(server);}
        if(variant==BarrelContent.Variant.PLASTIC&&(type.isCorrosive()||type.isHot())){server.destroyBlock(worldPosition,false);server.playSound(null,worldPosition,SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,1,1);}
        if(variant==BarrelContent.Variant.CORRODED){if(server.random.nextInt(3)==0){tank.setFill(tank.getFill()-1);type.release(server,worldPosition,tank,1,FluidTraits.Release.SPILL);setChanged();}if(server.random.nextInt(3600)==0)server.destroyBlock(worldPosition,false);}
        if(variant==BarrelContent.Variant.STEEL&&type.isHighlyCorrosive()){server.setBlock(worldPosition,BarrelContent.BARRELS.get(BarrelContent.Variant.CORRODED).get().defaultBlockState(),3);if(server.getBlockEntity(worldPosition) instanceof BarrelBlockEntity barrel){barrel.tank.setTankType(type);barrel.tank.setFill(tank.getFill());barrel.setChanged();}}
        if(type==HbmFluids.WATER&&TomSaveData.forWorld(server).fire>1e-5&&server.getBrightness(LightLayer.SKY,worldPosition)>7)explode(server);
    }
    protected void explode(ServerLevel level){level.explode(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,5,true,Level.ExplosionInteraction.BLOCK);}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return type==tank.getTankType();}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}
    @Override public FluidTank[] getReceivingTanks(){return getAllTanks();}
    @Override public FluidTank[] getSendingTanks(){return getAllTanks();}
    @Override public long getDemand(HbmFluidType type,int pressure){return !tilted&&mode!=2&&mode!=3&&tank.getPressure()==pressure&&type==tank.getTankType()?tank.getMaxFill()-tank.getFill():0;}
    @Override public long getFluidAvailable(HbmFluidType type,int pressure){return !tilted&&mode!=0&&mode!=3&&tank.getPressure()==pressure&&type==tank.getTankType()?tank.getFill():0;}
    @Override public long getReceiverSpeed(HbmFluidType type,int pressure){return mode==0||mode==1?1_000_000_000:0;}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return mode==1||mode==2?1_000_000_000:0;}
    @Override public ConnectionPriority getFluidPriority(){return mode==1?ConnectionPriority.LOW:ConnectionPriority.NORMAL;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remainder=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(remainder!=amount)setChanged();return remainder;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public int[] getFluidIDToCopy(){return new int[]{tank.getTankType().getID()};}
    @Override public FluidTank getTankToPaste(){return tank;}
    public void cycleMode(){mode=(short)((mode+1)%4);setChanged();}
    public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.barrel"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new BarrelMenu(id,inventory,this,worldPosition);}
    public ItemStack dropStack(){var result=new ItemStack(getBlockState().getBlock());var tag=new CompoundTag();writeItemData(tag);if(!tag.isEmpty())result.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));if(customName!=null)result.set(DataComponents.CUSTOM_NAME,customName);return result;}
    public void writeItemData(CompoundTag tag){if(tank.getFill()==0)return;var data=new CompoundTag();tank.writeToNBT(data,"tank");data.putShort("mode",mode);tag.put("persistent",data);}
    public void restoreItem(ItemStack stack){var custom=stack.get(DataComponents.CUSTOM_DATA);if(custom==null)return;var tag=custom.copyTag();if(!tag.contains("persistent"))return;readItemData(tag.getCompound("persistent"));setChanged();}
    protected void readItemData(CompoundTag tag){tank.readFromNBT(tag,"tank");mode=tag.getShort("mode");}
    protected void saveState(CompoundTag tag){tank.writeToNBT(tag,"tank");tag.putShort("mode",mode);tag.putBoolean("tilted",tilted);}
    protected void loadState(CompoundTag tag){tank.readFromNBT(tag,"tank");mode=tag.getShort("mode");tilted=tag.getBoolean("tilted");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var inventory=NonNullList.withSize(6,ItemStack.EMPTY);for(int i=0;i<6;i++)inventory.set(i,slots[i]);ContainerHelper.saveAllItems(tag,inventory,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var inventory=NonNullList.withSize(6,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,inventory,lookup);for(int i=0;i<6;i++)slots[i]=inventory.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return slots.length;}
    @Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots[slot];}
    @Override public ItemStack removeItem(int slot,int amount){var result=slots[slot].split(amount);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){if(slot==4){var full=FluidContainerRegistry.getFullContainer(stack,tank.getTankType());if(!full.isEmpty()&&FluidContainerRegistry.getFluidContent(full,tank.getTankType())<=tank.getMaxFill())return true;}int content=FluidContainerRegistry.getFluidContent(stack,tank.getTankType());return slot==2&&content>0&&content<=tank.getMaxFill();}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{2,3,4,5};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==3||slot==5;}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    @Override public String computerType(){return "ntm_fluid_tank";}
    @Override public String[] computerMethods(){return new String[]{"getFluidStored","getMaxStored","getTypeStored","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments arguments){return switch(method){case "getFluidStored"->new Object[]{tank.getFill()};case "getMaxStored"->new Object[]{tank.getMaxFill()};case "getTypeStored"->new Object[]{tank.getTankType().getName()};case "getInfo"->new Object[]{tank.getFill(),tank.getMaxFill(),tank.getTankType().getName()};default->throw new ComputerException("Unknown method: "+method);};}
    public String readIrOr(String name){return switch(name){case "type"->tank.getTankType().getName();case "fill"->Integer.toString(tank.getFill());case "fillpercent"->Integer.toString(tank.getFill()*100/tank.getMaxFill());default->null;};}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"type",PREFIX_VALUE+"fill",PREFIX_VALUE+"fillpercent",PREFIX_FUNCTION+"setmode"+NAME_SEPARATOR+"mode (0-3)",PREFIX_FUNCTION+"setmode"+NAME_SEPARATOR+"mode"+PARAM_SEPARATOR+"fallback (0-3)"};}
    @Override public String provideRORValue(String name){return name!=null&&name.startsWith(PREFIX_VALUE)?readIrOr(name.substring(PREFIX_VALUE.length())):null;}
    @Override public String runRORFunction(String name,String[] params){if((PREFIX_FUNCTION+"setmode").equals(name)&&params.length>0){int selected=api.hbm.redstoneoverradio.IRORInteractive.parseInt(params[0],0,3);if(selected!=mode){mode=(short)selected;setChanged();}else if(params.length>1){mode=(short)api.hbm.redstoneoverradio.IRORInteractive.parseInt(params[1],0,3);setChanged();}}return null;}
    private static int parseMode(String value){int result;try{result=Integer.parseInt(value);}catch(NumberFormatException integer){try{result=(int)Math.round(Double.parseDouble(value));}catch(NumberFormatException decimal){throw new IllegalArgumentException("Exception: Parameter in Invalid Format");}}if(result<0||result>3)throw new IllegalArgumentException("Exception: Parameter in Invalid Format");return result;}
    public String invokeIrOr(String name,String[] params){if(name.equals("setmode")&&params.length>0){int selected=parseMode(params[0]);if(selected!=mode){mode=(short)selected;setChanged();}else if(params.length>1){mode=(short)parseMode(params[1]);setChanged();}}return null;}
}
