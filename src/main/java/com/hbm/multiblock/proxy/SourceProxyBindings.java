package com.hbm.multiblock.proxy;
import api.hbm.redstoneoverradio.*;
import com.google.gson.*;
import com.hbm.api.computer.*;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.HeatSource;
import com.hbm.machine.foundry.FoundryAcceptor;
import com.hbm.material.MaterialStack;
import com.hbm.world.structure.LegacyStructureStates;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Original cached-position dispatch, restricted to the finite real source factory/metadata table.
 * No buffer, network, class-name coercion, reflection or changed unbound-machine implementation.
 */
public final class SourceProxyBindings {
 private static final String KEY="hbmSourceProxyBinding";
 private static final List<String> FLAGS=List.of("inv","power","conductor","fluid","heat","metal");
 private record Spec(String sourceKind,String sourceRegistry,Map<String,Boolean> flags){}
 private static final Map<String,Map<Integer,Spec>> TABLE=table();
 private static final Map<BlockState,Spec> CACHE=Collections.synchronizedMap(new HashMap<>());
 private static Map<String,Map<Integer,Spec>> table(){var result=new LinkedHashMap<String,Map<Integer,Spec>>();try(var stream=SourceProxyBindings.class.getResourceAsStream("/hbm/custom-machine114-source-proxies.json")){if(stream==null)throw new IllegalStateException("Missing exact X5846 proxy factory table");for(var element:JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonArray()){var row=element.getAsJsonObject();var states=new LinkedHashMap<Integer,Spec>();var metas=row.getAsJsonArray("metadata");for(int i=0;i<metas.size();i++)if(!metas.get(i).isJsonNull()){var value=metas.get(i).getAsJsonObject();var flags=new LinkedHashMap<String,Boolean>();for(var key:FLAGS)flags.put(key,value.getAsJsonObject("flags").get(key).getAsBoolean());states.put(i,new Spec(value.get("sourceProxy").getAsString(),row.get("sourceRegistryId").getAsString(),flags));}result.put(row.get("nativeBlock").getAsString(),states);}}catch(java.io.IOException e){throw new IllegalStateException(e);}return result;}
 private static Spec spec(BlockEntity proxy){if(!(proxy instanceof SourceRebindableProxy))return null;var state=proxy.getBlockState();if(CACHE.containsKey(state))return CACHE.get(state);var metas=TABLE.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());if(metas==null){CACHE.put(state,null);return null;}for(var property:state.getProperties())if(property.getName().equals("legacy_meta")&&state.getValue(property)instanceof Integer metadata){var spec=metas.get(metadata);CACHE.put(state,spec);return spec;}/* Explicit finite source states, for actual flattened host properties only. */if(metas.size()==16&&new HashSet<>(metas.values()).size()==1){var spec=metas.values().iterator().next();CACHE.put(state,spec);return spec;}for(var entry:metas.entrySet()){var expected=LegacyStructureStates.resolve(entry.getValue().sourceRegistry,entry.getKey(),0);if(expected.equals(state)){CACHE.put(state,entry.getValue());return entry.getValue();}}CACHE.put(state,null);return null;}
 public static Bound delegate(BlockEntity proxy){var bound=get(proxy);if(bound!=null)return bound;var spec=spec(proxy);var flags=spec==null?new LinkedHashMap<String,Boolean>():new LinkedHashMap<>(spec.flags);for(var key:FLAGS)flags.putIfAbsent(key,false);BlockEntity target=proxy instanceof SourceRebindableProxy source?source.sourceProxyFallbackTarget():null;if(target instanceof SourceRebindableProxy)target=null;return new Bound(proxy,target,spec==null?"":spec.sourceKind,flags);}
 public static BlockEntity originalCore(BlockEntity proxy){if(proxy.getLevel()==null||!(proxy.getBlockState().getBlock() instanceof com.hbm.multiblock.DummyableBlock block))return null;var core=block.findCore(proxy.getLevel(),proxy.getBlockPos());var target=core==null||!proxy.getLevel().hasChunkAt(core)?null:proxy.getLevel().getBlockEntity(core);return target instanceof SourceRebindableProxy?null:target;}
 public static boolean eligible(BlockEntity proxy){return proxy!=null&&spec(proxy)!=null;}
 public static boolean bind(BlockEntity proxy,BlockPos target){if(!eligible(proxy))return false;var tag=proxy.getPersistentData().getCompound(KEY);tag.putBoolean("hasPos",true);tag.putInt("pX",target.getX());tag.putInt("pY",target.getY());tag.putInt("pZ",target.getZ());proxy.getPersistentData().put(KEY,tag);proxy.setChanged();return true;}
 public static Bound get(BlockEntity proxy){if(!(proxy instanceof SourceRebindableProxy)||proxy.getLevel()==null)return null;var data=proxy.getPersistentData();if(!data.contains(KEY,net.minecraft.nbt.Tag.TAG_COMPOUND))return null;var tag=data.getCompound(KEY);if(!tag.getBoolean("hasPos"))return null;var spec=spec(proxy);if(spec==null)return null;var position=new BlockPos(tag.getInt("pX"),tag.getInt("pY"),tag.getInt("pZ"));var target=proxy.getLevel().hasChunkAt(position)?proxy.getLevel().getBlockEntity(position):null;if(target==null||target instanceof SourceRebindableProxy){tag.remove("hasPos");tag.remove("pX");tag.remove("pY");tag.remove("pZ");proxy.getPersistentData().put(KEY,tag);proxy.setChanged();return null;}var flags=new LinkedHashMap<>(spec.flags);if(tag.contains("flags")){var saved=tag.getCompound("flags");for(var flag:FLAGS)flags.put(flag,saved.getBoolean(flag));}return new Bound(proxy,target,spec.sourceKind,flags);}
 public static void load(BlockEntity proxy,CompoundTag source){var spec=spec(proxy);if(spec==null)return;var tag=proxy.getPersistentData().getCompound(KEY);if(source.getBoolean("hasPos")){tag.putBoolean("hasPos",true);for(var key:List.of("pX","pY","pZ"))tag.putInt(key,source.getInt(key));}if(spec.sourceKind.equals("TileEntityProxyCombo")||spec.sourceKind.equals("TileEntityProxyDyn")){boolean sourceFlags=source.getBoolean("hbm_legacy_block_entity");for(var key:FLAGS)sourceFlags|=source.contains(key);if(sourceFlags){var flags=new CompoundTag();for(var key:FLAGS)flags.putBoolean(key,source.getBoolean(key));tag.put("flags",flags);}}proxy.getPersistentData().put(KEY,tag);}
 public static void save(BlockEntity proxy,CompoundTag target){var spec=spec(proxy);if(spec==null)return;var tag=proxy.getPersistentData().getCompound(KEY);if(tag.getBoolean("hasPos")){target.putBoolean("hasPos",true);for(var key:List.of("pX","pY","pZ"))target.putInt(key,tag.getInt(key));}if(spec.sourceKind.equals("TileEntityProxyCombo")||spec.sourceKind.equals("TileEntityProxyDyn")){var flags=tag.getCompound("flags");for(var key:FLAGS)target.putBoolean(key,tag.contains("flags")?flags.getBoolean(key):spec.flags.get(key));}}
 /** A view of the existing target's native interfaces. It never owns independent HE, fluid or inventory. */
 public static final class Bound implements WorldlyContainer,EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver,HeatSource,FoundryAcceptor,ComputerControl,IRORValueProvider,IRORInteractive,MachineView {
  public final BlockEntity proxy;public final Object target;private final String kind;private final Map<String,Boolean> flags;
  Bound(BlockEntity proxy,BlockEntity target,String kind,Map<String,Boolean> flags){this.proxy=proxy;Object actual=kind.equals("TileEntityProxyDyn")&&target instanceof SourceProxyDelegateProvider delegate?delegate.sourceProxyDelegate(proxy.getBlockPos()):null;this.target=actual==null?target:actual;this.kind=kind;this.flags=flags;}
  private boolean flag(String key){return flags.get(key);}
  private WorldlyContainer inventory(){return flag("inv")&&target instanceof WorldlyContainer inv?inv:null;}
  private EnergyReceiver energy(){return flag("power")&&target instanceof EnergyReceiver energy?energy:null;}
  private FluidReceiver fluid(){return flag("fluid")&&target instanceof FluidReceiver fluid?fluid:null;}
  @Override public int getContainerSize(){var inv=inventory();return inv==null?0:inv.getContainerSize();}
  @Override public boolean isEmpty(){var inv=inventory();return inv==null||inv.isEmpty();}
  @Override public int getMaxStackSize(){var inv=inventory();return inv==null?0:inv.getMaxStackSize();}
  @Override public ItemStack getItem(int slot){var inv=inventory();return inv==null?ItemStack.EMPTY:inv.getItem(slot);}
  @Override public ItemStack removeItem(int slot,int count){/* Original Combo.decrStackSize uniquely ignores its inventory flag. */var inv=(kind.equals("TileEntityProxyCombo")||kind.equals("TileEntityProxyDyn"))&&target instanceof WorldlyContainer actual?actual:inventory();return inv==null?ItemStack.EMPTY:inv.removeItem(slot,count);}
  @Override public ItemStack removeItemNoUpdate(int slot){var inv=inventory();return inv==null?ItemStack.EMPTY:inv.removeItemNoUpdate(slot);}
  @Override public void setItem(int slot,ItemStack stack){var inv=inventory();if(inv!=null)inv.setItem(slot,stack);}
  @Override public void clearContent(){var inv=inventory();if(inv!=null)inv.clearContent();}
  @Override public void setChanged(){var inv=inventory();if(inv!=null)inv.setChanged();}
  @Override public boolean stillValid(Player player){var inv=inventory();return inv!=null&&inv.stillValid(player);}
  @Override public boolean canPlaceItem(int slot,ItemStack stack){var inv=inventory();return inv instanceof SourceProxyInventoryAccess source?source.sourceProxyCanPlace(proxy.getBlockPos(),slot,stack):inv!=null&&inv.canPlaceItem(slot,stack);}
  @Override public int[] getSlotsForFace(Direction side){var inv=inventory();return inv instanceof SourceProxyInventoryAccess source?source.sourceProxySlots(proxy.getBlockPos(),side):inv==null?new int[0]:inv.getSlotsForFace(side);}
  @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){var inv=inventory();return inv instanceof SourceProxyInventoryAccess source?source.sourceProxyCanInsert(proxy.getBlockPos(),slot,stack,side):inv!=null&&inv.canPlaceItemThroughFace(slot,stack,side);}
  @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){var inv=inventory();return inv instanceof SourceProxyInventoryAccess source?source.sourceProxyCanExtract(proxy.getBlockPos(),slot,stack,side):inv!=null&&inv.canTakeItemThroughFace(slot,stack,side);}
  @Override public long getPower(){var energy=energy();return energy==null?0:energy.getPower();}
  @Override public long getMaxPower(){var energy=energy();return energy==null?0:energy.getMaxPower();}
  @Override public void setPower(long value){var energy=energy();if(energy!=null)energy.setPower(value);}
  @Override public BigInteger getPowerExact(){var energy=energy();return energy==null?BigInteger.ZERO:energy.getPowerExact();}
  @Override public BigInteger getMaxPowerExact(){var energy=energy();return energy==null?BigInteger.ZERO:energy.getMaxPowerExact();}
  @Override public void setPowerExact(BigInteger value){var energy=energy();if(energy!=null)energy.setPowerExact(value);}
  @Override public long transferPower(long value){var energy=energy();return energy==null?(kind.equals("TileEntityProxyEnergy")?0:value):energy.transferPower(value);}
  @Override public BigInteger transferPowerExact(BigInteger value){var energy=energy();return energy==null?(kind.equals("TileEntityProxyEnergy")?BigInteger.ZERO:value):energy.transferPowerExact(value);}
  @Override public long getReceiverSpeed(){var energy=energy();return energy==null?0:energy.getReceiverSpeed();}
  @Override public BigInteger getReceiverSpeedExact(){var energy=energy();return energy==null?BigInteger.ZERO:energy.getReceiverSpeedExact();}
  public long getProviderSpeed(){return 0;}public BigInteger getProviderSpeedExact(){return BigInteger.ZERO;}public void usePower(long value){}public void usePowerExact(BigInteger value){}
  @Override public boolean isLoaded(){return !proxy.isRemoved()&&proxy.getLevel()!=null;}
  @Override public boolean canConnect(Direction side){if(kind.equals("TileEntityProxyConductor"))return true;return (flag("power")||flag("conductor"))&&target instanceof SidedEnergyConnector connector&&connector.canConnect(side);}
  public PowerNode energyNode(){return flag("conductor")&&target instanceof EnergyConductor conductor?conductor.energyNode():null;}
  @Override public boolean canConnect(HbmFluidType type,Direction side){return flag("fluid")&&(!(target instanceof FluidUser connector)||connector.canConnect(type,side));}
  @Override public FluidTank[] getAllTanks(){var fluid=fluid();return fluid==null?FluidTank.EMPTY_ARRAY:fluid.getAllTanks();}
  @Override public FluidTank[] getReceivingTanks(){return flag("fluid")&&target instanceof StandardFluidReceiver receiver?receiver.getReceivingTanks():FluidTank.EMPTY_ARRAY;}
  @Override public long transferFluid(HbmFluidType type,int pressure,long amount){var fluid=fluid();return fluid==null?amount:fluid.transferFluid(type,pressure,amount);}
  @Override public long getDemand(HbmFluidType type,int pressure){var fluid=fluid();return fluid==null?0:fluid.getDemand(type,pressure);}
  @Override public int[] getReceivingPressureRange(HbmFluidType type){var fluid=fluid();return fluid==null?DEFAULT_PRESSURE_RANGE:fluid.getReceivingPressureRange(type);}
  @Override public long getReceiverSpeed(HbmFluidType type,int pressure){var fluid=fluid();return fluid==null?0:fluid.getReceiverSpeed(type,pressure);}
  public FluidTank[] getSendingTanks(){return FluidTank.EMPTY_ARRAY;}public long getAvailable(HbmFluidType type,int pressure){return 0;}public void useUpFluid(HbmFluidType type,int pressure,long amount){}public int[] getSendingPressureRange(HbmFluidType type){return DEFAULT_PRESSURE_RANGE;}public long getSenderSpeed(HbmFluidType type,int pressure){return 0;}public long getProviderSpeed(HbmFluidType type,int pressure){return 0;}
  @Override public int getHeatStored(){return flag("heat")&&target instanceof HeatSource heat?heat.getHeatStored():0;}
  @Override public void useUpHeat(int amount){if(flag("heat")&&target instanceof HeatSource heat)heat.useUpHeat(amount);}
  @Override public boolean canAcceptPartialFlow(Direction side,MaterialStack stack){return flag("metal")&&target instanceof FoundryAcceptor acceptor&&acceptor.canAcceptPartialFlow(side,stack);}
  @Override public MaterialStack flow(Direction side,MaterialStack stack){return flag("metal")&&target instanceof FoundryAcceptor acceptor?acceptor.flow(side,stack):null;}
  @Override public boolean canAcceptPartialPour(Vec3 hit,Direction side,MaterialStack stack){return flag("metal")&&target instanceof FoundryAcceptor acceptor&&acceptor.canAcceptPartialPour(hit,side,stack);}
  @Override public MaterialStack pour(Vec3 hit,Direction side,MaterialStack stack){return flag("metal")&&target instanceof FoundryAcceptor acceptor?acceptor.pour(hit,side,stack):null;}
  @Override public String computerType(){return target instanceof ComputerControl computer?computer.computerType():"null";}
  @Override public String[] computerMethods(){return target instanceof ComputerControl computer?computer.computerMethods():new String[0];}
  @Override public Object[] callComputer(String method,ComputerArguments args){if(target instanceof ComputerControl computer)return computer.callComputer(method,args);throw new ComputerException("Source proxy has no computer component");}
  @Override public boolean canComputerConnect(Direction side){if(side==null||proxy.getLevel()==null)return false;var adjacentPosition=proxy.getBlockPos().relative(side);var adjacent=proxy.getLevel().hasChunkAt(adjacentPosition)?proxy.getLevel().getBlockEntity(adjacentPosition):null;var other=adjacent==null?null:get(adjacent);boolean same=adjacent==target||other!=null&&other.target==target;int metadata=metadata(proxy.getBlockState());return metadata>=6&&metadata<=11&&(flag("power")||flag("fluid")||target instanceof IRORInteractive)&&target instanceof ComputerControl computer&&computer.canComputerConnect(side)&&!same;}
  @Override public String[] getFunctionInfo(){return target instanceof IRORInfo info?info.getFunctionInfo():new String[0];}
  @Override public String provideRORValue(String name){return target instanceof IRORValueProvider value?value.provideRORValue(name):null;}
  @Override public String runRORFunction(String name,String[] args){return target instanceof IRORInteractive interactive?interactive.runRORFunction(name,args):null;}
  @Override public long energyStored(){return target instanceof MachineView view?view.energyStored():0;}@Override public long energyCapacity(){return target instanceof MachineView view?view.energyCapacity():0;}
  @Override public BigInteger energyStoredExact(){return target instanceof MachineView view?view.energyStoredExact():BigInteger.ZERO;}@Override public BigInteger energyCapacityExact(){return target instanceof MachineView view?view.energyCapacityExact():BigInteger.ZERO;}
  @Override public List<TankView> tanks(){return target instanceof MachineView view?view.tanks():List.of();}@Override public List<ProgressView> progressBars(){return target instanceof MachineView view?view.progressBars():List.of();}
 }
 private static int metadata(BlockState state){for(var property:state.getProperties())if(property.getName().equals("legacy_meta")&&state.getValue(property)instanceof Integer value)return value;return 0;}
 private SourceProxyBindings(){}
}
