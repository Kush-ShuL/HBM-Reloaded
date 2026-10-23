package com.hbm.machine.cyclotron;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.crystallizer.EffectUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.hazard.HbmHazards;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Source3lanes/12slots and genuine HE+water/spentsteam/antimatter network. */
public final class CyclotronEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable ,com.hbm.multiblock.proxy.SourceProxyInventoryAccess {
 public static final long MAX_POWER=100000000;public static int consumption=1000000;public static final int DURATION=690;
 private long power;public int progress;private byte plugs;public boolean tilted;
 private static HbmFluidType realFluid(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE)throw new IllegalStateException("Missing original cyclotron fluid "+name);return type;}
 public final FluidTank[] tanks={new FluidTank(realFluid("WATER"),32000),new FluidTank(realFluid("SPENTSTEAM"),32000),new FluidTank(realFluid("AMAT"),8000)};
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
 private final ItemStack[] cachedUpgrades=new ItemStack[2];private int speedUpgrade,powerUpgrade,effectUpgrade;private CompoundTag lastPacked;private String sourceName="";
 public CyclotronEntity(BlockPos pos,BlockState state){super(CyclotronContent.ENTITY.get(),pos,state,12);}
 @Override protected String title(){return "container.cyclotron";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CyclotronMenu(id,inventory,this,worldPosition);}
 @Override public void setCustomName(Component name){sourceName=name.getString();setChanged();}@Override public Component getDisplayName(){return sourceName.isEmpty()?Component.translatable(title()):Component.literal(sourceName);}
 @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
 public static List<NodeConnection> connections(BlockPos core){var out=new ArrayList<NodeConnection>();for(int i=-1;i<=1;i++){out.add(new NodeConnection(core.offset(3,0,i),Direction.EAST));out.add(new NodeConnection(core.offset(-3,0,i),Direction.WEST));out.add(new NodeConnection(core.offset(i,0,3),Direction.SOUTH));out.add(new NodeConnection(core.offset(i,0,-3),Direction.NORTH));}return out;}
 public static List<BlockPos> ports(BlockPos core){return connections(core).stream().map(NodeConnection::origin).toList();}
 public static void tick(Level level,BlockPos pos,BlockState state,CyclotronEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
 private void checkUpgrades(){var first=slots.get(10);var second=slots.get(11);if(cachedUpgrades[0]==first&&cachedUpgrades[1]==second)return;cachedUpgrades[0]=first;cachedUpgrades[1]=second;speedUpgrade=powerUpgrade=effectUpgrade=0;for(var stack:List.of(first,second)){if(stack.getItem() instanceof FurnaceUpgradeItem u){if(u.type()==FurnaceUpgradeItem.Type.SPEED)speedUpgrade=Math.min(3,speedUpgrade+u.tier());else powerUpgrade=Math.min(3,powerUpgrade+u.tier());}else if(stack.getItem() instanceof EffectUpgradeItem u)effectUpgrade=Math.min(3,effectUpgrade+u.tier());}}
 public int getSpeed(){return speedUpgrade+1;}public int getConsumption(){return consumption-100000*powerUpgrade;}public int getCoolantConsumption(){return 500/(effectUpgrade+1)*getSpeed();}
 private void battery(){var stack=slots.get(9);if(stack.getItem() instanceof CreativeBatteryItem){power=MAX_POWER;return;}if(stack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(MAX_POWER-power,battery.getDischargeRate(stack)),battery.getCharge(stack));battery.dischargeBattery(stack,amount);power+=amount;}}
 public boolean canProcess(){if(power<getConsumption())return false;int convert=getCoolantConsumption();if(tanks[0].getFill()<convert||tanks[1].getFill()+convert>tanks[1].getMaxFill())return false;for(int i=0;i<3;i++){var recipe=CyclotronRecipes.find(level.getRecipeManager(),slots.get(i),slots.get(i+3));if(recipe==null)continue;var out=recipe.output();if(out.isEmpty())continue;if(slots.get(i+6).isEmpty()||same(slots.get(i+6),out)&&slots.get(i+6).getCount()<out.getMaxStackSize())return true;}return false;}
 private static boolean same(ItemStack a,ItemStack b){return ItemStack.isSameItem(a,b)&&a.getDamageValue()==b.getDamageValue()&&a.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0).equals(b.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
 public void process(){for(int i=0;i<3;i++){var recipe=CyclotronRecipes.find(level.getRecipeManager(),slots.get(i),slots.get(i+3));if(recipe==null)continue;var out=recipe.output();if(out.isEmpty())continue;var current=slots.get(i+6);if(current.isEmpty()){removeItem(i,1);removeItem(i+3,1);slots.set(i+6,out);tanks[2].setFill(tanks[2].getFill()+recipe.antimatter());}else if(same(current,out)&&current.getCount()<out.getMaxStackSize()){removeItem(i,1);removeItem(i+3,1);current.grow(1);tanks[2].setFill(tanks[2].getFill()+recipe.antimatter());}}if(tanks[2].getFill()>tanks[2].getMaxFill())tanks[2].setFill(tanks[2].getMaxFill());}
 private void serverTick(ServerLevel server){updatePorts(server);battery();checkUpgrades();if(canProcess()){progress+=getSpeed();power-=getConsumption();int convert=getCoolantConsumption();tanks[0].setFill(tanks[0].getFill()-convert);tanks[1].setFill(tanks[1].getFill()+convert);if(progress>=DURATION){process();progress=0;setChanged();}}else progress=0;var packed=getUpdateTag(server.registryAccess());if(!packed.equals(lastPacked)||server.getGameTime()%20==0){lastPacked=packed;sync(25);}}
 private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var p:ports(worldPosition))if(server.hasChunkAt(p)){var s=server.getBlockState(p);if(s.is(getBlockState().getBlock())&&DummyableBlock.isPort(s))loaded.add(p);}if(!loaded.equals(currentPorts)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var cons=connections(worldPosition).stream().filter(c->loaded.contains(c.origin())).toArray(NodeConnection[]::new);for(var c:cons){var node=new PowerNode(c.origin()).setConnections(c);HbmEnergy.nodespace(server).createNode(node);nodes.put(c.origin(),node);}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),cons);}if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);}
 private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 public void setPlug(int i){plugs|=1<<i;setChanged();}public boolean getPlug(int i){return(plugs&(1<<i))>0;}
 public static Item plugItem(int i){return com.hbm.registry.HbmContent.item(switch(i){case 0->"powder_balefire";case 1->"book_of_";case 2->"diamond_gavel";case 3->"coin_maskman";default->throw new IllegalArgumentException("No original cyclotron plug"+i);});}
 public static boolean particle(ItemStack stack){for(String id:List.of("part_lithium","part_beryllium","part_carbon","part_copper","part_plutonium"))if(stack.is(com.hbm.registry.HbmContent.item(id)))return true;return false;}
 public int[] portSlots(BlockPos port){for(var dir:List.of(Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST)){var rot=dir.getClockWise();var center=worldPosition.relative(dir,2);if(port.equals(center.relative(rot)))return new int[]{0,3,6,7,8};if(port.equals(center))return new int[]{1,4,6,7,8};if(port.equals(center.relative(rot,-1)))return new int[]{2,5,6,7,8};}return new int[]{6,7,8};}
 public boolean accepts(int slot,ItemStack stack){if(level==null)return false;for(var recipe:CyclotronRecipes.recipes(level.getRecipeManager()))if(slot<3&&recipe.inputItems().get(0).matches(stack,true)||slot>=3&&slot<6&&recipe.inputItems().get(1).matches(stack,true))return true;return false;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}
 @Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
 @Override public long getPower(){return power;}@Override public long getMaxPower(){return MAX_POWER;}@Override public void setPower(long value){power=value;setChanged();}@Override public long energyStored(){return power;}@Override public long energyCapacity(){return MAX_POWER;}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1],tanks[2]};}@Override public FluidTank getTankToPaste(){return null;}
 @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public List<ProgressView> progressBars(){return progress>0?List.of(new ProgressView("jade.hbm.processing",progress,DURATION,0xB874DB)):List.of();}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
 @Override protected void saveMachine(CompoundTag tag){tag.putLong("power",power);tag.putInt("progress",progress);tag.putByte("plugs",plugs);tag.putBoolean("tilted",tilted);for(int i=0;i<3;i++)tanks[i].writeToNBT(tag,"t"+i);}
 @Override protected void loadMachine(CompoundTag tag){power=tag.getLong("power");progress=tag.getInt("progress");plugs=tag.getByte("plugs");tilted=tag.getBoolean("tilted");for(int i=0;i<3;i++)tanks[i].readFromNBT(tag,"t"+i);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.remove("Items");var list=new ListTag();for(int i=0;i<12;i++)if(!slots.get(i).isEmpty()){var item=(CompoundTag)slots.get(i).save(lookup);item.putByte("slot",(byte)i);list.add(item);}tag.put("items",list);if(!sourceName.isEmpty())tag.putString("name",sourceName);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("items",Tag.TAG_LIST)){slots.replaceAll(stack->ItemStack.EMPTY);for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)value;int slot=item.getByte("slot");if(slot>=0&&slot<12)slots.set(slot,ItemStack.parseOptional(lookup,item));}}sourceName=tag.getString("name");Arrays.fill(cachedUpgrades,null);}

 @Override public int[] sourceProxySlots(net.minecraft.core.BlockPos position,net.minecraft.core.Direction side){return portSlots(position);}
 @Override public boolean sourceProxyCanPlace(net.minecraft.core.BlockPos position,int slot,net.minecraft.world.item.ItemStack stack){return accepts(slot,stack);}
 @Override public boolean sourceProxyCanInsert(net.minecraft.core.BlockPos position,int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){return accepts(slot,stack);}
 @Override public boolean sourceProxyCanExtract(net.minecraft.core.BlockPos position,int slot,net.minecraft.world.item.ItemStack stack,net.minecraft.core.Direction side){return slot>=6&&slot<=8;}
}
