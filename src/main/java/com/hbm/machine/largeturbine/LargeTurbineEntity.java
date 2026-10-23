package com.hbm.machine.largeturbine;
import com.hbm.api.computer.*;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Genuine deprecated TileEntityMachineLargeTurbine: no IndustrialMk2 flywheel or Leviathan mechanics. */
public final class LargeTurbineEntity extends ThermalMachine implements EnergyProvider,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable,ComputerControl {
 public static long maxPower=100000000;public static int inputTankSize=512000,outputTankSize=10240000;public static double efficiency=1;
 public long power;public final FluidTank[] tanks={new FluidTank(HbmFluids.STEAM,inputTankSize),new FluidTank(HbmFluids.SPENTSTEAM,outputTankSize)};
 public double[] info=new double[3];public boolean operational,shouldTurn,tilted;public float rotor,lastRotor,fanAcceleration;public final float audioDesync=new Random().nextFloat()*.05F;
 public static Consumer<LargeTurbineEntity> clientTick=machine->{},clientRemoved=machine->{};
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<NodeConnection> currentPorts=List.of();private CompoundTag lastPacked;private String sourceName="";
 public LargeTurbineEntity(BlockPos pos,BlockState state){super(LargeTurbineContent.ENTITY.get(),pos,state,7);}
 @Override protected String title(){return "container.machineLargeTurbine";}@Override public void setCustomName(Component name){sourceName=name.getString();setChanged();}@Override public Component getDisplayName(){return sourceName.isEmpty()?Component.translatable(title()):Component.literal(sourceName);}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new LargeTurbineMenu(id,inventory,this,worldPosition);}@Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
 public static List<NodeConnection> connections(BlockPos core,Direction dir){var rot=dir.getClockWise();return List.of(new NodeConnection(core.relative(rot,2),rot),new NodeConnection(core.relative(rot,-2),rot.getOpposite()),new NodeConnection(core.relative(dir,2),dir));}
 public static List<BlockPos> extras(BlockPos core,Direction dir){var out=new ArrayList<BlockPos>();for(var connection:connections(core,dir))out.add(connection.origin());out.add(core.relative(dir,-3));return out;}
 public static void tick(Level level,BlockPos pos,BlockState state,LargeTurbineEntity turbine){if(level instanceof ServerLevel server)turbine.serverTick(server);else turbine.tickClient();}
 private ItemStack[] inventoryArray(){return slots.toArray(ItemStack[]::new);}private void storeArray(ItemStack[] values){for(int i=0;i<7;i++)slots.set(i,values[i]);}
 private void chargeItem(ItemStack[] items){if(power<0){power=0;return;}if(power>maxPower){power=maxPower;return;}var stack=items[4];if(stack.getItem() instanceof CreativeBatteryItem){power=0;return;}if(stack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(power,battery.getChargeRate(stack)),battery.getMaxCharge(stack)-battery.getCharge(stack));power-=amount;battery.chargeBattery(stack,amount);}}
 private void serverTick(ServerLevel server){
  updatePorts(server);info=new double[3];var items=inventoryArray();tanks[0].setType(0,1,items);tanks[0].loadTank(2,3,items);chargeItem(items);
  power*=.95; // Source compound double→long truncation, after item charging and before conversion.
  boolean valid=false;var trait=tanks[0].getTankType().getTrait(FluidTraits.Coolable.class);
  if(trait!=null){double eff=trait.getEfficiency(FluidTraits.CoolingType.TURBINE)*efficiency;if(eff>0){
   tanks[1].setTankType(trait.coolsTo());int inputOps=(int)Math.floor(tanks[0].getFill()/trait.amountReq());int outputOps=(tanks[1].getMaxFill()-tanks[1].getFill())/trait.amountProduced();int cap=(int)Math.ceil(tanks[0].getFill()/trait.amountReq()/5F);int ops=Math.min(inputOps,Math.min(outputOps,cap));
   tanks[0].setFill(tanks[0].getFill()-ops*trait.amountReq());tanks[1].setFill(tanks[1].getFill()+ops*trait.amountProduced());
   power+=ops*trait.heatEnergy()*eff; // Preserve original int product before multiplying by double efficiency.
   info[0]=ops*trait.amountReq();info[1]=ops*trait.amountProduced();info[2]=ops*trait.heatEnergy()*eff;valid=true;operational=ops>0;
  }}
  if(!valid)tanks[1].setTankType(HbmFluids.NONE); // Source deliberately leaves the previous operational flag unchanged here.
  if(power>maxPower)power=maxPower;tanks[1].unloadTank(5,6,items);storeArray(items);
  var packed=getUpdateTag(server.registryAccess());if(!packed.equals(lastPacked)||server.getGameTime()%20==0){lastPacked=packed;sync(50);}
 }
 private void tickClient(){lastRotor=rotor;rotor+=fanAcceleration;if(rotor>=360){rotor-=360;lastRotor-=360;}if(shouldTurn)fanAcceleration=Math.max(0,Math.min(15,fanAcceleration+=.075F+audioDesync));else fanAcceleration=Math.max(0,Math.min(15,fanAcceleration-=.1F));clientTick.accept(this);}
 private void updatePorts(ServerLevel server){var loaded=new ArrayList<NodeConnection>();for(var connection:connections(worldPosition,DummyableBlock.facing(getBlockState()))){var p=connection.origin();if(server.hasChunkAt(p)){var s=server.getBlockState(p);if(s.is(getBlockState().getBlock())&&DummyableBlock.isPort(s))loaded.add(connection);}}
  if(!loaded.equals(currentPorts)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnectNetworks();currentPorts=List.copyOf(loaded);for(var connection:loaded){var node=new PowerNode(connection.origin()).setConnections(connection);HbmEnergy.nodespace(server).createNode(node);nodes.put(connection.origin(),node);}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.stream().map(NodeConnection::origin).toArray(BlockPos[]::new),loaded.toArray(NodeConnection[]::new));}
  if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addProvider(this);
 }
 private void disconnectNetworks(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}
 private void removedClient(){if(level!=null&&level.isClientSide)clientRemoved.accept(this);}@Override public void setRemoved(){disconnectNetworks();removedClient();super.setRemoved();}@Override public void onChunkUnloaded(){disconnectNetworks();removedClient();super.onChunkUnloaded();}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
 @Override public long getPower(){return power;}@Override public void setPower(long value){power=value;setChanged();}@Override public long getMaxPower(){return maxPower;}@Override public long energyStored(){return power;}@Override public long energyCapacity(){return maxPower;}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank getTankToPaste(){return null;}
 @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
 @Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
 public int steamType(){var type=tanks[0].getTankType();return type==HbmFluids.HOTSTEAM?1:type==HbmFluids.SUPERHOTSTEAM?2:type==HbmFluids.ULTRAHOTSTEAM?3:0;}
 @Override public String computerType(){return "ntm_turbine";}@Override public String[] computerMethods(){return new String[]{"getFluid","getType","setType","getPower","getInfo"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getFluid"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};case "getType"->new Object[]{steamType()};case "setType"->{tanks[0].setTankType(switch(args.checkInteger(0)){case 1->HbmFluids.HOTSTEAM;case 2->HbmFluids.SUPERHOTSTEAM;case 3->HbmFluids.ULTRAHOTSTEAM;default->HbmFluids.STEAM;});setChanged();yield new Object[]{};}case "getPower"->new Object[]{power};case "getInfo"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill(),steamType(),power};default->throw new ComputerException("No such method: "+method);};}
 @Override protected void saveMachine(CompoundTag tag){tanks[0].writeToNBT(tag,"water");tanks[1].writeToNBT(tag,"steam");tag.putLong("power",power);tag.putBoolean("tilted",tilted);}
 @Override protected void loadMachine(CompoundTag tag){tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");power=tag.getLong("power");tilted=tag.getBoolean("tilted");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("operational",operational);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);shouldTurn=tag.getBoolean("operational");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.remove("Items");var list=new ListTag();for(int i=0;i<7;i++)if(!slots.get(i).isEmpty()){var stack=(CompoundTag)slots.get(i).save(lookup);stack.putByte("slot",(byte)i);list.add(stack);}tag.put("items",list);if(!sourceName.isEmpty())tag.putString("name",sourceName);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("items",Tag.TAG_LIST)){slots.replaceAll(stack->ItemStack.EMPTY);for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var stack=(CompoundTag)value;int slot=stack.getByte("slot");if(slot>=0&&slot<7)slots.set(slot,ItemStack.parseOptional(lookup,stack));}}sourceName=tag.getString("name");}
}
