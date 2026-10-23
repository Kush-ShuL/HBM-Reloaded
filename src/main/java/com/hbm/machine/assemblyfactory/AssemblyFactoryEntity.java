package com.hbm.machine.assemblyfactory;
import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.assembler.*;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Four real source assembly processors; ordered independent cooling, true private native ports and full60-slot inventory. */
public final class AssemblyFactoryEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,IRORValueProvider ,com.hbm.multiblock.proxy.SourceProxyDelegateProvider ,com.hbm.multiblock.proxy.SourceProxyInventoryAccess {
 public static Consumer<AssemblyFactoryEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
 public final FluidTank[] inputTanks=new FluidTank[4],outputTanks=new FluidTank[4];public final FluidTank water=new FluidTank(HbmFluids.WATER,4000),lps=new FluidTank(HbmFluids.SPENTSTEAM,4000);public final GenericMachineProcessor[] modules=new GenericMachineProcessor[4];public final boolean[] didProcess=new boolean[4];
 public BigInteger power=BigInteger.ZERO,maxPower=BigInteger.valueOf(1000000);public boolean tilted;private CompoundTag lastSent;private List<Port> cachedPower,cachedFluid;
 private List<Port> powerDefs(){if(cachedPower==null)cachedPower=powerDefinitions(worldPosition,DummyableBlock.facing(getBlockState()));return cachedPower;}
 private List<Port> fluidDefs(){if(cachedFluid==null)cachedFluid=fluidDefinitions(worldPosition,DummyableBlock.facing(getBlockState()));return cachedFluid;}
 private final Map<BlockPos,PowerNode> powerPorts=new LinkedHashMap<>();private FluidPorts recipePorts,coolantPorts;private List<Port> activePower=List.of(),activeFluid=List.of(),activeCoolant=List.of();
 private final StandardFluidTransceiver coolant=new StandardFluidTransceiver(){@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{water};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{lps};}@Override public FluidTank[] getAllTanks(){return AssemblyFactoryEntity.this.getAllTanks();}@Override public boolean isLoaded(){return AssemblyFactoryEntity.this.isLoaded();}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remaining=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(remaining!=amount)setChanged();return remaining;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}};
 public AssemblyFactoryEntity(BlockPos pos,BlockState state){
  super(AssemblyFactoryContent.ENTITY.get(),pos,state,60);
  for(int i=0;i<4;i++){inputTanks[i]=new FluidTank(HbmFluids.NONE,4000);outputTanks[i]=new FluidTank(HbmFluids.NONE,4000);modules[i]=new AssemblyFactoryProcessor(this,i,inputTanks[i],outputTanks[i],this,this::recipes);}
 }
 @Override protected String title(){return "container.machineAssemblyFactory";}@Override public AbstractContainerMenu createMenu(int id,Inventory player,Player owner){return new AssemblyFactoryMenu(id,player,this,worldPosition);}
 @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
 public List<AssemblerRecipe> recipes(){return level==null?List.of():AssemblerRecipeConfig.recipes(level.getRecipeManager());}
 public record Port(BlockPos position,List<Direction> sides,BlockPos physical){public NodeConnection[] connections(){return sides.stream().map(side->new NodeConnection(position.relative(side),side)).toArray(NodeConnection[]::new);}}
 private static Port port(BlockPos pos,Direction... sides){return new Port(pos,List.of(sides),pos);}
 public static List<Port> powerDefinitions(BlockPos c,Direction ignored){
  // Exact TilePortShapes.purex order:16 independent nodes/20 outward faces, no top ports.
  return List.of(port(c.offset(2,0,-1),Direction.EAST),port(c.east(2),Direction.EAST),port(c.offset(2,0,1),Direction.EAST),port(c.offset(2,0,2),Direction.EAST,Direction.SOUTH),port(c.offset(1,0,2),Direction.SOUTH),port(c.south(2),Direction.SOUTH),port(c.offset(-1,0,2),Direction.SOUTH),port(c.offset(-2,0,2),Direction.WEST,Direction.SOUTH),port(c.offset(-2,0,1),Direction.WEST),port(c.west(2),Direction.WEST),port(c.offset(-2,0,-1),Direction.WEST),port(c.offset(-2,0,-2),Direction.WEST,Direction.NORTH),port(c.offset(-1,0,-2),Direction.NORTH),port(c.north(2),Direction.NORTH),port(c.offset(1,0,-2),Direction.NORTH),port(c.offset(2,0,-2),Direction.EAST,Direction.NORTH));
 }
 public static List<Port> coolantDefinitions(BlockPos c,Direction dir){var rot=dir.getClockWise();return List.of(port(c.relative(rot).relative(dir,2),dir),port(c.relative(rot,-1).relative(dir,2),dir),port(c.relative(rot).relative(dir,-2),dir.getOpposite()),port(c.relative(rot,-1).relative(dir,-2),dir.getOpposite()));}
 public static List<Port> fluidDefinitions(BlockPos c,Direction dir){
  var rot=dir.getClockWise();return List.of(port(c.relative(dir).relative(rot,2),rot),port(c.relative(dir,-1).relative(rot,2),rot),port(c.relative(dir).relative(rot,-2),rot.getOpposite()),port(c.relative(dir,-1).relative(rot,-2),rot.getOpposite()),port(c.east(2),Direction.EAST),port(c.offset(2,0,2),Direction.EAST,Direction.SOUTH),port(c.south(2),Direction.SOUTH),port(c.offset(-2,0,2),Direction.WEST,Direction.SOUTH),port(c.west(2),Direction.WEST),port(c.offset(-2,0,-2),Direction.WEST,Direction.NORTH),port(c.north(2),Direction.NORTH),port(c.offset(2,0,-2),Direction.EAST,Direction.NORTH));
 }
 public boolean coolantPosition(BlockPos pos){return coolantDefinitions(worldPosition,DummyableBlock.facing(getBlockState())).stream().anyMatch(p->p.position.equals(pos));}
 public int fieldAtPort(BlockPos pos){var defs=fluidDefs();for(int i=0;i<4;i++)if(defs.get(i).position.equals(pos))return i;return -1;}
 private List<Port> loaded(ServerLevel server,List<Port> defs){return defs.stream().filter(p->server.hasChunkAt(p.position)&&server.hasChunkAt(p.physical)&&server.getBlockState(p.physical).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(p.physical))).toList();}
 private static FluidPorts fluidGroup(List<Port> defs){return defs.isEmpty()?null:new FluidPorts(defs.stream().map(Port::position).toArray(BlockPos[]::new),defs.stream().flatMap(p->Arrays.stream(p.connections())).toArray(NodeConnection[]::new));}
 private void updatePorts(ServerLevel server){var dir=DummyableBlock.facing(getBlockState());var defs=powerDefs();var power=loaded(server,defs);var fluids=loaded(server,fluidDefs());var cooling=loaded(server,coolantDefinitions(worldPosition,dir));if(!power.equals(activePower)||!fluids.equals(activeFluid)||!cooling.equals(activeCoolant)||powerPorts.values().stream().anyMatch(PowerNode::isExpired)){disconnect();activePower=power;activeFluid=fluids;activeCoolant=cooling;for(var p:power){var node=new PowerNode(p.position).setConnections(p.connections());HbmEnergy.nodespace(server).createNode(node);powerPorts.put(p.position,node);}recipePorts=fluidGroup(fluids);coolantPorts=fluidGroup(cooling);}if(recipePorts!=null)recipePorts.update(server,this);for(var node:powerPorts.values())if(node.hasValidNet())node.addReceiver(this);if(coolantPorts!=null)coolantPorts.update(server,coolant);}
 public boolean canCool(){return water.getFill()>=100&&lps.getFill()<=3900;}public boolean processing(){for(boolean active:didProcess)if(active)return true;return false;}
 public static void tick(Level level,BlockPos pos,BlockState state,AssemblyFactoryEntity factory){if(factory.maxPower.signum()<=0)factory.maxPower=BigInteger.valueOf(10000000);if(level instanceof ServerLevel server)factory.serverTick(server);else CLIENT_TICK.accept(factory);}
 private void serverTick(ServerLevel server){updatePorts(server);BigInteger requested=BigInteger.ZERO;for(var module:modules){var recipe=module.getRecipe();if(recipe!=null)requested=requested.add(BigInteger.valueOf(recipe.power()).multiply(BigInteger.valueOf(100)));}maxPower=requested.max(power).max(BigInteger.valueOf(1000000));chargeBattery();int speed=0,saving=0,overdrive=0;for(int i=1;i<=3;i++){var item=slots.get(i).getItem();if(item instanceof FurnaceUpgradeItem u){if(u.type()==FurnaceUpgradeItem.Type.SPEED)speed+=u.tier();else saving+=u.tier();}else if(item instanceof OverdriveUpgradeItem u)overdrive+=u.tier();}speed=Math.min(speed,3);saving=Math.min(saving,3);overdrive=Math.min(overdrive,3);double speedFactor=1+speed/3D+overdrive,powerFactor=1-saving*.25D+speed+overdrive*10D/3D;boolean dirty=false;
  for(int i=0;i<4;i++){modules[i].update(speedFactor*2,powerFactor*2,canCool(),slots.get(4+i*14));didProcess[i]=modules[i].didProcess;dirty|=modules[i].markDirty;if(didProcess[i]){water.setFill(water.getFill()-100);lps.setFill(lps.getFill()+100);}}
  if(dirty)setChanged();var state=getUpdateTag(server.registryAccess());if(!state.equals(lastSent)||server.getGameTime()%20==0){lastSent=state;sync(100);}
 }
 private void chargeBattery(){var stack=slots.getFirst();if(stack.getItem() instanceof CreativeBatteryItem){power=maxPower;return;}if(stack.getItem() instanceof HbmBattery b){long amount=maxPower.subtract(power).min(BigInteger.valueOf(b.getDischargeRate(stack))).min(BigInteger.valueOf(b.getCharge(stack))).longValueExact();b.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));}}
 public void selectRecipe(int index,String selection){if(index>=0&&index<4){modules[index].setRecipe(selection,false);setChanged();}}
 public void receiveControl(Player player,CompoundTag data){if(stillValid(player)&&data.contains("index")&&data.contains("selection"))selectRecipe(data.getInt("index"),data.getString("selection"));}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){if(slot==0)return true;for(int i=0;i<4;i++)if(slot==4+i*14&&stack.is(Blueprints.ITEM.get()))return true;if(slot>=1&&slot<=3&&com.hbm.machine.oilmixer.MixerEntity.isUpgradeItem(stack))return true;for(var module:modules)if(module.isItemValid(slot,stack))return true;return false;}
 public boolean extractable(int slot){for(int i=0;i<4;i++)if(slot==17+i*14)return true;for(var module:modules)if(module.isSlotClogged(slot))return true;return false;}
 @Override public int[] getSlotsForFace(Direction side){return java.util.stream.IntStream.range(5,60).filter(i->(i-4)%14!=0).toArray();}
 public int[] slotsAtPort(BlockPos pos,Direction side){if(coolantPosition(pos))return new int[0];int field=fieldAtPort(pos);if(field<0)return getSlotsForFace(side);var result=new int[16];for(int i=0;i<4;i++)result[i]=5+field*14+i;for(int i=0;i<4;i++)result[12+i]=17+i*14;return result;}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return extractable(slot);}
 private static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}@Override public long getPower(){return legacy(power);}@Override public long getMaxPower(){return legacy(maxPower);}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return maxPower;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}@Override public BigInteger getReceiverSpeedExact(){return maxPower;}
 @Override public long transferPower(long amount){return transferPowerExact(BigInteger.valueOf(amount)).longValueExact();}
 @Override public BigInteger transferPowerExact(BigInteger amount){if(amount.signum()<0)throw new IllegalArgumentException("Negative HE transfer");var room=maxPower.subtract(power);if(room.signum()<0)throw new IllegalStateException("Invalid factory HE storage");var accepted=room.min(amount);power=power.add(accepted);if(accepted.signum()!=0)setChanged();return amount.subtract(accepted);}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
 @Override public FluidTank[] getAllTanks(){var result=new FluidTank[10];System.arraycopy(inputTanks,0,result,0,4);System.arraycopy(outputTanks,0,result,4,4);result[8]=water;result[9]=lps;return result;}@Override public FluidTank[] getReceivingTanks(){return inputTanks;}@Override public FluidTank[] getSendingTanks(){return outputTanks;}public StandardFluidTransceiver fluidDelegate(BlockPos pos){return coolantPosition(pos)?coolant:this;}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rest=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rest!=amount)setChanged();return rest;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}@Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public List<ProgressView> progressBars(){var result=new ArrayList<ProgressView>();for(int i=0;i<4;i++)if(didProcess[i])result.add(new ProgressView("gui.hbm.assembly_factory.progress_"+i,modules[i].progress,1,0x71c78b));return result;}
 @Override protected void saveMachine(CompoundTag tag){for(int i=0;i<4;i++){inputTanks[i].writeToNBT(tag,"i"+i);outputTanks[i].writeToNBT(tag,"o"+i);}water.writeToNBT(tag,"w");lps.writeToNBT(tag,"s");tag.putString("power",power.toString());tag.putString("maxPower",maxPower.toString());tag.putBoolean("tilted",tilted);for(var module:modules)module.writeToNBT(tag);}
 @Override protected void loadMachine(CompoundTag tag){for(int i=0;i<4;i++){inputTanks[i].readFromNBT(tag,"i"+i);outputTanks[i].readFromNBT(tag,"o"+i);}water.readFromNBT(tag,"w");lps.readFromNBT(tag,"s");power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));maxPower=tag.contains("maxPower",8)?new BigInteger(tag.getString("maxPower")):BigInteger.valueOf(tag.getLong("maxPower"));tilted=tag.getBoolean("tilted");for(var module:modules)module.readFromNBT(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);int active=0,restricted=0;for(int i=0;i<4;i++){if(didProcess[i])active|=1<<i;if(modules[i].restrictedMode)restricted|=1<<i;}tag.putByte("active",(byte)active);tag.putByte("restricted",(byte)restricted);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);int active=tag.getByte("active"),restricted=tag.getByte("restricted");for(int i=0;i<4;i++){didProcess[i]=(active&1<<i)!=0;modules[i].restrictedMode=(restricted&1<<i)!=0;}}
 @Override public String[] getFunctionInfo(){return new String[]{"VAL:progress1","VAL:progress2","VAL:progress3","VAL:progress4","VAL:recipe1","VAL:recipe2","VAL:recipe3","VAL:recipe4","VAL:anyactive","VAL:active1","VAL:active2","VAL:active3","VAL:active4"};}@Override public String provideRORValue(String name){if(name.equals("VAL:anyactive"))return processing()?"1":"0";for(int i=0;i<4;i++){if(name.equals("VAL:progress"+(i+1)))return Integer.toString((int)Math.round(modules[i].progress*100));if(name.equals("VAL:recipe"+(i+1)))return modules[i].getRecipeName();if(name.equals("VAL:active"+(i+1)))return didProcess[i]?"1":"0";}return null;}
 private void disconnect(){if(level instanceof ServerLevel server){powerPorts.values().forEach(node->HbmEnergy.nodespace(server).destroyNode(node));if(recipePorts!=null)recipePorts.remove(server);if(coolantPorts!=null)coolantPorts.remove(server);}powerPorts.clear();recipePorts=coolantPorts=null;activePower=activeFluid=activeCoolant=List.of();}
 private void removed(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void onChunkUnloaded(){removed();super.onChunkUnloaded();}@Override public void setRemoved(){removed();super.setRemoved();}

 private final com.hbm.multiblock.proxy.SourceProxyFluidEnergyDelegate sourceProxyCoolantDelegate=new com.hbm.multiblock.proxy.SourceProxyFluidEnergyDelegate(this,coolant);
 @Override public Object sourceProxyDelegate(net.minecraft.core.BlockPos position){return coolantPosition(position)?sourceProxyCoolantDelegate:null;}

 @Override public int[] sourceProxySlots(net.minecraft.core.BlockPos position,net.minecraft.core.Direction side){return slotsAtPort(position,side);}
}
