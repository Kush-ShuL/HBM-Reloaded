package com.hbm.machine.oilhydrotreater;

import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.multiblock.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846: eleven slots, pressurized hydrogen/catalyst, one 20,000 HE operation every two world ticks. */
public final class HydrotreaterBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,MachineView,IFluidCopiable, com.hbm.multiblock.MuffledMachine {
    public static final BigInteger CAPACITY=BigInteger.valueOf(1000000);
    private BigInteger power=BigInteger.ZERO;
    private final ItemStack[] slots=new ItemStack[11];
    public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("OIL"),64000),new FluidTank(HbmFluids.fromName("HYDROGEN"),64000).withPressure(1),new FluidTank(HbmFluids.fromName("OIL_DS"),24000),new FluidTank(HbmFluids.fromName("SOURGAS"),24000)};
    public boolean muffled,tilted;
    private Component customName;private CompoundTag lastSentState;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
    public HydrotreaterBlockEntity(BlockPos pos,BlockState state){super(HydrotreaterContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.hydrotreater"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new HydrotreaterMenu(id,inventory,this,menuData,worldPosition);}
    public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.offset(-1,0,-1),core.offset(1,0,-1),core.offset(1,0,1),core.offset(-1,0,1)};}
    public static Direction[] portSides(BlockPos core,BlockPos port){return new Direction[]{port.getX()<core.getX()?Direction.WEST:Direction.EAST,port.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH};}
    public static void tick(Level level,BlockPos pos,BlockState state,HydrotreaterBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    private void serverTick(ServerLevel server){
        updatePorts(server);chargeBattery();boolean changed=tanks[0].setType(9,slots);
        changed|=tanks[0].loadTank(1,2,slots);changed|=tanks[1].loadTank(3,4,slots);
        if(server.getGameTime()%2==0)changed|=reform(server);
        changed|=tanks[2].unloadTank(5,6,slots);changed|=tanks[3].unloadTank(7,8,slots);if(changed)setChanged();
        var packetState=getUpdateTag(server.registryAccess());if(!packetState.equals(lastSentState)||server.getGameTime()%20==0){lastSentState=packetState;var packet=ClientboundBlockEntityDataPacket.create(this,(ignored,lookup)->packetState);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625)player.connection.send(packet);dispatchScope.commit();}}
    }
    public static boolean catalyst(ItemStack stack){return !stack.isEmpty()&&BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(net.minecraft.resources.ResourceLocation.parse("hbm:catalytic_converter"));}
    private boolean reform(ServerLevel server){
        var recipe=HydrotreaterRecipes.find(server.getRecipeManager(),tanks[0].getTankType());
        if(recipe==null){tanks[2].setTankType(HbmFluids.NONE);tanks[3].setTankType(HbmFluids.NONE);return false;}
        var hydrogen=recipe.inputFluids().get(1);var outputs=recipe.outputFluids();
        tanks[1].withPressure(hydrogen.pressure).setTankType(hydrogen.type);tanks[2].setTankType(outputs.get(0).type);tanks[3].setTankType(outputs.get(1).type);
        if(power.compareTo(BigInteger.valueOf(20000))<0||tanks[0].getFill()<100||tanks[1].getFill()<hydrogen.fill||!catalyst(slots[10]))return false;
        if((long)tanks[2].getFill()+outputs.get(0).fill>tanks[2].getMaxFill()||(long)tanks[3].getFill()+outputs.get(1).fill>tanks[3].getMaxFill())return false;
        tanks[0].setFill(tanks[0].getFill()-100);tanks[1].setFill(tanks[1].getFill()-hydrogen.fill);tanks[2].setFill(tanks[2].getFill()+outputs.get(0).fill);tanks[3].setFill(tanks[3].getFill()+outputs.get(1).fill);power=power.subtract(BigInteger.valueOf(20000));return true;
    }
    private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(CAPACITY)>0){power=CAPACITY;return;}if(slots[0].getItem() instanceof CreativeBatteryItem){power=CAPACITY;return;}if(slots[0].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(CAPACITY.subtract(power).longValueExact(),battery.getDischargeRate(slots[0])),battery.getCharge(slots[0]));battery.dischargeBattery(slots[0],amount);power=power.add(BigInteger.valueOf(amount));if(amount>0)setChanged();}}
    private void updatePorts(ServerLevel server){
        var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);
        if(!loaded.equals(currentPorts)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var fluidConnections=new ArrayList<NodeConnection>();for(var port:loaded){var connections=Arrays.stream(portSides(worldPosition,port)).map(side->new NodeConnection(port.relative(side),side)).toArray(NodeConnection[]::new);var node=new PowerNode(port).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);fluidConnections.addAll(List.of(connections));}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),fluidConnections.toArray(NodeConnection[]::new));}
        if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long getPower(){return power.longValueExact();}@Override public long getMaxPower(){return 1000000;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return CAPACITY;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public boolean canConnect(Direction side){return side!=null&&side!=Direction.DOWN;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side!=Direction.DOWN;}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0],tanks[1]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[2],tanks[3]};}@Override public FluidTank[] getAllTanks(){return tanks;}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return 1000000000L;}@Override public long getReceiverSpeed(HbmFluidType type,int pressure){return 1000000000L;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 1000000;}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    private void saveState(CompoundTag tag){tag.putString("power",power.toString());for(int i=0;i<4;i++)tanks[i].writeToNBT(tag,"t"+i);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    private void loadState(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));for(int i=0;i<4;i++)tanks[i].readFromNBT(tag,"t"+i);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var inventory=NonNullList.withSize(11,ItemStack.EMPTY);for(int i=0;i<11;i++)inventory.set(i,slots[i]);ContainerHelper.saveAllItems(tag,inventory,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var inventory=NonNullList.withSize(11,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,inventory,lookup);for(int i=0;i<11;i++)slots[i]=inventory.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    public ItemStack dropStack(){var result=new ItemStack(getBlockState().getBlock());if(Arrays.stream(tanks).anyMatch(tank->tank.getFill()>0)){var data=new CompoundTag();for(int i=0;i<4;i++)tanks[i].writeToNBT(data,""+i);var root=new CompoundTag();root.put("persistent",data);result.set(DataComponents.CUSTOM_DATA,CustomData.of(root));}if(customName!=null)result.set(DataComponents.CUSTOM_NAME,customName);return result;}
    public void restoreItem(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null||!data.copyTag().contains("persistent"))return;var tag=data.copyTag().getCompound("persistent");for(int i=0;i<4;i++)tanks[i].readFromNBT(tag,""+i);setChanged();}
    @Override public int getContainerSize(){return 11;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[]{};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    private final ContainerData menuData=new ContainerData(){public int getCount(){return 4;}public int get(int index){return(int)(getPower()>>>(index*16))&65535;}public void set(int index,int value){}};

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
