package com.hbm.machine.oilextraction;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmItemTags;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
/** The actual OilDrillBase finite-rock extraction loop, shared by the original two variants. */
public class OilDrillEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable {
 public BigInteger power=BigInteger.ZERO;public int clientMaximum,indicator,speedLevel,energyLevel,overLevel=1,afterburnLevel;public float rot,prevRot,speed;public boolean explosionHandled;
 public FluidTank[] tanks={new FluidTank(HbmFluids.fromName("OIL"),64000),new FluidTank(HbmFluids.fromName("GAS"),64000)};
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private final Map<BlockPos,FluidPorts> fluids=new LinkedHashMap<>();
 private final Set<BlockPos> trace=new HashSet<>();
 public OilDrillEntity(BlockPos pos,BlockState state){this(OilExtractionContent.ENTITY.get(((OilDrillBlock)state.getBlock()).kind).get(),pos,state);}
 protected OilDrillEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state,8);}
 public OilDrillKind kind(){return ((OilDrillBlock)getBlockState().getBlock()).kind;}public OilDrillConfig.Values config(){return OilDrillConfig.get(kind());}
 @Override protected String title(){return kind().title;}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new OilDrillMenu(id,inventory,this,worldPosition,kind());}
 public static Block block(String id){return BuiltInRegistries.BLOCK.get(ResourceLocation.parse("hbm:"+id));}
 public static BlockPos[] portPositions(BlockPos core,Direction dir,OilDrillKind kind){if(kind==OilDrillKind.WELL)return new BlockPos[]{core};var rot=dir.getCounterClockWise();return new BlockPos[]{core.relative(rot,2).relative(dir),core.relative(rot,2).relative(dir,-1),core.relative(rot,4).relative(dir),core.relative(rot,4).relative(dir,-1)};}
 public static Direction[] portSides(BlockPos core,Direction dir,OilDrillKind kind,BlockPos port){if(kind==OilDrillKind.WELL)return new Direction[]{Direction.WEST,Direction.EAST,Direction.NORTH,Direction.SOUTH,Direction.DOWN};int along=(port.getX()-core.getX())*dir.getStepX()+(port.getZ()-core.getZ())*dir.getStepZ();return new Direction[]{along>0?dir:dir.getOpposite()};}
 protected BlockPos[] sourcePorts(){return portPositions(worldPosition,DummyableBlock.facing(getBlockState()),kind());}
 protected Direction[] sourceSides(BlockPos port){return portSides(worldPosition,DummyableBlock.facing(getBlockState()),kind(),port);}
 private void updatePorts(ServerLevel server){var loaded=new HashSet<BlockPos>();for(var pos:sourcePorts()){if(!server.hasChunkAt(pos)||!server.getBlockState(pos).is(getBlockState().getBlock())||!pos.equals(worldPosition)&&!DummyableBlock.isPort(server.getBlockState(pos)))continue;loaded.add(pos);var links=Arrays.stream(sourceSides(pos)).map(side->new NodeConnection(pos.relative(side),side)).toArray(NodeConnection[]::new);var node=nodes.get(pos);if(node==null||node.isExpired()){if(node!=null)HbmEnergy.nodespace(server).destroyNode(node);node=new PowerNode(pos).setConnections(links);HbmEnergy.nodespace(server).createNode(node);nodes.put(pos,node);}if(node.hasValidNet())node.addReceiver(this);fluids.computeIfAbsent(pos,p->new FluidPorts(new BlockPos[]{p},links)).update(server,this);}for(var old:List.copyOf(nodes.keySet()))if(!loaded.contains(old)){HbmEnergy.nodespace(server).destroyNode(nodes.remove(old));var fluid=fluids.remove(old);if(fluid!=null)fluid.remove(server);}}
 public static void tick(Level level,BlockPos pos,BlockState state,OilDrillEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else if(machine.kind()==OilDrillKind.PUMPJACK){machine.prevRot=machine.rot;if(machine.indicator==0)machine.rot+=machine.speed;if(machine.rot>=360){machine.prevRot-=360;machine.rot-=360;}}}
 private void upgrades(){speedLevel=energyLevel=afterburnLevel=0;int over=0;for(int i=5;i<=7;i++){var item=slots.get(i).getItem();if(item instanceof FurnaceUpgradeItem upgrade){if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED)speedLevel+=upgrade.tier();else energyLevel+=upgrade.tier();}else if(item instanceof OverdriveUpgradeItem upgrade)over+=upgrade.tier();else if(item instanceof AfterburnUpgradeItem upgrade)afterburnLevel+=upgrade.tier();}speedLevel=Math.min(3,speedLevel);energyLevel=Math.min(3,energyLevel);afterburnLevel=Math.min(3,afterburnLevel);overLevel=Math.min(3,over)+1;}
 public int powerRequired(){int req=config().consumption();return(req+req/4*speedLevel-req/4*energyLevel)*overLevel;}
 public int delay(){int delay=config().delay();return Math.max((delay-delay/4*speedLevel+delay/10*energyLevel)/overLevel,1);}
 private void charge(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(getMaxPowerExact())>0){power=getMaxPowerExact();return;}var stack=slots.getFirst();if(stack.getItem() instanceof CreativeBatteryItem){power=getMaxPowerExact();return;}if(stack.getItem() instanceof HbmBattery battery){long draw=Math.min(Math.min(getMaxPowerExact().subtract(power).longValueExact(),battery.getDischargeRate(stack)),battery.getCharge(stack));battery.dischargeBattery(stack,draw);power=power.add(BigInteger.valueOf(draw));}}
 protected void serverTick(ServerLevel server){updatePorts(server);var items=slots.toArray(ItemStack[]::new);tanks[0].unloadTank(1,2,items);tanks[1].unloadTank(3,4,items);for(int i=1;i<=4;i++)slots.set(i,items[i]);upgrades();int burn=Math.min(tanks[1].getFill(),afterburnLevel*10);if(burn>0){tanks[1].setFill(tanks[1].getFill()-burn);power=power.add(BigInteger.valueOf(burn*5L)).min(getMaxPowerExact());}charge();if(power.compareTo(BigInteger.valueOf(powerRequired()))>=0&&tanks[0].getFill()<tanks[0].getMaxFill()&&tanks[1].getFill()<tanks[1].getMaxFill()){power=power.subtract(BigInteger.valueOf(powerRequired()));if(server.getGameTime()%delay()==0){indicator=0;int depth=drillDepth(server);for(int y=worldPosition.getY()-1;y>=depth;y--){var at=new BlockPos(worldPosition.getX(),y,worldPosition.getZ());if(!server.getBlockState(at).is(OilExtractionContent.PIPE.get())){if(!trySuck(at))tryDrill(at);break;}if(y==depth)indicator=1;}}}else indicator=2;sync(25);}
 private void tryDrill(BlockPos pos){var state=level.getBlockState(pos);if(state.getBlock().getExplosionResistance()<1000){onDrill(pos,state);level.setBlock(pos,OilExtractionContent.PIPE.get().defaultBlockState(),3);}else indicator=2;}
 protected void onDrill(BlockPos pos,BlockState state){var stack=new ItemStack(state.getBlock());for(var name:HbmItemTags.tags(stack)){String gas=name.equals("c:ores/uranium")?"gas_radon_dense":name.equals("c:ores/asbestos")?"gas_asbestos":null;if(gas==null)continue;if(kind()==OilDrillKind.PUMPJACK){for(var side:Direction.Plane.HORIZONTAL){var at=worldPosition.relative(side);if(level.getBlockState(at).canBeReplaced())level.setBlock(at,block(gas).defaultBlockState(),3);}}else{for(int j=-1;j<=1;j++)for(int k=-1;k<=1;k++){// Preserve source j/k probe/placement coordinates, including its explicit legacy quirk.
 if(level.getBlockState(worldPosition.offset(j,10,j)).canBeReplaced())level.setBlock(worldPosition.offset(k,10,k),block(gas).defaultBlockState(),3);}}}}
 protected int drillDepth(ServerLevel server){return server.getMinBuildHeight()+5;}
 protected boolean canSuckBlock(BlockState state){return state.is(block("ore_oil"))||state.is(block("ore_oil_empty"));}
 protected boolean canPump(){return true;}
 private boolean trySuck(BlockPos pos){var state=level.getBlockState(pos);if(!canSuckBlock(state))return false;if(!canPump())return true;trace.clear();return suck(pos,0);}
 private boolean suck(BlockPos pos,int layer){if(!trace.add(pos.immutable())||layer>64)return false;var state=level.getBlockState(pos);if(state.is(block("ore_oil"))||state.is(block("ore_bedrock_oil"))){doSuck(pos);return true;}if(state.is(block("ore_oil_empty"))){var directions=new ArrayList<>(List.of(Direction.values()));Collections.shuffle(directions);for(var side:directions)if(suck(pos.relative(side),layer+1))return true;}return false;}
 protected void doSuck(BlockPos pos){if(level.getBlockState(pos).is(block("ore_oil")))onSuck(pos);}
 protected void onSuck(BlockPos pos){var c=config();if(kind()==OilDrillKind.WELL)level.playSound(null,worldPosition,net.minecraft.sounds.SoundEvents.GENERIC_SPLASH,net.minecraft.sounds.SoundSource.BLOCKS,2,.5F);tanks[0].setFill(Math.min(tanks[0].getMaxFill(),tanks[0].getFill()+c.oil()));tanks[1].setFill(Math.min(tanks[1].getMaxFill(),tanks[1].getFill()+c.gasMin()+level.random.nextInt(c.gasMax()-c.gasMin()+1)));if(level.random.nextDouble()<c.drain())level.setBlock(pos,block("ore_oil_empty").defaultBlockState(),3);}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null&&(side.getAxis().isHorizontal()||side==Direction.DOWN&&kind()==OilDrillKind.WELL);}@Override public boolean canConnect(HbmFluidType type,Direction side){return canConnect(side);}@Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getSendingTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return FluidTank.EMPTY_ARRAY;}
 @Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(level!=null&&level.isClientSide&&clientMaximum>0?clientMaximum:config().power());}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}@Override public long getPower(){return power.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}@Override public long getMaxPower(){return getMaxPowerExact().longValueExact();}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}
 @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.processing",indicator==0?1:0,1,0x65be6b));}
 @Override public int[] getFluidIDToCopy(){return IFluidCopiable.super.getFluidIDToCopy();}@Override public FluidTank getTankToPaste(){return null;}
 @Override protected void saveMachine(CompoundTag tag){tag.putLong("power",getPower());tag.putString("powerExact",power.toString());for(int i=0;i<tanks.length;i++)tanks[i].writeToNBT(tag,"t"+i);}@Override protected void loadMachine(CompoundTag tag){power=tag.contains("powerExact")?new BigInteger(tag.getString("powerExact")):BigInteger.valueOf(tag.getLong("power"));for(int i=0;i<tanks.length;i++)tanks[i].readFromNBT(tag,"t"+i);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putInt("indicator",indicator);tag.putInt("maximum",config().power());tag.putFloat("speed",indicator==0?5F+2F*speedLevel+(overLevel-1F)*10:0);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);indicator=tag.getInt("indicator");clientMaximum=tag.getInt("maximum");speed=tag.getFloat("speed");}
 @Override public void setItem(int slot,ItemStack stack){super.setItem(slot,stack);if(level!=null&&slot>=5&&slot<=7&&upgrade(stack))level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,com.hbm.registry.HbmSounds.get("item.upgradePlug"),net.minecraft.sounds.SoundSource.BLOCKS,1,1);}
 public static boolean upgrade(ItemStack stack){return stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem||stack.getItem() instanceof AfterburnUpgradeItem;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
 public ItemStack dropStack(){var stack=new ItemStack(getBlockState().getBlock());if(power.signum()!=0||Arrays.stream(tanks).anyMatch(tank->tank.getFill()>0)){var data=new CompoundTag();saveMachine(data);var root=new CompoundTag();root.put("persistent",data);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));}return stack;}
 public void restore(ItemStack stack){var custom=stack.get(DataComponents.CUSTOM_DATA);if(custom!=null&&custom.copyTag().contains("persistent"))loadMachine(custom.copyTag().getCompound("persistent"));setChanged();}
 private void disconnect(){if(level instanceof ServerLevel server){nodes.values().forEach(node->HbmEnergy.nodespace(server).destroyNode(node));fluids.values().forEach(port->port.remove(server));}nodes.clear();fluids.clear();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
}
