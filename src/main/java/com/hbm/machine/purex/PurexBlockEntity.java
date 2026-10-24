package com.hbm.machine.purex;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.multiblock.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 13-slot PUREX with three input tanks and one output tank and native HE only. */
public final class PurexBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,MachineView,api.hbm.redstoneoverradio.IRORValueProvider, com.hbm.multiblock.MuffledMachine {
    public static Consumer<PurexBlockEntity> CLIENT_TICK=machine->{}, CLIENT_REMOVED=machine->{};
    private final ItemStack[] slots=new ItemStack[13];
    public final FluidTank[] inputTanks={new FluidTank(HbmFluids.NONE,24000),new FluidTank(HbmFluids.NONE,24000),new FluidTank(HbmFluids.NONE,24000)};
    public final FluidTank[] outputTanks={new FluidTank(HbmFluids.NONE,24000)};
    private long power,maxPower=1000000;
    private boolean didProcess;
    public boolean muffled,tilted;
    private Component customName;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
    private FluidPorts fluidPorts;
    private List<BlockPos> currentPorts=List.of();
    public final GenericMachineProcessor module=new GenericMachineProcessor(this,4,3,7,6,inputTanks,outputTanks,this,this::recipes);
    public PurexBlockEntity(BlockPos pos,BlockState state) { super(PurexContent.ENTITY.get(),pos,state); Arrays.fill(slots,ItemStack.EMPTY); }
    @Override public boolean isValidBlockState(BlockState state) { return super.isValidBlockState(state)&&DummyableBlock.isCore(state); }
    public List<PurexRecipe> recipes() { return level==null?List.of():PurexRecipeConfig.recipes(level.getRecipeManager()); }
    public boolean processing() { return didProcess; }
    public float volume(float original) { return muffled?original*.1F:original; }
    @Override public void setCustomName(Component name) { customName=name; setChanged(); }
    @Override public Component getDisplayName() { return customName==null?Component.translatable("container.machinePUREX"):customName; }
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player) { return new PurexMenu(id,inventory,this,menuData,worldPosition); }
    public void selectRecipe(int index,String selection) { if(index==0){module.setRecipe(selection,false);setChanged();} }
    /** Original ROR callbacks; no OC component or invented CC interface exists on this machine. */
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"progress",PREFIX_VALUE+"recipe",PREFIX_VALUE+"active"};}
    @Override public String provideRORValue(String name){return name.startsWith(PREFIX_VALUE)?readIrOr(name.substring(PREFIX_VALUE.length())):null;}
    public String readIrOr(String name) { return switch(name) { case "progress"->Integer.toString((int)Math.round(module.progress*100));case "recipe"->module.recipeName;case "active"->didProcess?"1":"0";default->null; }; }
    public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.offset(2,0,-1),core.offset(2,0,0),core.offset(2,0,1),core.offset(2,0,2),core.offset(1,0,2),core.offset(0,0,2),core.offset(-1,0,2),core.offset(-2,0,2),core.offset(-2,0,1),core.offset(-2,0,0),core.offset(-2,0,-1),core.offset(-2,0,-2),core.offset(-1,0,-2),core.offset(0,0,-2),core.offset(1,0,-2),core.offset(2,0,-2)};}
    public static Direction[] portSides(BlockPos core,BlockPos port) {
        var sides=new ArrayList<Direction>();
        if(port.getX()==core.getX()-2)sides.add(Direction.WEST);
        if(port.getX()==core.getX()+2)sides.add(Direction.EAST);
        if(port.getZ()==core.getZ()-2)sides.add(Direction.NORTH);
        if(port.getZ()==core.getZ()+2)sides.add(Direction.SOUTH);
        return sides.toArray(Direction[]::new);
    }
    public static void tick(Level level,BlockPos pos,BlockState state,PurexBlockEntity machine) {
        if(machine.maxPower<=0)machine.maxPower=1000000;
        if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);
    }
    private void serverTick(ServerLevel server) {
        updatePorts(server);
        var recipe=module.getRecipe();
        if(recipe!=null)maxPower=recipe.power()*100L;
        maxPower=Math.max(Math.max(power,maxPower),1000000L);
        chargeBattery();
        int speed=0,saving=0,overdrive=0;
        for(int slot=2;slot<=3;slot++) {
            var item=slots[slot].getItem();
            if(item instanceof FurnaceUpgradeItem upgrade) {
                if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED)speed+=upgrade.tier();else saving+=upgrade.tier();
            } else if(item instanceof OverdriveUpgradeItem upgrade)overdrive+=upgrade.tier();
        }
        speed=Math.min(speed,3);saving=Math.min(saving,3);overdrive=Math.min(overdrive,3);
        module.update(1D+speed/3D+overdrive,1D-saving*.25D+speed+overdrive*10D/3D,true,slots[1]);
        didProcess=module.didProcess;
        if(module.markDirty)setChanged();
        var packet=getUpdatePacket();
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);dispatchScope.commit();}
    }
    private void chargeBattery() {
        if(power<0){power=0;return;}if(power>maxPower){power=maxPower;return;}
        if(slots[0].getItem() instanceof CreativeBatteryItem){power=maxPower;return;}
        if(slots[0].getItem() instanceof HbmBattery battery) {
            long charge=Math.min(Math.min(maxPower-power,battery.getDischargeRate(slots[0])),battery.getCharge(slots[0]));
            battery.dischargeBattery(slots[0],charge);power+=charge;if(charge!=0)setChanged();
        }
    }
    private void updatePorts(ServerLevel server) {
        var loaded=new ArrayList<BlockPos>();
        for(var port:portPositions(worldPosition))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);
        if(!currentPorts.equals(loaded)||nodes.values().stream().anyMatch(PowerNode::isExpired)) {
            disconnect();currentPorts=List.copyOf(loaded);var fluidConnections=new ArrayList<NodeConnection>();
            for(var port:loaded) {
                var connections=Arrays.stream(portSides(worldPosition,port)).map(side->new NodeConnection(port.relative(side),side)).toArray(NodeConnection[]::new);
                var node=new PowerNode(port).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);fluidConnections.addAll(List.of(connections));
            }
            if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),fluidConnections.toArray(NodeConnection[]::new));
        }
        // Source manyToMany: sixteen independent HE nodes, no power passthrough.
        if(fluidPorts!=null)fluidPorts.update(server,this);
        for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
    }
    private void disconnect() {
        if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}
        nodes.clear();fluidPorts=null;currentPorts=List.of();
    }
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long getPower(){return power;}@Override public long getMaxPower(){return maxPower;}@Override public void setPower(long value){power=value;setChanged();}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
    @Override public FluidTank[] getReceivingTanks(){return inputTanks;}@Override public FluidTank[] getSendingTanks(){return outputTanks;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{inputTanks[0],inputTanks[1],inputTanks[2],outputTanks[0]};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public java.util.List<ProgressView> progressBars() { if (!(module.progress > 0 || didProcess)) return java.util.List.of(); return java.util.List.of(new ProgressView("jade.hbm.processing",module.progress,1,0x71C78B)); }
    @Override public long energyStored(){return power;}@Override public long energyCapacity(){return maxPower;}
    @Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}
    private void saveState(CompoundTag tag){for(int i=0;i<3;i++)inputTanks[i].writeToNBT(tag,"i"+i);outputTanks[0].writeToNBT(tag,"o0");tag.putLong("power",power);tag.putLong("maxPower",maxPower);module.writeToNBT(tag);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    private void loadState(CompoundTag tag){for(int i=0;i<3;i++)inputTanks[i].readFromNBT(tag,"i"+i);outputTanks[0].readFromNBT(tag,"o0");power=tag.getLong("power");maxPower=tag.getLong("maxPower");module.readFromNBT(tag);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=NonNullList.withSize(13,ItemStack.EMPTY);for(int i=0;i<13;i++)list.set(i,slots[i]);ContainerHelper.saveAllItems(tag,list,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var list=NonNullList.withSize(13,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,list,lookup);for(int i=0;i<13;i++)slots[i]=list.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);tag.putBoolean("didProcess",didProcess);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag);didProcess=tag.getBoolean("didProcess");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return slots.length;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}
    @Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0||slot==1&&stack.is(Blueprints.ITEM.get())||(slot==2||slot==3)&&PurexMenu.upgrade(stack)||module.isItemValid(slot,stack);}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{4,5,6,7,8,9,10,11,12};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=7&&slot<=12||module.isSlotClogged(slot);}
    private final ContainerData menuData=new ContainerData(){public int getCount(){return 14;}public int get(int index){long value=index<4?power:index<8?maxPower:Double.doubleToRawLongBits(module.progress);if(index<12)return(int)(value>>>((index%4)*16))&65535;return index==12?(didProcess?1:0):(module.restrictedMode?1:0);}public void set(int index,int value){}};

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
