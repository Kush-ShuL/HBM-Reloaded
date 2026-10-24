package com.hbm.machine.plasmaforge;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.multiblock.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original fusion plasma forge: real12 positional inputs, plasma ignition, booster and native HE/fluid ports. */
public final class PlasmaForgeBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver,MachineView,com.hbm.fusion.plasma.FusionPowerReceiver,api.hbm.redstoneoverradio.IRORValueProvider, com.hbm.multiblock.MuffledMachine {
    public static Consumer<PlasmaForgeBlockEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{},CLIENT_STOP=machine->{};
    public final ItemStack[] slots=new ItemStack[16];public final FluidTank inputTank=new FluidTank(HbmFluids.NONE,16000);
    public long power,maxPower=10000000,plasmaEnergy,plasmaEnergySync;
    public double neutronEnergy;
    public float plasmaRed,plasmaGreen,plasmaBlue;
    public boolean didProcess,connected,muffled;
    public int booster,maxBooster;
    private Component customName;
    private com.hbm.fusion.plasma.PlasmaNode receiverNode,providerNode;
    public record Booster(GenericIngredient ingredient,int ticks){}
    public static final List<Booster> boosters=createBoosters();
    private static List<Booster> createBoosters(){var list=new ArrayList<Booster>();String[] names={"c:nuggets/cobalt_60","hbm:billets/cobalt_60","c:ingots/cobalt_60","c:dusts/cobalt_60","c:nuggets/strontium_90","hbm:dusts/tiny/strontium_90","hbm:billets/strontium_90","c:ingots/strontium_90","c:dusts/strontium_90","c:nuggets/gold_198","hbm:billets/gold_198","c:ingots/gold_198","c:dusts/gold_198","hbm:dusts/tiny/iodine_131","c:dusts/iodine_131","hbm:dusts/tiny/xenon_135","c:dusts/xenon_135","hbm:dusts/tiny/caesium_137","c:dusts/caesium_137","c:dusts/astatine_209"};int[] times={20,120,200,200,40,40,240,400,400,60,360,600,600,60,600,60,600,50,500,1200};for(int i=0;i<names.length;i++)list.add(new Booster(new GenericIngredient(com.hbm.registry.HbmItemTags.ingredient(names[i]),1),times[i]));return List.copyOf(list);}
        private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();
    public final GenericMachineProcessor module=new GenericMachineProcessor(this,3,12,15,1,new FluidTank[]{inputTank},FluidTank.EMPTY_ARRAY,this,this::recipes){
        @Override public void setupTanks(GenericMachineRecipe recipe){super.setupTanks(recipe);if(recipe!=null&&!recipe.inputFluids().isEmpty())inputTank.changeTankSize(Math.max(Math.max(inputTank.getFill(),recipe.inputFluids().getFirst().fill*2),16000));}
    };
    public PlasmaForgeBlockEntity(BlockPos pos,BlockState state){super(PlasmaForgeContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public List<PlasmaForgeRecipe> recipes(){return level==null?List.of():PlasmaForgeRecipeConfig.recipes(level.getRecipeManager());}
    public boolean processing(){return didProcess;}public FluidTank inputTank(){return inputTank;}
    public float volume(float base){return muffled?base*.1F:base;}
    @Override public void setCustomName(Component name){customName=name;setChanged();}@Override public Component getDisplayName(){return customName==null?Component.translatable("container.machinePlasmaForge"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new PlasmaForgeMenu(id,inventory,this);}
    public void selectRecipe(int index,String selection){if(index!=0)return;module.setRecipe(selection,false);setChanged();}
    public PlasmaForgeRecipe recipe(){return module.getRecipe() instanceof PlasmaForgeRecipe recipe?recipe:null;}
    @Override public boolean receivesFusionPower(){return true;}
    @Override public void receiveFusionPower(long fusionPower,double neutronPower,float red,float green,float blue){plasmaEnergy=fusionPower;neutronEnergy=neutronPower;plasmaRed=red;plasmaGreen=green;plasmaBlue=blue;}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"progress",PREFIX_VALUE+"recipe",PREFIX_VALUE+"active",PREFIX_VALUE+"booster",PREFIX_VALUE+"plasma"};}
    @Override public String provideRORValue(String name){if(name==null)return null;if(name.equals(PREFIX_VALUE+"progress"))return ""+(int)Math.round(module.progress*100);if(name.equals(PREFIX_VALUE+"recipe"))return module.getRecipeName();if(name.equals(PREFIX_VALUE+"active"))return didProcess?"1":"0";if(name.equals(PREFIX_VALUE+"booster"))return ""+booster;if(name.equals(PREFIX_VALUE+"plasma"))return ""+plasmaEnergy;return null;}
    public static BlockPos[] portPositions(BlockPos core,Direction facing){var result=new ArrayList<BlockPos>();var rot=facing.getClockWise();for(int sign:new int[]{1,-1})for(int shift=-2;shift<=2;shift++)result.add(core.relative(facing,sign*5).relative(rot,shift));return result.toArray(BlockPos[]::new);}
    public static Direction[] portSides(BlockPos core,BlockPos port,Direction facing){return new Direction[]{(port.getX()-core.getX())*facing.getStepX()+(port.getZ()-core.getZ())*facing.getStepZ()>0?facing:facing.getOpposite()};}
    public PowerNode nodeAt(BlockPos port){return nodes.get(port);}
    public static void tick(Level level,BlockPos pos,BlockState state,PlasmaForgeBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
    private void serverTick(ServerLevel server){
        if(maxPower<=0)maxPower=1000000;updatePorts(server);
        plasmaEnergySync=plasmaEnergy;plasmaEnergy=0;
        if(booster<=0&&!slots[2].isEmpty())for(var fuel:boosters)if(fuel.ingredient.matches(slots[2],true)){maxBooster=booster=fuel.ticks;removeItem(2,1);break;}
        var rot=DummyableBlock.facing(getBlockState()).getClockWise();
        if(receiverNode==null||receiverNode.isExpired())receiverNode=createPlasmaNode(server,rot);
        if(providerNode==null||providerNode.isExpired())providerNode=createPlasmaNode(server,rot.getOpposite());
        receiverNode.addReceiver(this);providerNode.addProvider(this);
        var recipe=recipe();if(recipe!=null)maxPower=recipe.power()*100;maxPower=Math.max(Math.max(power,maxPower),100000);chargeBattery();
        boolean ignition=recipe==null||recipe.ignitionTemp()<=plasmaEnergySync;module.update(booster>0?4D:1D,1D,ignition,slots[1]);didProcess=module.didProcess;if(module.markDirty)setChanged();
        long forwarded=(long)Math.ceil(plasmaEnergySync*.75D);connected=providerNode.hasValidNet()&&!providerNode.network().receivers.isEmpty();if(providerNode.hasValidNet()&&forwarded>0)for(var receiver:List.copyOf(providerNode.network().receivers))receiver.receiveFusionPower(forwarded,neutronEnergy,plasmaRed,plasmaGreen,plasmaBlue);
        if(didProcess&&booster>0)booster--;neutronEnergy=0;
        var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);
    }
    private com.hbm.fusion.plasma.PlasmaNode createPlasmaNode(ServerLevel server,Direction direction){var space=com.hbm.fusion.plasma.PlasmaNetworks.nodespace(server);var pos=worldPosition.relative(direction,5).above(2);var existing=space.getNode(pos);if(existing!=null)return existing;var node=new com.hbm.fusion.plasma.PlasmaNode(pos).setConnections(new NodeConnection(pos.relative(direction),direction));space.createNode(node);return node;}
    private void chargeBattery(){if(power<0){power=0;setChanged();return;}if(power>maxPower){power=maxPower;setChanged();return;}if(slots[0].getItem() instanceof CreativeBatteryItem){power=maxPower;setChanged();return;}if(slots[0].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(maxPower-power,battery.getDischargeRate(slots[0])),battery.getCharge(slots[0]));battery.dischargeBattery(slots[0],amount);power+=amount;if(amount!=0)setChanged();}}
    private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition,DummyableBlock.facing(getBlockState())))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);if(!currentPorts.equals(loaded)||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();currentPorts=List.copyOf(loaded);var fluidConnections=new ArrayList<NodeConnection>();for(var port:loaded){var connections=Arrays.stream(portSides(worldPosition,port,DummyableBlock.facing(getBlockState()))).map(side->new NodeConnection(port.relative(side),side)).toArray(NodeConnection[]::new);PowerNode node=new PowerNode(port).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port,node);fluidConnections.addAll(List.of(connections));}if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),fluidConnections.toArray(NodeConnection[]::new));}for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);if(fluidPorts!=null)fluidPorts.update(server,this);}
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;currentPorts=List.of();}
    private void disconnectPlasma(){if(level instanceof ServerLevel server){var space=com.hbm.fusion.plasma.PlasmaNetworks.nodespace(server);space.destroyNode(receiverNode);space.destroyNode(providerNode);}receiverNode=null;providerNode=null;}
    @Override public void onChunkUnloaded(){disconnectPlasma();disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}@Override public void setRemoved(){disconnectPlasma();disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}
    @Override public long getPower(){return power;}@Override public long getMaxPower(){return maxPower;}@Override public void setPower(long value){power=value;setChanged();}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}@Override public FluidTank[] getAllTanks(){return new FluidTank[]{inputTank};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{inputTank};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remaining=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(remaining!=amount)setChanged();return remaining;}
    @Override public java.util.List<ProgressView> progressBars() { if (!(module.progress > 0 || didProcess)) return java.util.List.of(); return java.util.List.of(new ProgressView("jade.hbm.processing",module.progress,1,0x71C78B)); }
    @Override public long energyStored(){return power;}@Override public long energyCapacity(){return maxPower;}@Override public List<TankView> tanks(){return List.of(new TankView(inputTank.getTankType(),inputTank.getFill(),inputTank.getMaxFill()));}
    private void saveState(CompoundTag tag){inputTank.writeToNBT(tag,"i");tag.putInt("booster",booster);tag.putInt("maxBooster",maxBooster);tag.putLong("power",power);tag.putLong("maxPower",maxPower);tag.putDouble("progress0",module.progress);tag.putString("recipe0",module.recipeName);tag.putBoolean("restrictedMode",module.restrictedMode);tag.putBoolean("muffled",muffled);}
    private void loadState(CompoundTag tag){inputTank.readFromNBT(tag,"i");booster=tag.getInt("booster");maxBooster=tag.getInt("maxBooster");power=tag.getLong("power");maxPower=tag.getLong("maxPower");module.progress=tag.getDouble("progress0");module.recipeName=tag.contains("recipe0")?tag.getString("recipe0"):"null";module.restrictedMode=tag.getBoolean("restrictedMode");muffled=tag.getBoolean("muffled");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var items=NonNullList.withSize(16,ItemStack.EMPTY);for(int i=0;i<16;i++)items.set(i,slots[i]);ContainerHelper.saveAllItems(tag,items,lookup);saveState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var items=NonNullList.withSize(16,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,items,lookup);for(int i=0;i<16;i++)slots[i]=items.get(i);loadState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag);tag.putBoolean("didProcess",didProcess);tag.putBoolean("connected",connected);tag.putLong("plasmaEnergySync",plasmaEnergySync);tag.putFloat("plasmaRed",plasmaRed);tag.putFloat("plasmaGreen",plasmaGreen);tag.putFloat("plasmaBlue",plasmaBlue);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){boolean was=didProcess;loadState(tag);didProcess=tag.getBoolean("didProcess");connected=tag.getBoolean("connected");plasmaEnergySync=tag.getLong("plasmaEnergySync");plasmaRed=tag.getFloat("plasmaRed");plasmaGreen=tag.getFloat("plasmaGreen");plasmaBlue=tag.getFloat("plasmaBlue");if(was&&!didProcess)CLIENT_STOP.accept(this);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 16;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){if(slot==0)return true;if(slot==1&&stack.getItem()==Blueprints.ITEM.get())return true;if(module.isItemValid(slot,stack))return true;if(slot==2)for(var fuel:boosters)if(fuel.ingredient.matches(stack,true))return true;return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{2,3,4,5,6,7,8,9,10,11,12,13,14,15};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==15||module.isSlotClogged(slot);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
