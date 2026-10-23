package com.hbm.machine.rockmill;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.recipe.generic.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
/** Real source eight-slot stone mill, native independent HE ports and combined private-fluid mouths. */
public final class RockMillEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver {
 public static Consumer<RockMillEntity> CLIENT_TICK=ignored->{};
 public final FluidTank[] inputTanks={new FluidTank(HbmFluids.NONE,4000)},outputTanks={new FluidTank(HbmFluids.NONE,4000)};
 public final RockMillProcessor module=new RockMillProcessor(this,inputTanks[0],outputTanks[0],this,this::recipes);
 public BigInteger power=BigInteger.ZERO,maxPower=BigInteger.valueOf(2500);public boolean didProcess,tilted;
 public float rotation,prevRotation,rotationSpeed;public boolean frame;
 private final Map<BlockPos,PowerNode> powerNodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<Port> activePorts=List.of();private CompoundTag lastSent;
 public RockMillEntity(BlockPos pos,BlockState state){super(RockMillContent.ENTITY.get(),pos,state,8);}
 @Override protected String title(){return "container.machineRockMill";}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RockMillMenu(id,inventory,this,worldPosition);}
 @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
 public List<RockMillRecipe> recipes(){return level==null?List.of():RockMillRecipeConfig.recipes(level.getRecipeManager());}
 public record Port(BlockPos position,Direction side){public NodeConnection connection(){return new NodeConnection(position.relative(side),side);}}
 /** Source getPorts order is fixed in world coordinates; the eight-cell set is rotationally symmetric. */
 public static List<Port> portDefinitions(BlockPos p){return List.of(new Port(p.offset(2,0,1),Direction.EAST),new Port(p.offset(2,0,-1),Direction.EAST),new Port(p.offset(-2,0,1),Direction.WEST),new Port(p.offset(-2,0,-1),Direction.WEST),new Port(p.offset(1,0,2),Direction.SOUTH),new Port(p.offset(-1,0,2),Direction.SOUTH),new Port(p.offset(1,0,-2),Direction.NORTH),new Port(p.offset(-1,0,-2),Direction.NORTH));}
 private void updatePorts(ServerLevel server){var current=portDefinitions(worldPosition).stream().filter(p->server.hasChunkAt(p.position)&&server.getBlockState(p.position).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(p.position))).toList();if(!current.equals(activePorts)||powerNodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();activePorts=current;for(var port:current){var node=new PowerNode(port.position).setConnections(port.connection());HbmEnergy.nodespace(server).createNode(node);powerNodes.put(port.position,node);}if(!current.isEmpty())fluidPorts=new FluidPorts(current.stream().map(Port::position).toArray(BlockPos[]::new),current.stream().map(Port::connection).toArray(NodeConnection[]::new));}if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:powerNodes.values())if(node.hasValidNet())node.addReceiver(this);}
 public static void tick(Level level,BlockPos pos,BlockState state,RockMillEntity mill){if(mill.maxPower.signum()<=0)mill.maxPower=BigInteger.valueOf(2500);if(level instanceof ServerLevel server)mill.serverTick(server);else CLIENT_TICK.accept(mill);}
 private void serverTick(ServerLevel server){updatePorts(server);var recipe=module.getRecipe();if(recipe!=null)maxPower=BigInteger.valueOf(recipe.power()).multiply(BigInteger.valueOf(100));maxPower=maxPower.max(power).max(BigInteger.valueOf(2500));chargeBattery();module.update(1,1,true,slots.get(1));didProcess=module.didProcess;if(module.markDirty)setChanged();int identity=(worldPosition.getY()+worldPosition.getZ()*27644437)*27644437+worldPosition.getX();if(didProcess&&(server.getGameTime()+identity)%3==0){var state=Blocks.STONE.defaultBlockState();if(recipe!=null&&recipe.icon().getItem() instanceof BlockItem block)state=block.getBlock().defaultBlockState();var sound=state.getSoundType(server,worldPosition,null).getStepSound();server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,sound,net.minecraft.sounds.SoundSource.BLOCKS,isMuffled()?.1F:1F,.75F);}var tag=getUpdateTag(server.registryAccess());if(!tag.equals(lastSent)||server.getGameTime()%20==0){lastSent=tag;sync(100);}}
 private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(maxPower)>0){power=maxPower;return;}var stack=slots.getFirst();if(stack.getItem() instanceof CreativeBatteryItem){power=maxPower;return;}if(stack.getItem() instanceof HbmBattery battery){long amount=maxPower.subtract(power).min(BigInteger.valueOf(battery.getDischargeRate(stack))).min(BigInteger.valueOf(battery.getCharge(stack))).longValueExact();battery.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));if(amount!=0)setChanged();}}
 public void selectRecipe(int index,String selection){if(index==0){module.setRecipe(selection,false);setChanged();}}
 public void receiveControl(Player player,CompoundTag data){if(stillValid(player)&&data.contains("index")&&data.contains("selection"))selectRecipe(data.getInt("index"),data.getString("selection"));}
 @Override public boolean canPlaceItem(int index,ItemStack stack){if(index==0)return stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem;if(index==1&&stack.is(Blueprints.ITEM.get()))return true;return module.isItemValid(index,stack);}
 @Override public int[] getSlotsForFace(Direction side){return new int[]{2,3,4,5,6,7};}@Override public boolean canTakeItemThroughFace(int index,ItemStack stack,Direction side){return index>=5&&index<=7||module.isSlotClogged(index);}
 private static long legacy(BigInteger n){return n.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
 @Override public long getPower(){return legacy(power);}@Override public long getMaxPower(){return legacy(maxPower);}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return maxPower;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}@Override public BigInteger getReceiverSpeedExact(){return maxPower;}
 @Override public long transferPower(long amount){return transferPowerExact(BigInteger.valueOf(amount)).longValueExact();}@Override public BigInteger transferPowerExact(BigInteger amount){if(amount.signum()<0)throw new IllegalArgumentException("Negative HE transfer");var room=maxPower.subtract(power);if(room.signum()<0)throw new IllegalStateException("Invalid mill HE storage");var accepted=room.min(amount);power=power.add(accepted);if(accepted.signum()!=0)setChanged();return amount.subtract(accepted);}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
 @Override public FluidTank[] getReceivingTanks(){return inputTanks;}@Override public FluidTank[] getSendingTanks(){return outputTanks;}@Override public FluidTank[] getAllTanks(){return new FluidTank[]{inputTanks[0],outputTanks[0]};}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rest=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rest!=amount)setChanged();return rest;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}@Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public List<ProgressView> progressBars(){return module.getRecipe()==null?List.of():List.of(new ProgressView("gui.hbm.rockmill.progress",module.progress,1,0x71c78b));}
 @Override protected void saveMachine(CompoundTag tag){inputTanks[0].writeToNBT(tag,"i0");outputTanks[0].writeToNBT(tag,"o0");tag.putString("power",power.toString());tag.putString("maxPower",maxPower.toString());tag.putBoolean("tilted",tilted);module.writeToNBT(tag);}
 @Override protected void loadMachine(CompoundTag tag){inputTanks[0].readFromNBT(tag,"i0");outputTanks[0].readFromNBT(tag,"o0");power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));maxPower=tag.contains("maxPower",8)?new BigInteger(tag.getString("maxPower")):BigInteger.valueOf(tag.getLong("maxPower"));tilted=tag.getBoolean("tilted");module.readFromNBT(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("didProcess",didProcess);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);didProcess=tag.getBoolean("didProcess");}
 private void disconnect(){if(level instanceof ServerLevel server){for(var node:powerNodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}powerNodes.clear();fluidPorts=null;activePorts=List.of();}
 @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
}
