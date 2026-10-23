package com.hbm.machine.oilmixer;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.ore.OreProcessing;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original five-slot output-selected mixer, with exact upgrade arithmetic and source update order. */
public final class MixerEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable {
 public static final BigInteger CAPACITY=BigInteger.valueOf(10000);
 public final FluidTank[] tanks={new FluidTank(HbmFluids.NONE,16000),new FluidTank(HbmFluids.NONE,16000),new FluidTank(HbmFluids.NONE,24000)};
 public BigInteger power=BigInteger.ZERO;public int progress,processTime,recipeIndex,consumption=50,speedLevel,powerLevel,overLevel;public boolean wasOn;public float rotation,prevRotation;
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;
 public MixerEntity(BlockPos pos,BlockState state){super(MixerContent.ENTITY.get(),pos,state,5);}
 @Override protected String title(){return "container.machineMixer";}@Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new MixerMenu(id,inv,this,worldPosition);}
 public static Direction[] coreSides(){return new Direction[]{Direction.EAST,Direction.WEST,Direction.SOUTH,Direction.NORTH,Direction.DOWN};}
 private void updatePorts(ServerLevel server){var core=worldPosition;var top=core.above(2);if(nodes.values().stream().anyMatch(PowerNode::isExpired))disconnect();if(nodes.isEmpty()){var coreConnections=Arrays.stream(coreSides()).map(s->new NodeConnection(core.relative(s),s)).toArray(NodeConnection[]::new);var low=new PowerNode(core).setConnections(coreConnections);var high=new PowerNode(top).setConnections(new NodeConnection(top.above(),Direction.UP));HbmEnergy.nodespace(server).createNode(low);HbmEnergy.nodespace(server).createNode(high);nodes.put(core,low);nodes.put(top,high);var all=new ArrayList<NodeConnection>(Arrays.asList(coreConnections));all.add(new NodeConnection(top.above(),Direction.UP));fluidPorts=new FluidPorts(new BlockPos[]{core,top},all.toArray(NodeConnection[]::new));}for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);if(fluidPorts!=null)fluidPorts.update(server,this);}
 public List<MixerRecipe> variants(){return level==null?List.of():MixerRecipeConfig.output(level.getRecipeManager(),tanks[2].getTankType());}
 public MixerRecipe selected(){var variants=variants();return variants.isEmpty()?null:variants.get(Math.floorMod(recipeIndex,variants.size()));}
 public void cycle(){recipeIndex++;setChanged();sync(50);}
 public static boolean isUpgradeItem(ItemStack stack){var id=BuiltInRegistries.ITEM.getKey(stack.getItem());return id.getNamespace().equals("hbm")&&Set.of("upgrade_speed_1","upgrade_speed_2","upgrade_speed_3","upgrade_effect_1","upgrade_effect_2","upgrade_effect_3","upgrade_power_1","upgrade_power_2","upgrade_power_3","upgrade_fortune_1","upgrade_fortune_2","upgrade_fortune_3","upgrade_afterburn_1","upgrade_afterburn_2","upgrade_afterburn_3","upgrade_overdrive_1","upgrade_overdrive_2","upgrade_overdrive_3","upgrade_radius","upgrade_health","upgrade_smelter","upgrade_shredder","upgrade_centrifuge","upgrade_crystallizer","upgrade_nullifier","upgrade_screm","upgrade_gc_speed","upgrade_5g").contains(id.getPath());}
 private void upgrades(){speedLevel=powerLevel=overLevel=0;for(int i=3;i<=4;i++){var item=slots.get(i).getItem();if(item instanceof FurnaceUpgradeItem u){if(u.type()==FurnaceUpgradeItem.Type.SPEED)speedLevel+=u.tier();else powerLevel+=u.tier();}else if(item instanceof OverdriveUpgradeItem u)overLevel+=u.tier();}speedLevel=Math.min(3,speedLevel);powerLevel=Math.min(3,powerLevel);overLevel=Math.min(6,overLevel);consumption=50+150*speedLevel;consumption-=consumption*powerLevel*.25;consumption*=overLevel*3+1;}
 private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(CAPACITY)>0){power=CAPACITY;return;}var stack=slots.getFirst();if(stack.getItem() instanceof CreativeBatteryItem){power=CAPACITY;return;}if(stack.getItem() instanceof HbmBattery b){long amount=Math.min(Math.min(CAPACITY.subtract(power).longValueExact(),b.getDischargeRate(stack)),b.getCharge(stack));b.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));}}
 public static void tick(Level level,BlockPos pos,BlockState state,MixerEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else{machine.prevRotation=machine.rotation;if(machine.wasOn)machine.rotation+=20F;if(machine.rotation>=360){machine.rotation-=360;machine.prevRotation-=360;}}}
 private void serverTick(ServerLevel server){updatePorts(server);chargeBattery();tanks[2].setType(2,slots.toArray(ItemStack[]::new));upgrades();var recipe=selected();wasOn=canProcess(recipe);if(wasOn){progress++;power=power.subtract(BigInteger.valueOf(consumption));processTime-=processTime*speedLevel/4;processTime/=overLevel+1;if(processTime<=0)processTime=1;if(progress>=processTime){process(recipe,server);progress=0;}}else progress=0;sync(50);}
 private double processingRate(MixerRecipe recipe){return recipe.inputItems().isEmpty()?1:OreProcessing.rate(slots.get(1));}
 private boolean canProcess(MixerRecipe recipe){if(recipe==null){recipeIndex=0;return false;}recipeIndex=Math.floorMod(recipeIndex,variants().size());for(int i=0;i<2;i++){var fluid=recipe.fluid(i);tanks[i].setTankType(fluid==null?HbmFluids.NONE:fluid.type);}for(int i=0;i<2;i++){var fluid=recipe.fluid(i);if(fluid!=null&&tanks[i].getFill()<fluid.fill)return false;}if(power.compareTo(BigInteger.valueOf(consumption))<0)return false;var output=recipe.outputFluids().getFirst();if((long)tanks[2].getFill()+OreProcessing.maximumCount(output.fill,processingRate(recipe))>tanks[2].getMaxFill())return false;if(!recipe.inputItems().isEmpty()&&!recipe.inputItems().getFirst().matchesProcessing(slots.get(1),false))return false;processTime=recipe.duration();return true;}
 private void process(MixerRecipe recipe,ServerLevel server){double rate=processingRate(recipe);for(int i=0;i<2;i++){var fluid=recipe.fluid(i);if(fluid!=null)tanks[i].setFill(tanks[i].getFill()-fluid.fill);}if(!recipe.inputItems().isEmpty())removeItem(1,recipe.inputItems().getFirst().count());tanks[2].setFill(tanks[2].getFill()+OreProcessing.count(recipe.outputFluids().getFirst().fill,rate,server.random));}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){var recipe=selected();return recipe!=null&&!recipe.inputItems().isEmpty()&&recipe.inputItems().getFirst().matchesProcessing(stack,true);}@Override public int[] getSlotsForFace(Direction side){return new int[]{1};}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null&&side!=Direction.UP;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
 @Override public long getPower(){return power.longValueExact();}@Override public long getMaxPower(){return 10000;}@Override public void setPower(long amount){setPowerExact(BigInteger.valueOf(amount));}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return CAPACITY;}@Override public void setPowerExact(BigInteger amount){power=amount;setChanged();}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0],tanks[1]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[2]};}@Override public FluidTank getTankToPaste(){return tanks[2];}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rest=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rest!=amount)setChanged();return rest;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 10000;}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.mixer.progress",progress,processTime,0x71c78b));}
 @Override protected void saveMachine(CompoundTag tag){tag.putString("powerExact",power.toString());tag.putLong("power",getPower());tag.putInt("progress",progress);tag.putInt("processTime",processTime);tag.putInt("recipe",recipeIndex);for(int i=0;i<3;i++)tanks[i].writeToNBT(tag,Integer.toString(i));}
 @Override protected void loadMachine(CompoundTag tag){power=tag.contains("powerExact",Tag.TAG_STRING)?new BigInteger(tag.getString("powerExact")):BigInteger.valueOf(tag.getLong("power"));progress=tag.getInt("progress");processTime=tag.getInt("processTime");recipeIndex=tag.getInt("recipe");for(int i=0;i<3;i++)tanks[i].readFromNBT(tag,Integer.toString(i));}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("wasOn",wasOn);tag.putInt("consumption",consumption);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);wasOn=tag.getBoolean("wasOn");consumption=tag.getInt("consumption");}
 private void disconnect(){if(level instanceof ServerLevel server){nodes.values().forEach(n->HbmEnergy.nodespace(server).destroyNode(n));if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
