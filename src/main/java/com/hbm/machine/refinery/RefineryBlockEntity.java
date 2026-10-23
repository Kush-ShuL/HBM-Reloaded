package com.hbm.machine.refinery;

import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.multiblock.*;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.pollution.*;
import com.hbm.recipe.generic.GenericIngredient;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** X5846 refinery:13 original slots,5 fixed tanks,100mB +5HE each successful tick. */
public final class RefineryBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,MachineView,IRepairable,IOverpressurable,IFluidCopiable, com.hbm.multiblock.MuffledMachine {
    public static Consumer<RefineryBlockEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{};
    public static java.util.function.BooleanSupplier MACHINE_GRAVITY=com.hbm.config.GeneralModes::machineGravity;
    private int tiltBlocksChecked,tiltBlocksValid;
    public static final BigInteger CAPACITY=BigInteger.valueOf(1000);
    private BigInteger power=BigInteger.ZERO;
    private final ItemStack[] slots=new ItemStack[13];
    public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("HOTOIL"),64000),new FluidTank(HbmFluids.fromName("HEAVYOIL"),24000),new FluidTank(HbmFluids.fromName("NAPHTHA"),24000),new FluidTank(HbmFluids.fromName("LIGHTOIL"),24000),new FluidTank(HbmFluids.fromName("PETROLEUM"),24000)};
    public boolean hasExploded,onFire,isOn,tilted,muffled;public int sulfur;public Explosion lastExplosion;
    private Component customName;private CompoundTag lastSentState;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
    public RefineryBlockEntity(BlockPos pos,BlockState state){super(RefineryContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.machineRefinery"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return hasExploded?null:new RefineryMenu(id,inventory,this,menuData,worldPosition);}
    public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.offset(-1,0,-1),core.offset(1,0,-1),core.offset(1,0,1),core.offset(-1,0,1)};}
    public static Direction[] portSides(BlockPos core,BlockPos port){return new Direction[]{port.getX()<core.getX()?Direction.WEST:Direction.EAST,port.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH};}
    public static void tick(Level level,BlockPos pos,BlockState state,RefineryBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
    private void serverTick(ServerLevel server){
        updatePorts(server);checkTilt(server);isOn=false;
        if(!hasExploded){
            chargeBattery();boolean changed=tanks[0].setType(12,slots);changed|=tanks[0].loadTank(1,2,slots);changed|=refine(server);
            for(int tank=1;tank<5;tank++)changed|=tanks[tank].unloadTank(1+tank*2,2+tank*2,slots);
            if(changed)setChanged();
        }else if(onFire){
            boolean hasFuel=false;for(var tank:tanks)if(tank.getFill()>0){tank.setFill(Math.max(0,tank.getFill()-10));hasFuel=true;}
            if(hasFuel){double x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();for(var entity:server.getEntitiesOfClass(Entity.class,new AABB(x-1.5,y,z-1.5,x+2.5,y+8,z+2.5)))entity.igniteForSeconds(5);var rand=server.random;LegacyFluidEffects.gasFlame(server,x+rand.nextDouble(),y+1.5+rand.nextDouble()*3,z+rand.nextDouble(),rand.nextGaussian()*.05,.1,rand.nextGaussian()*.05);if(server.getGameTime()%20==0)PollutionSystem.increment(server,worldPosition,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND*70);setChanged();}
        }
        var packetState=getUpdateTag(server.registryAccess());if(!packetState.equals(lastSentState)||server.getGameTime()%20==0){lastSentState=packetState;var packet=ClientboundBlockEntityDataPacket.create(this,(ignored,lookup)->packetState);for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);}
    }
    /** Source standardFloor3x3, four foundation corners over consecutive20-tick probes. */
    private void checkTilt(ServerLevel server){
        if(!MACHINE_GRAVITY.getAsBoolean()){tilted=false;return;}
        int identity=(worldPosition.getY()+worldPosition.getZ()*27644437)*27644437+worldPosition.getX();if((server.getGameTime()+identity)%20!=0)return;
        if(tiltBlocksChecked>=4){boolean next=tiltBlocksValid<tiltBlocksChecked*.95;if(next&&!tilted)server.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("block.metalImpact"),net.minecraft.sounds.SoundSource.BLOCKS,3,1);tilted=next;setChanged();tiltBlocksChecked=0;tiltBlocksValid=0;}
        var pos=worldPosition.offset(-1+(tiltBlocksChecked/2)*2,-1,-1+(tiltBlocksChecked%2)*2);tiltBlocksChecked++;var state=server.getBlockState(pos);var name=BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        if(state.isFaceSturdy(server,pos,Direction.UP)&&!com.hbm.block.fluid.LegacyMaterials.of(state).name().equals("sand")&&!Set.of("hbm:dirt_dead","hbm:dirt_oily","hbm:stone_cracked").contains(name))tiltBlocksValid++;
    }
    private boolean refine(ServerLevel server){
        var recipe=RefineryRecipeConfig.find(server.getRecipeManager(),tanks[0].getTankType());
        if(recipe==null){for(int i=1;i<5;i++)tanks[i].setTankType(HbmFluids.NONE);return false;}
        var outputs=recipe.data().outputFluids();for(int i=0;i<4;i++)tanks[i+1].setTankType(outputs.get(i).type);
        if(power.compareTo(BigInteger.valueOf(5))<0||tanks[0].getFill()<100)return false;
        for(int i=0;i<4;i++)if(tanks[i+1].getFill()+outputs.get(i).fill/10>tanks[i+1].getMaxFill())return false;
        isOn=true;tanks[0].setFill(tanks[0].getFill()-100);for(int i=0;i<4;i++)tanks[i+1].setFill(tanks[i+1].getFill()+outputs.get(i).fill/10);
        if(++sulfur>=10){sulfur-=10;var solid=recipe.solid();if(!solid.isEmpty()){if(slots[11].isEmpty())slots[11]=solid.copy();else if(ItemStack.isSameItemSameComponents(slots[11],solid)&&slots[11].getCount()+solid.getCount()<=slots[11].getMaxStackSize())slots[11].grow(solid.getCount());}}
        if(server.getGameTime()%20==0)PollutionSystem.increment(server,worldPosition,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND*5);power=power.subtract(BigInteger.valueOf(5));return true;
    }
    private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(CAPACITY)>0){power=CAPACITY;return;}if(slots[0].getItem() instanceof CreativeBatteryItem){power=CAPACITY;return;}if(slots[0].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(CAPACITY.subtract(power).longValueExact(),battery.getDischargeRate(slots[0])),battery.getCharge(slots[0]));battery.dischargeBattery(slots[0],amount);power=power.add(BigInteger.valueOf(amount));if(amount>0)setChanged();}}
    private void updatePorts(ServerLevel server){
        var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);
        if(!loaded.equals(currentPorts)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var fluidConnections=new ArrayList<NodeConnection>();for(var port:loaded){var connections=Arrays.stream(portSides(worldPosition,port)).map(side->new NodeConnection(port.relative(side),side)).toArray(NodeConnection[]::new);var node=new PowerNode(port).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);fluidConnections.addAll(List.of(connections));}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),fluidConnections.toArray(NodeConnection[]::new));}
        if(fluidPorts!=null)fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long getPower(){return power.longValueExact();}@Override public long getMaxPower(){return 1000;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return CAPACITY;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side!=Direction.DOWN;}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1],tanks[2],tanks[3],tanks[4]};}@Override public FluidTank[] getAllTanks(){return tanks;}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return hasExploded?0:1000000000L;}@Override public long getReceiverSpeed(HbmFluidType type,int pressure){return hasExploded?0:1000000000L;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 1000;}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return isOn ? List.of(new ProgressView("jade.hbm.processing",sulfur,10,0x71C78B)) : List.of();}
    @Override public boolean isDamaged(){return hasExploded;}@Override public void explode(Level level,BlockPos pos){if(!hasExploded){hasExploded=true;onFire=true;setChanged();}}
    @Override public List<GenericIngredient> getRepairMaterials(){return List.of(new GenericIngredient(Ingredient.of(TagKey.create(Registries.ITEM,ResourceLocation.parse("hbm:legacy_ore_dict/platesteel"))),8),new GenericIngredient(Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse("hbm:ducttape"))),4));}
    @Override public void repair(Player player){hasExploded=false;setChanged();}
    @Override public void tryExtinguish(Level level,BlockPos pos,EnumExtinguishType type){if(!hasExploded||!onFire)return;if(type==EnumExtinguishType.FOAM||type==EnumExtinguishType.CO2){onFire=false;setChanged();return;}if(type==EnumExtinguishType.WATER)for(var tank:tanks)if(tank.getFill()>0){level.explode(null,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,5,true,Level.ExplosionInteraction.BLOCK);return;}}
    private void saveState(CompoundTag tag){tag.putString("power",power.toString());String[] names={"input","heavy","naphtha","light","petroleum"};for(int i=0;i<5;i++)tanks[i].writeToNBT(tag,names[i]);tag.putInt("sulfur",sulfur);tag.putBoolean("exploded",hasExploded);tag.putBoolean("onFire",onFire);tag.putBoolean("tilted",tilted);tag.putBoolean("muffled",muffled);}
    private void loadState(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));String[] names={"input","heavy","naphtha","light","petroleum"};for(int i=0;i<5;i++)tanks[i].readFromNBT(tag,names[i]);sulfur=tag.getInt("sulfur");hasExploded=tag.getBoolean("exploded");onFire=tag.getBoolean("onFire");tilted=tag.getBoolean("tilted");muffled=tag.getBoolean("muffled");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var inventory=NonNullList.withSize(13,ItemStack.EMPTY);for(int i=0;i<13;i++)inventory.set(i,slots[i]);ContainerHelper.saveAllItems(tag,inventory,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var inventory=NonNullList.withSize(13,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,inventory,lookup);for(int i=0;i<13;i++)slots[i]=inventory.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);tag.putBoolean("isOn",isOn);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag);isOn=tag.getBoolean("isOn");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    public ItemStack dropStack(){var result=new ItemStack(getBlockState().getBlock());if(Arrays.stream(tanks).anyMatch(tank->tank.getFill()>0)||hasExploded){var data=new CompoundTag();for(int i=0;i<5;i++)tanks[i].writeToNBT(data,""+i);data.putBoolean("hasExploded",hasExploded);data.putBoolean("onFire",onFire);var root=new CompoundTag();root.put("persistent",data);result.set(DataComponents.CUSTOM_DATA,CustomData.of(root));}if(customName!=null)result.set(DataComponents.CUSTOM_NAME,customName);return result;}
    public void restoreItem(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null||!data.copyTag().contains("persistent"))return;var tag=data.copyTag().getCompound("persistent");for(int i=0;i<5;i++)tanks[i].readFromNBT(tag,""+i);hasExploded=tag.getBoolean("hasExploded");onFire=tag.getBoolean("onFire");setChanged();}
    @Override public int getContainerSize(){return 13;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[]{11};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==11;}
    private final ContainerData menuData=new ContainerData(){public int getCount(){return 4;}public int get(int index){return(int)(getPower()>>>(index*16))&65535;}public void set(int index,int value){}};

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
