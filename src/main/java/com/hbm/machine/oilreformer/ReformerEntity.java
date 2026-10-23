package com.hbm.machine.oilreformer;

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

/** X5846 catalytic reformer:11real slots,100mB+20000HE per tick,unconsumed catalytic converter and3private outputs. */
public final class ReformerEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,MachineView,IFluidCopiable, com.hbm.multiblock.MuffledMachine {
    public static final BigInteger CAPACITY=BigInteger.valueOf(1000000);
    private BigInteger power=BigInteger.ZERO;
    private final ItemStack[] slots=new ItemStack[11];
    public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("NAPHTHA"),64000),new FluidTank(HbmFluids.fromName("REFORMATE"),24000),new FluidTank(HbmFluids.fromName("PETROLEUM"),24000),new FluidTank(HbmFluids.fromName("HYDROGEN"),24000)};
    public boolean isOn,muffled;
    private Component customName;private CompoundTag lastSentState;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
    public ReformerEntity(BlockPos pos,BlockState state){super(ReformerContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.catalyticReformer"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ReformerMenu(id,inventory,this,menuData,worldPosition);}
    public static NodeConnection[] connections(BlockPos core,Direction dir){var right=dir.getClockWise();return new NodeConnection[]{new NodeConnection(core.relative(dir).relative(right).relative(dir),dir),new NodeConnection(core.relative(dir).relative(right,-1).relative(dir),dir),new NodeConnection(core.relative(dir,-1).relative(right).relative(dir,-1),dir.getOpposite()),new NodeConnection(core.relative(dir,-1).relative(right,-1).relative(dir,-1),dir.getOpposite()),new NodeConnection(core.relative(right,3),right),new NodeConnection(core.relative(right,-3),right.getOpposite())};}
    public static BlockPos[] portPositions(BlockPos core,Direction dir){return Arrays.stream(connections(core,dir)).map(NodeConnection::origin).toArray(BlockPos[]::new);}
    public static Direction[] portSides(BlockPos core,Direction dir,BlockPos port){var positions=portPositions(core,dir);var connections=connections(core,dir);for(int i=0;i<positions.length;i++)if(positions[i].equals(port))return new Direction[]{connections[i].direction()};return new Direction[0];}
    public static void tick(Level level,BlockPos pos,BlockState state,ReformerEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    private void serverTick(ServerLevel server){
        updatePorts(server);isOn=false;chargeBattery();boolean changed=tanks[0].setType(9,slots);changed|=tanks[0].loadTank(1,2,slots);changed|=refine(server);for(int tank=1;tank<4;tank++)changed|=tanks[tank].unloadTank(1+tank*2,2+tank*2,slots);if(changed)setChanged();
        var packetState=getUpdateTag(server.registryAccess());if(!packetState.equals(lastSentState)||server.getGameTime()%20==0){lastSentState=packetState;var packet=ClientboundBlockEntityDataPacket.create(this,(ignored,lookup)->packetState);for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);}
    }
    private boolean refine(ServerLevel server){
        var recipe=ReformerRecipeConfig.find(server.getRecipeManager(),tanks[0].getTankType());
        if(recipe==null){for(int i=1;i<4;i++)tanks[i].setTankType(HbmFluids.NONE);return false;}
        var outputs=recipe.data().outputFluids();for(int i=0;i<3;i++)tanks[i+1].setTankType(outputs.get(i).type);
        if(power.compareTo(BigInteger.valueOf(20000))<0||tanks[0].getFill()<100||!slots[10].is(com.hbm.registry.HbmContent.item("catalytic_converter")))return false;
        for(int i=0;i<3;i++)if(tanks[i+1].getFill()+outputs.get(i).fill>tanks[i+1].getMaxFill())return false;
        isOn=true;tanks[0].setFill(tanks[0].getFill()-100);for(int i=0;i<3;i++)tanks[i+1].setFill(tanks[i+1].getFill()+outputs.get(i).fill);
        power=power.subtract(BigInteger.valueOf(20000));return true;
    }
    private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(CAPACITY)>0){power=CAPACITY;return;}if(slots[0].getItem() instanceof CreativeBatteryItem){power=CAPACITY;return;}if(slots[0].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(CAPACITY.subtract(power).longValueExact(),battery.getDischargeRate(slots[0])),battery.getCharge(slots[0]));battery.dischargeBattery(slots[0],amount);power=power.add(BigInteger.valueOf(amount));if(amount>0)setChanged();}}
    private void updatePorts(ServerLevel server){
        var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition,DummyableBlock.facing(getBlockState())))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);
        if(!loaded.equals(currentPorts)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var fluidConnections=new ArrayList<NodeConnection>();for(var port:loaded){var connections=Arrays.stream(portSides(worldPosition,DummyableBlock.facing(getBlockState()),port)).map(side->new NodeConnection(port.relative(side),side)).toArray(NodeConnection[]::new);var node=new PowerNode(port).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);fluidConnections.addAll(List.of(connections));}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),fluidConnections.toArray(NodeConnection[]::new));}
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
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1],tanks[2],tanks[3]};}@Override public FluidTank[] getAllTanks(){return tanks;}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return 1000000000L;}@Override public long getReceiverSpeed(HbmFluidType type,int pressure){return 1000000000L;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 1000000;}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return List.of();}
    private void saveState(CompoundTag tag){tag.putString("power",power.toString());String[] names={"input","o1","o2","o3"};for(int i=0;i<4;i++)tanks[i].writeToNBT(tag,names[i]);tag.putBoolean("muffled",muffled);}
    private void loadState(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));String[] names={"input","o1","o2","o3"};for(int i=0;i<4;i++)tanks[i].readFromNBT(tag,names[i]);muffled=tag.getBoolean("muffled");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var inventory=NonNullList.withSize(11,ItemStack.EMPTY);for(int i=0;i<11;i++)inventory.set(i,slots[i]);ContainerHelper.saveAllItems(tag,inventory,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var inventory=NonNullList.withSize(11,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,inventory,lookup);for(int i=0;i<11;i++)slots[i]=inventory.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);tag.putBoolean("isOn",isOn);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag);isOn=tag.getBoolean("isOn");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    public ItemStack dropStack(){var result=new ItemStack(getBlockState().getBlock());if(Arrays.stream(tanks).anyMatch(tank->tank.getFill()>0)){var data=new CompoundTag();for(int i=0;i<4;i++)tanks[i].writeToNBT(data,""+i);var root=new CompoundTag();root.put("persistent",data);result.set(DataComponents.CUSTOM_DATA,CustomData.of(root));}if(customName!=null)result.set(DataComponents.CUSTOM_NAME,customName);return result;}
    public void restoreItem(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null||!data.copyTag().contains("persistent"))return;var tag=data.copyTag().getCompound("persistent");for(int i=0;i<4;i++)tanks[i].readFromNBT(tag,""+i);setChanged();}
    @Override public int getContainerSize(){return 11;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;stack.limitSize(getMaxStackSize());setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[]{};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    private final ContainerData menuData=new ContainerData(){public int getCount(){return 4;}public int get(int index){return(int)(getPower()>>>(index*16))&65535;}public void set(int index,int value){}};

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
