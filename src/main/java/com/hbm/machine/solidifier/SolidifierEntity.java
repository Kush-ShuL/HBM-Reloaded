package com.hbm.machine.solidifier;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.multiblock.*;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Actual source5 slots/24k receiving tank,60tick+250HE,ordered type-selection and deterministic fluid-to-solid completion. */
public final class SolidifierEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver,MachineView,IFluidCopiable,MuffledMachine {
 public static final BigInteger CAPACITY=BigInteger.valueOf(100000);private BigInteger power=BigInteger.ZERO;
 private final ItemStack[] slots={ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY};public final FluidTank tank=new FluidTank(HbmFluids.NONE,24000);public int progress,usage=250,processTime=60;public boolean processing,muffled;private Component customName;
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
 public SolidifierEntity(BlockPos pos,BlockState state){super(SolidifierContent.ENTITY.get(),pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 @Override public void setCustomName(Component name){customName=name;setChanged();}@Override public Component getDisplayName(){return customName==null?Component.translatable("container.machineSolidifier"):customName;}@Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new SolidifierMenu(id,inv,this,worldPosition);}
 @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
 public static NodeConnection[] connections(BlockPos core){return new NodeConnection[]{new NodeConnection(core.above(4),Direction.UP),new NodeConnection(core.below(),Direction.DOWN),new NodeConnection(core.offset(2,1,0),Direction.EAST),new NodeConnection(core.offset(-2,1,0),Direction.WEST),new NodeConnection(core.offset(0,1,2),Direction.SOUTH),new NodeConnection(core.offset(0,1,-2),Direction.NORTH)};}
 public static BlockPos[] portPositions(BlockPos core){return Arrays.stream(connections(core)).map(NodeConnection::origin).toArray(BlockPos[]::new);}
 public static void tick(Level level,BlockPos pos,BlockState state,SolidifierEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
 private void serverTick(ServerLevel server){updatePorts(server);chargeBattery();tank.setType(4,slots);int speed=0,efficiency=0;for(int slot=2;slot<=3;slot++)if(slots[slot].getItem() instanceof FurnaceUpgradeItem u){if(u.type()==FurnaceUpgradeItem.Type.SPEED)speed+=u.tier();else efficiency+=u.tier();}speed=Math.min(3,speed);efficiency=Math.min(3,efficiency);processTime=60-15*speed;usage=(250+250*speed)/(efficiency+1);
  var recipe=SolidificationRecipes.find(server.getRecipeManager(),tank.getTankType());
  processing=canProcess(recipe);
  if(processing){power=power.subtract(BigInteger.valueOf(usage));if(++progress>=processTime){tank.setFill(tank.getFill()-recipe.input().fill);if(slots[0].isEmpty())slots[0]=recipe.output().copy();else slots[0].grow(recipe.output().getCount());progress=0;}setChanged();}else if(progress!=0){progress=0;setChanged();}
  var packet=ClientboundBlockEntityDataPacket.create(this);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);dispatchScope.commit();}
 }
 /** Item+source damage equality deliberately ignores source output NBT,as the original canProcess does. */
 private boolean canProcess(SolidifierRecipe recipe){if(power.compareTo(BigInteger.valueOf(usage))<0||recipe==null||recipe.input().fill>tank.getFill())return false;var output=recipe.output();return slots[0].isEmpty()||slots[0].is(output.getItem())&&com.hbm.material.HbmMaterials.metadata(slots[0])==com.hbm.material.HbmMaterials.metadata(output)&&slots[0].getCount()+output.getCount()<=slots[0].getMaxStackSize();}
 private void chargeBattery(){if(slots[1].getItem() instanceof CreativeBatteryItem){if(!power.equals(CAPACITY)){power=CAPACITY;setChanged();}return;}if(slots[1].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(CAPACITY.subtract(power).longValueExact(),battery.getDischargeRate(slots[1])),battery.getCharge(slots[1]));if(amount>0){battery.dischargeBattery(slots[1],amount);power=power.add(BigInteger.valueOf(amount));setChanged();}}}
 private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition))if(server.hasChunkAt(port)){var state=server.getBlockState(port);if(state.is(getBlockState().getBlock())&&(port.equals(worldPosition)?DummyableBlock.isCore(state):DummyableBlock.isPort(state)))loaded.add(port);}
  if(!loaded.equals(currentPorts)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var connections=Arrays.stream(connections(worldPosition)).filter(c->loaded.contains(c.origin())).toArray(NodeConnection[]::new);for(var connection:connections){var node=new PowerNode(connection.origin()).setConnections(new NodeConnection[]{connection});HbmEnergy.nodespace(server).createNode(node);nodes.put(connection.origin(),node);}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),connections);}
  if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
 }
 private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
 @Override public long getPower(){return power.longValueExact();}@Override public long getMaxPower(){return 100000;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return CAPACITY;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
 @Override public FluidTank getTankToPaste(){return tank;}
 @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return getAllTanks();}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}

 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}@Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}@Override public List<ProgressView> progressBars(){return processing?List.of(new ProgressView("gui.hbm.solidifier.progress",progress,processTime,0x69bfb2)):List.of();}
 private void saveState(CompoundTag tag){tag.putString("power",power.toString());tag.putInt("progress",progress);tank.writeToNBT(tag,"tank");tag.putBoolean("muffled",muffled);}private void loadState(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));progress=tag.getInt("progress");tank.readFromNBT(tag,"tank");muffled=tag.getBoolean("muffled");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var inv=NonNullList.withSize(5,ItemStack.EMPTY);for(int i=0;i<5;i++)inv.set(i,slots[i]);ContainerHelper.saveAllItems(tag,inv,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var inv=NonNullList.withSize(5,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,inv,lookup);for(int i=0;i<5;i++)slots[i]=inv.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);tag.putInt("usage",usage);tag.putInt("processTime",processTime);tag.putBoolean("processing",processing);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag);usage=tag.getInt("usage");processTime=Math.max(1,tag.getInt("processTime"));processing=tag.getBoolean("processing");}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection c,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public int getContainerSize(){return 5;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var out=slots[slot];slots[slot]=ItemStack.EMPTY;return out;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&worldPosition.distToCenterSqr(player.position())<=144;}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==0;}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
