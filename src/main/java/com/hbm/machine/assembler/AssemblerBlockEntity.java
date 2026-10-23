package com.hbm.machine.assembler;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** X5846 AssemblyMachine: ordered12 item lanes, blueprint gate, source floating progress and upgrade formula. */
public final class AssemblerBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,MachineView, com.hbm.multiblock.MuffledMachine {
    public static Consumer<AssemblerBlockEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{},CLIENT_STOP=machine->{};
    private final ItemStack[] slots=new ItemStack[17];private final FluidTank input=new FluidTank(HbmFluids.NONE,4000),output=new FluidTank(HbmFluids.NONE,4000);
    private long power,maxPower=100000;private boolean didProcess;public boolean muffled;private Component customName;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
    public final GenericMachineProcessor module=new GenericMachineProcessor(this,4,12,16,1,new FluidTank[]{input},new FluidTank[]{output},this,()->recipes()){
        @Override public void setupTanks(GenericMachineRecipe recipe){super.setupTanks(recipe);if(recipe==null)return;if(!recipe.inputFluids().isEmpty())input.changeTankSize(Math.max(Math.max(input.getFill(),recipe.inputFluids().getFirst().fill*2),4000));if(!recipe.outputFluids().isEmpty())output.changeTankSize(Math.max(Math.max(output.getFill(),recipe.outputFluids().getFirst().fill*2),4000));}
    };
    public AssemblerBlockEntity(BlockPos pos,BlockState state){super(AssemblerContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public List<AssemblerRecipe> recipes(){return level==null?List.of():AssemblerRecipeConfig.recipes(level.getRecipeManager());}
    public boolean processing(){return didProcess;}public FluidTank inputTank(){return input;}public FluidTank outputTank(){return output;}
    public float volume(float base){return muffled?base*.1F:base;}
    @Override public void setCustomName(Component name){customName=name;setChanged();}@Override public Component getDisplayName(){return customName==null?Component.translatable("container.machineAssemblyMachine"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new AssemblerMenu(id,inventory,this,menuData,worldPosition);}
    public void selectRecipe(int index,String selection){if(index!=0)return;module.setRecipe(selection,false);setChanged();}
    /** Exact source IROR values/functions; the IROR protocol binds here when its original module is migrated. */
    public String readIrOr(String name){return switch(name){case "progress"->Long.toString(Math.round(module.progress*100));case "recipe"->module.recipeName;case "active"->didProcess?"1":"0";default->null;};}
    public Object invokeIrOr(String name,String value){if(name.equals("setrecipe")){module.setRecipe(value,true);setChanged();}return null;}
    public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.offset(-1,0,-1),core.offset(0,0,-1),core.offset(1,0,-1),core.offset(1,0,0),core.offset(1,0,1),core.offset(0,0,1),core.offset(-1,0,1),core.offset(-1,0,0)};}
    public static Direction[] portSides(BlockPos core,BlockPos pos){var directions=new ArrayList<Direction>();if(pos.getX()==core.getX()-1)directions.add(Direction.WEST);if(pos.getX()==core.getX()+1)directions.add(Direction.EAST);if(pos.getZ()==core.getZ()-1)directions.add(Direction.NORTH);if(pos.getZ()==core.getZ()+1)directions.add(Direction.SOUTH);return directions.toArray(Direction[]::new);}
    public PowerNode nodeAt(BlockPos port){return nodes.get(port);}
    public static void tick(Level level,BlockPos pos,BlockState state,AssemblerBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
    private void serverTick(ServerLevel server){if(maxPower<=0)maxPower=1000000;updatePorts(server);var recipe=module.getRecipe();if(recipe!=null)maxPower=recipe.power()*100;maxPower=Math.max(Math.max(power,maxPower),100000);chargeBattery();int speed=0,saving=0,overdrive=0;for(int slot=2;slot<=3;slot++){var item=slots[slot].getItem();if(item instanceof FurnaceUpgradeItem upgrade){if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED)speed+=upgrade.tier();else saving+=upgrade.tier();}else if(item instanceof OverdriveUpgradeItem upgrade)overdrive+=upgrade.tier();}speed=Math.min(speed,3);saving=Math.min(saving,3);overdrive=Math.min(overdrive,3);module.update(1D+speed/3D+overdrive,1D-saving*.25D+speed+overdrive*10D/3D,true,slots[1]);didProcess=module.didProcess;if(module.markDirty)setChanged();if(didProcess)AssemblerProcessingHooks.processed(this,slots);var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);}
    private void chargeBattery(){if(power<0){power=0;setChanged();return;}if(power>maxPower){power=maxPower;setChanged();return;}if(slots[0].getItem() instanceof CreativeBatteryItem){power=maxPower;setChanged();return;}if(slots[0].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(maxPower-power,battery.getDischargeRate(slots[0])),battery.getCharge(slots[0]));battery.dischargeBattery(slots[0],amount);power+=amount;if(amount!=0)setChanged();}}
    private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);if(!currentPorts.equals(loaded)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var fluidConnections=new ArrayList<NodeConnection>();for(var port:loaded){var connections=Arrays.stream(portSides(worldPosition,port)).map(side->new NodeConnection(port.relative(side),side)).toArray(NodeConnection[]::new);PowerNode node=new PowerNode(port).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);fluidConnections.addAll(List.of(connections));}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),fluidConnections.toArray(NodeConnection[]::new));}for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);if(fluidPorts!=null)fluidPorts.update(server,this);}
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}
    @Override public long getPower(){return power;}@Override public long getMaxPower(){return maxPower;}@Override public void setPower(long value){power=value;setChanged();}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}@Override public FluidTank[] getAllTanks(){return new FluidTank[]{input,output};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{input};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{output};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remaining=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(remaining!=amount)setChanged();return remaining;}
    @Override public java.util.List<ProgressView> progressBars() { if (!(module.progress > 0 || didProcess)) return java.util.List.of(); return java.util.List.of(new ProgressView("jade.hbm.processing",module.progress,1,0x71C78B)); }
    @Override public long energyStored(){return power;}@Override public long energyCapacity(){return maxPower;}@Override public List<TankView> tanks(){return List.of(new TankView(input.getTankType(),input.getFill(),input.getMaxFill()),new TankView(output.getTankType(),output.getFill(),output.getMaxFill()));}
    private void saveState(CompoundTag tag){input.writeToNBT(tag,"i");output.writeToNBT(tag,"o");tag.putLong("power",power);tag.putLong("maxPower",maxPower);tag.putDouble("progress0",module.progress);tag.putString("recipe0",module.recipeName);tag.putBoolean("restrictedMode",module.restrictedMode);tag.putBoolean("muffled",muffled);}
    private void loadState(CompoundTag tag){input.readFromNBT(tag,"i");output.readFromNBT(tag,"o");power=tag.getLong("power");maxPower=tag.getLong("maxPower");module.progress=tag.getDouble("progress0");module.recipeName=tag.contains("recipe0")?tag.getString("recipe0"):"null";module.restrictedMode=tag.getBoolean("restrictedMode");muffled=tag.getBoolean("muffled");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var items=NonNullList.withSize(17,ItemStack.EMPTY);for(int i=0;i<17;i++)items.set(i,slots[i]);ContainerHelper.saveAllItems(tag,items,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var items=NonNullList.withSize(17,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,items,lookup);for(int i=0;i<17;i++)slots[i]=items.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);tag.putBoolean("didProcess",didProcess);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){boolean was=didProcess;loadState(tag);didProcess=tag.getBoolean("didProcess");if(was&&!didProcess)CLIENT_STOP.accept(this);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 17;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}@Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0||slot==1&&stack.getItem()==Blueprints.ITEM.get()||(slot==2||slot==3)&&AssemblerMenu.upgrade(stack)||module.isItemValid(slot,stack);}@Override public int[] getSlotsForFace(Direction side){return new int[]{4,5,6,7,8,9,10,11,12,13,14,15,16};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==16||module.isSlotClogged(slot);}
    private final ContainerData menuData=new ContainerData(){@Override public int getCount(){return 14;}@Override public int get(int index){long value=index<4?power:index<8?maxPower:Double.doubleToRawLongBits(module.progress);if(index<12)return(int)(value>>>((index%4)*16))&65535;return index==12?(didProcess?1:0):(module.restrictedMode?1:0);}@Override public void set(int index,int value){}};

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
