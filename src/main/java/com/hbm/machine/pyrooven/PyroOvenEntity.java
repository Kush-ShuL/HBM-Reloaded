package com.hbm.machine.pyrooven;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.ore.OreProcessing;
import com.hbm.pollution.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Genuine electrical pyrolysis: source float progress, integer upgrade math and three buffered exhaust channels. */
public final class PyroOvenEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable {
 public static final BigInteger CAPACITY=BigInteger.valueOf(10000000);public static final int CONSUMPTION=10000;
 public static Consumer<PyroOvenEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
 public BigInteger power=BigInteger.ZERO;public boolean isVenting,isProgressing,tilted;public float progress;public int prevAnim,anim,speedLevel,powerLevel,overdriveLevel;
 public final FluidTank[] tanks={new FluidTank(HbmFluids.NONE,24000),new FluidTank(HbmFluids.NONE,24000)};
 public final FluidTank smoke=new FluidTank(HbmFluids.SMOKE,50),leaded=new FluidTank(HbmFluids.SMOKE_LEADED,50),poison=new FluidTank(HbmFluids.SMOKE_POISON,50);
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts,smokePorts;private List<BlockPos> currentPorts=List.of();private BlockPos currentSmoke;private PyroOvenRecipe lastValidRecipe;private CompoundTag lastSent;
 private final StandardFluidSender exhaust=new StandardFluidSender(){@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{smoke,leaded,poison};}@Override public FluidTank[] getAllTanks(){return getSendingTanks();}@Override public boolean isLoaded(){return PyroOvenEntity.this.isLoaded();}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidSender.super.useUpFluid(type,pressure,amount);setChanged();}};
 public PyroOvenEntity(BlockPos pos,BlockState state){super(PyroOvenContent.ENTITY.get(),pos,state,6);}
 @Override protected String title(){return "container.machinePyroOven";}@Override public AbstractContainerMenu createMenu(int id,Inventory player,Player owner){return new PyroOvenMenu(id,player,this,worldPosition);}
 public static BlockPos[] portPositions(BlockPos core,Direction dir){var rot=dir.getCounterClockWise();var result=new BlockPos[5];for(int i=0;i<5;i++)result[i]=core.relative(dir,2-i).relative(rot,2);return result;}
 public static BlockPos smokePosition(BlockPos core,Direction dir){return core.relative(dir.getCounterClockWise(),-1).above(2);}
 public static boolean upgrade(ItemStack stack){return com.hbm.machine.oilmixer.MixerEntity.isUpgradeItem(stack);}
 @Override public void setItem(int slot,ItemStack stack){super.setItem(slot,stack);if(level!=null&&slot>=4&&slot<=5&&upgrade(stack))level.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("item.upgradePlug"),SoundSource.BLOCKS,1,1);}
 private void upgrades(){speedLevel=powerLevel=overdriveLevel=0;for(int i=4;i<=5;i++){var item=slots.get(i).getItem();if(item instanceof FurnaceUpgradeItem u){if(u.type()==FurnaceUpgradeItem.Type.SPEED)speedLevel+=u.tier();else powerLevel+=u.tier();}else if(item instanceof OverdriveUpgradeItem u)overdriveLevel+=u.tier();}speedLevel=Math.min(speedLevel,3);powerLevel=Math.min(powerLevel,3);overdriveLevel=Math.min(overdriveLevel,3);}
 public static int consumption(int speed,int saving){return (int)(CONSUMPTION*Math.pow(speed+1,2))/(saving+1);}
 public boolean doesRecipeMatch(PyroOvenRecipe recipe){if(recipe.inputFluid()!=null&&tanks[0].getTankType()!=recipe.inputFluid().type)return false;if(recipe.inputItem()!=null)return recipe.inputItem().matchesProcessing(slots.get(1),true);return slots.get(1).isEmpty();}
 public PyroOvenRecipe matchingRecipe(){if(lastValidRecipe!=null&&doesRecipeMatch(lastValidRecipe))return lastValidRecipe;for(var recipe:PyroOvenRecipes.recipes(level.getRecipeManager()))if(doesRecipeMatch(recipe)){lastValidRecipe=recipe;return recipe;}return null;}
 private double rate(PyroOvenRecipe recipe){return recipe.inputItem()==null?1:OreProcessing.rate(slots.get(1));}
 private static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 private boolean canProcess(){if(power.compareTo(BigInteger.valueOf(consumption(speedLevel,powerLevel)))<0)return false;var recipe=matchingRecipe();if(recipe==null)return false;if(recipe.inputFluid()!=null&&tanks[0].getFill()<recipe.inputFluid().fill)return false;if(recipe.inputItem()!=null&&slots.get(1).getCount()<recipe.inputItem().count())return false;double rate=rate(recipe);var fluid=recipe.outputFluid();if(fluid!=null&&(long)OreProcessing.maximumCount(fluid.fill,rate)+tanks[1].getFill()>tanks[1].getMaxFill()&&fluid.type==tanks[1].getTankType())return false;var output=recipe.outputItem();var present=slots.get(2);if(!output.isEmpty()&&!present.isEmpty()){if((long)OreProcessing.maximumOutput(output,rate).getCount()+present.getCount()>present.getMaxStackSize())return false;if(output.getItem()!=present.getItem()||metadata(output)!=metadata(present))return false;}return true;}
 private void finishRecipe(PyroOvenRecipe recipe,ServerLevel server){double rate=rate(recipe);var output=OreProcessing.output(recipe.outputItem(),rate,server.random);if(!output.isEmpty()){if(slots.get(2).isEmpty())slots.set(2,output.copy());else slots.get(2).grow(output.getCount());}var fluid=recipe.outputFluid();if(fluid!=null){tanks[1].setTankType(fluid.type);tanks[1].setFill(tanks[1].getFill()+OreProcessing.count(fluid.fill,rate,server.random));}if(recipe.inputItem()!=null)removeItem(1,recipe.inputItem().count());if(recipe.inputFluid()!=null)tanks[0].setFill(tanks[0].getFill()-recipe.inputFluid().fill);}
 public static void tick(Level level,BlockPos pos,BlockState state,PyroOvenEntity self){if(level instanceof ServerLevel server)self.serverTick(server);else{self.prevAnim=self.anim;if(self.isProgressing)self.anim++;CLIENT_TICK.accept(self);}}
 private void serverTick(ServerLevel server){updatePorts(server);chargeBattery();tanks[0].setType(3,slots.toArray(ItemStack[]::new));upgrades();isVenting=false;isProgressing=false;if(canProcess()){var recipe=matchingRecipe();int duration=Math.max((recipe.duration()-speedLevel*(recipe.duration()/4))/(overdriveLevel*2+1),1);progress+=1F/duration;isProgressing=true;power=power.subtract(BigInteger.valueOf(consumption(speedLevel+overdriveLevel*2,powerLevel)));if(progress>=1F){progress=0;finishRecipe(recipe,server);setChanged();}pollute(server,PollutionSystem.SOOT_PER_SECOND);}else progress=0;var state=getUpdateTag(server.registryAccess());if(!state.equals(lastSent)||server.getGameTime()%20==0){lastSent=state;sync(50);}}
 private void chargeBattery(){var stack=slots.getFirst();if(stack.getItem() instanceof CreativeBatteryItem){power=CAPACITY;return;}if(stack.getItem() instanceof HbmBattery b){long amount=CAPACITY.subtract(power).min(BigInteger.valueOf(b.getDischargeRate(stack))).min(BigInteger.valueOf(b.getCharge(stack))).longValueExact();b.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));}}
 private void pollute(ServerLevel server,float amount){smoke.setFill(smoke.getFill()+(int)Math.ceil(amount*100));if(smoke.getFill()>50){int overflow=smoke.getFill()-50;smoke.setFill(50);PollutionSystem.increment(server,worldPosition,PollutionType.SOOT,overflow/100F);isVenting=true;}}
 private void updatePorts(ServerLevel server){var dir=DummyableBlock.facing(getBlockState());var rot=dir.getCounterClockWise();var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition,dir))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);var chimney=smokePosition(worldPosition,dir);BlockPos smokePos=server.hasChunkAt(chimney)&&server.getBlockState(chimney).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(chimney))?chimney:null;
  if(!loaded.equals(currentPorts)||!Objects.equals(smokePos,currentSmoke)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);currentSmoke=smokePos;var connections=new ArrayList<NodeConnection>();for(var port:loaded){var connection=new NodeConnection(port.relative(rot),rot);var node=new PowerNode(port).setConnections(connection);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);connections.add(connection);}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));if(smokePos!=null)smokePorts=new FluidPorts(new BlockPos[]{smokePos},new NodeConnection[]{new NodeConnection(smokePos.above(),Direction.UP)});}
  if(smokePorts!=null)smokePorts.update(server,exhaust);if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
 }
 @Override public long getPower(){return power.longValueExact();}@Override public long getMaxPower(){return 10000000;}@Override public void setPower(long amount){setPowerExact(BigInteger.valueOf(amount));}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return CAPACITY;}@Override public void setPowerExact(BigInteger amount){power=amount;setChanged();}
 @Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
 @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tanks[0],tanks[1],smoke};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1],smoke};}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rest=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rest!=amount)setChanged();return rest;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==1;}@Override public int[] getSlotsForFace(Direction side){return new int[]{1,2};}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==2;}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 10000000;}@Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public List<ProgressView> progressBars(){return isProgressing?List.of(new ProgressView("jade.hbm.processing",progress,1,0x71c78b)):List.of();}
 @Override protected void saveMachine(CompoundTag tag){tag.putString("power",power.toString());tag.putFloat("prog",progress);tag.putBoolean("tilted",tilted);for(int i=0;i<2;i++)tanks[i].writeToNBT(tag,"t"+i);smoke.writeToNBT(tag,"smoke0");leaded.writeToNBT(tag,"smoke1");poison.writeToNBT(tag,"smoke2");}
 @Override protected void loadMachine(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));progress=tag.getFloat("prog");tilted=tag.getBoolean("tilted");for(int i=0;i<2;i++)tanks[i].readFromNBT(tag,"t"+i);smoke.readFromNBT(tag,"smoke0");leaded.readFromNBT(tag,"smoke1");poison.readFromNBT(tag,"smoke2");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("isProgressing",isProgressing);tag.putBoolean("isVenting",isVenting);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);isProgressing=tag.getBoolean("isProgressing");isVenting=tag.getBoolean("isVenting");}
 private void disconnect(){if(level instanceof ServerLevel server){nodes.values().forEach(n->HbmEnergy.nodespace(server).destroyNode(n));if(fluidPorts!=null)fluidPorts.remove(server);if(smokePorts!=null)smokePorts.remove(server);}nodes.clear();fluidPorts=smokePorts=null;currentPorts=List.of();currentSmoke=null;}
 private void removed(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void onChunkUnloaded(){removed();super.onChunkUnloaded();}@Override public void setRemoved(){removed();super.setRemoved();}
}
