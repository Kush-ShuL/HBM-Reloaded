package com.hbm.machine.crystallizer;

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

/** X5846 vacuum distillation:8 source slots, acid processing with original productivity and seven overdrive subcycles. */
public final class CrystallizerBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver,MachineView,IFluidCopiable, com.hbm.multiblock.MuffledMachine {
    public static Consumer<CrystallizerBlockEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{};
    public static final BigInteger CAPACITY=BigInteger.valueOf(1000000);
    private BigInteger power=BigInteger.ZERO;
    private final ItemStack[] slots=new ItemStack[8];
    public final FluidTank tank=new FluidTank(HbmFluids.fromName("PEROXIDE"),8000);
    public short progress,duration=600;public int speed,effect,overdrive;public float angle,prevAngle;
    public boolean isOn,muffled;
    private Component customName;private CompoundTag lastSentState;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
    public CrystallizerBlockEntity(BlockPos pos,BlockState state){super(CrystallizerContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.crystallizer"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CrystallizerMenu(id,inventory,this,menuData,worldPosition);}
    public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.offset(-1,0,-1),core.offset(1,0,-1),core.offset(1,0,1),core.offset(-1,0,1)};}
    public static Direction[] portSides(BlockPos core,BlockPos port){return new Direction[]{port.getX()<core.getX()?Direction.WEST:Direction.EAST,port.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH};}
    public static void tick(Level level,BlockPos pos,BlockState state,CrystallizerBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);var facing=DummyableBlock.facing(state).getClockWise();var box=new net.minecraft.world.phys.AABB(pos.getX()+.25,pos.getY()+1,pos.getZ()+.25,pos.getX()+.75,pos.getY()+6,pos.getZ()+.75).move(facing.getStepX()*1.5,0,facing.getStepZ()*1.5);for(var player:level.getEntitiesOfClass(Player.class,box))com.hbm.machine.fluidtank.LegacyFauxLadder.mark(player);}
    public CrystallizerRecipe recipe(){return level==null?null:CrystallizerRecipeConfig.find(level.getRecipeManager(),slots[0],tank.getTankType());}
    private void upgrades(){speed=0;effect=0;overdrive=0;for(int slot=5;slot<=6;slot++){var item=slots[slot].getItem();if(item instanceof com.hbm.machine.electricfurnace.FurnaceUpgradeItem upgrade&&upgrade.type()==com.hbm.machine.electricfurnace.FurnaceUpgradeItem.Type.SPEED)speed+=upgrade.tier();else if(item instanceof EffectUpgradeItem upgrade)effect+=upgrade.tier();else if(item instanceof com.hbm.machine.centrifuge.OverdriveUpgradeItem upgrade)overdrive+=upgrade.tier();}speed=Math.min(3,speed);effect=Math.min(3,effect);overdrive=Math.min(3,overdrive);}
    public int cycleCount(){return Math.min(1+overdrive*2,7);}public int powerRequired(){return 1000+speed*1000+effect*2000;}
    public short processDuration(){if(level!=null&&level.isClientSide)return duration;var recipe=recipe();int base=recipe==null?600:recipe.duration();return(short)(speed>0?Math.ceil(base*Math.max(1F-.25F*speed,.25F)):base);}
    private boolean same(ItemStack a,ItemStack b){return a.getItem()==b.getItem()&&a.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),b.getDamageValue()));}
    private boolean canProcess(CrystallizerRecipe recipe){if(slots[0].isEmpty()||power.compareTo(BigInteger.valueOf(powerRequired()))<0||recipe==null||slots[0].getCount()<recipe.inputItems().getFirst().count()||tank.getFill()<recipe.inputFluids().getFirst().fill)return false;var output=com.hbm.ore.OreProcessing.maximumOutput(recipe.getResultItem(level.registryAccess()),com.hbm.ore.OreProcessing.rate(slots[0]));return output.isEmpty()||slots[2].isEmpty()||same(slots[2],output)&&(long)slots[2].getCount()+output.getCount()<=slots[2].getMaxStackSize();}
    private void process(CrystallizerRecipe recipe){var output=com.hbm.ore.OreProcessing.output(recipe.getResultItem(level.registryAccess()),com.hbm.ore.OreProcessing.rate(slots[0]),level.random);if(slots[2].isEmpty())slots[2]=output;else slots[2].grow(output.getCount());tank.setFill(tank.getFill()-recipe.inputFluids().getFirst().fill);float free=effect>0?Math.min(effect*recipe.productivity(),.99F):0;if(free==0||free<level.random.nextFloat())slots[0].shrink(recipe.inputItems().getFirst().count());}
    private void serverTick(ServerLevel server){updatePorts(server);isOn=false;chargeBattery();boolean changed=tank.setType(7,slots);changed|=tank.loadTank(3,4,slots);upgrades();for(int cycle=0;cycle<cycleCount();cycle++){var recipe=recipe();if(canProcess(recipe)){progress++;power=power.subtract(BigInteger.valueOf(powerRequired()));isOn=true;if(progress>processDuration()){progress=0;process(recipe);}changed=true;}else progress=0;}if(changed)setChanged();var packetState=getUpdateTag(server.registryAccess());if(!packetState.equals(lastSentState)||server.getGameTime()%20==0){lastSentState=packetState;var packet=ClientboundBlockEntityDataPacket.create(this,(ignored,lookup)->packetState);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625)player.connection.send(packet);dispatchScope.commit();}}}
    private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(CAPACITY)>0){power=CAPACITY;return;}if(slots[1].getItem() instanceof CreativeBatteryItem){power=CAPACITY;return;}if(slots[1].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(CAPACITY.subtract(power).longValueExact(),battery.getDischargeRate(slots[1])),battery.getCharge(slots[1]));battery.dischargeBattery(slots[1],amount);power=power.add(BigInteger.valueOf(amount));if(amount>0)setChanged();}}
    private void updatePorts(ServerLevel server){
        var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);
        if(!loaded.equals(currentPorts)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var fluidConnections=new ArrayList<NodeConnection>();for(var port:loaded){var connections=Arrays.stream(portSides(worldPosition,port)).map(side->new NodeConnection(port.relative(side),side)).toArray(NodeConnection[]::new);var node=new PowerNode(port).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);fluidConnections.addAll(List.of(connections));}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),fluidConnections.toArray(NodeConnection[]::new));}
        if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long getPower(){return power.longValueExact();}@Override public long getMaxPower(){return 1000000;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return CAPACITY;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getAllTanks(){return getReceivingTanks();}
@Override public long getReceiverSpeed(HbmFluidType type,int pressure){return 1000000000L;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 1000000;}@Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.processing",progress,processDuration(),0x71c78b));}
    @Override public int[] getFluidIDToCopy(){return new int[]{tank.getTankType().getID()};}@Override public FluidTank getTankToPaste(){return tank;}
    private void saveState(CompoundTag tag){tag.putLong("power",getPower());tag.putString("powerExact",power.toString());tank.writeToNBT(tag,"tank");tag.putBoolean("muffled",muffled);}
    private void loadState(CompoundTag tag){power=tag.contains("powerExact",8)?new BigInteger(tag.getString("powerExact")):BigInteger.valueOf(tag.getLong("power"));tank.readFromNBT(tag,"tank");muffled=tag.getBoolean("muffled");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var inventory=NonNullList.withSize(8,ItemStack.EMPTY);for(int i=0;i<8;i++)inventory.set(i,slots[i]);ContainerHelper.saveAllItems(tag,inventory,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var inventory=NonNullList.withSize(8,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,inventory,lookup);for(int i=0;i<8;i++)slots[i]=inventory.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);tag.putBoolean("isOn",isOn);tag.putShort("progress",progress);tag.putShort("duration",processDuration());tag.putInt("overdrive",overdrive);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag);isOn=tag.getBoolean("isOn");progress=tag.getShort("progress");duration=tag.getShort("duration");overdrive=tag.getInt("overdrive");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 8;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;setChanged();if(level!=null&&slot>=5&&slot<=6&&CrystallizerMenu.upgrade(stack))level.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("item.upgradePlug"),net.minecraft.sounds.SoundSource.BLOCKS,1,1);}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&level!=null&&CrystallizerRecipeConfig.find(level.getRecipeManager(),stack,tank.getTankType())!=null||slot==1&&stack.getItem() instanceof HbmBattery;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0,2};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==2;}
    private final ContainerData menuData=new ContainerData(){public int getCount(){return 4;}public int get(int index){return(int)(getPower()>>>(index*16))&65535;}public void set(int index,int value){}};

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
