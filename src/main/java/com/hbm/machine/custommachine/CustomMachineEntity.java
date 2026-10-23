package com.hbm.machine.custommachine;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.HeatSource;
import com.hbm.item.battery.*;
import com.hbm.machine.reactor.*;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.pneumatic.PatternMatcher;
import com.hbm.pollution.*;
import com.hbm.radiation.HbmRadiation;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Source CustomMachine full22-slot state machine, unchanged fixed-order recipe/pollution/flux/heat rules. */
public final class CustomMachineEntity extends BaseContainerBlockEntity implements WorldlyContainer,EnergyReceiver,EnergyProvider,SidedEnergyConnector,StandardFluidTransceiver,MachineView,MuffledMachine {
    public String machineType="";
    public CustomMachinesConfig.MachineConfiguration config;
    private NonNullList<ItemStack> slots=NonNullList.withSize(22,ItemStack.EMPTY);
    public BigInteger power=BigInteger.ZERO;
    public int flux,heat,maxHeat,progress,maxProgress=1,structureCheckDelay;
    public boolean structureOK,muffled,tilted;
    public FluidTank[] inputTanks=FluidTank.EMPTY_ARRAY,outputTanks=FluidTank.EMPTY_ARRAY;
    public final FluidTank smoke=new FluidTank(HbmFluids.SMOKE,100),leaded=new FluidTank(HbmFluids.SMOKE_LEADED,100),poison=new FluidTank(HbmFluids.SMOKE_POISON,100);
    public PatternMatcher matcher=new PatternMatcher(0);
    public CustomMachineRecipe cachedRecipe;
    public final List<BlockPos> fluxPos=new ArrayList<>(),heatPos=new ArrayList<>();
    private List<BlockPos> ports=List.of(),firstPorts;
    private final Map<BlockPos,PowerNode> powerNodes=new LinkedHashMap<>();
    private final Map<HbmFluidType,FluidNode> fluidNodes=new IdentityHashMap<>();
    public CustomMachineEntity(BlockPos pos,BlockState state){super(CustomMachineContent.ENTITY.get(),pos,state);}
    public void init(CustomMachinesConfig.MachineConfiguration config){this.config=config;inputTanks=new FluidTank[config.fluidInCount];for(int i=0;i<inputTanks.length;i++)inputTanks[i]=new FluidTank(HbmFluids.NONE,config.fluidInCap);outputTanks=new FluidTank[config.fluidOutCount];for(int i=0;i<outputTanks.length;i++)outputTanks[i]=new FluidTank(HbmFluids.NONE,config.fluidOutCap);maxHeat=config.maxHeat;matcher=new PatternMatcher(config.itemInCount);for(var tank:smokeTanks())tank.changeTankSize(config.maxPollutionCap);}
    public void tickServer(){if(!(level instanceof ServerLevel server))return;if(config==null){server.destroyBlock(worldPosition,false);return;}if(structureOK)updatePorts(server);chargeBattery();var items=slots.toArray(ItemStack[]::new);for(int i=0;i<Math.min(3,inputTanks.length);i++)inputTanks[i].setType(1+i,items);structureCheckDelay--;if(structureCheckDelay<=0)checkStructure();if(server.getGameTime()%20==0){for(var side:new Direction[]{Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST}){for(var port:fluxPos){var pos=port.relative(side);var state=server.getBlockState(pos);if(state.getBlock()instanceof ResearchReactorBlock reactor){var core=reactor.findCore(server,pos);if(core!=null&&server.getBlockEntity(core)instanceof ResearchReactorBlockEntity actual)flux=actual.totalFlux;}}if(config.maxHeat>0)for(var pos:heatPos)pullHeat(pos.relative(side).below());}}
        if(structureOK){if(config.generatorMode){if(cachedRecipe==null){var recipe=getMatchingRecipe();if(recipe!=null&&hasRequiredQuantities(recipe)&&hasSpace(recipe)){cachedRecipe=recipe;useUpInput(recipe);}}if(cachedRecipe!=null){maxProgress=duration(cachedRecipe);int amount=consumption(cachedRecipe);progress++;power=power.add(BigInteger.valueOf(amount));heat-=cachedRecipe.heat();power=power.min(config.maxPower);if(server.getGameTime()%20==0)emissions(server,cachedRecipe);if(progress>=maxProgress){progress=0;processRecipe(cachedRecipe);cachedRecipe=null;}}}else{var recipe=getMatchingRecipe();if(recipe!=null){maxProgress=duration(recipe);int amount=consumption(recipe);if(power.compareTo(BigInteger.valueOf(amount))>=0&&hasRequiredQuantities(recipe)&&hasSpace(recipe)){progress++;power=power.subtract(BigInteger.valueOf(amount));heat-=recipe.heat();if(server.getGameTime()%20==0)emissions(server,recipe);if(progress>=maxProgress){progress=0;useUpInput(recipe);processRecipe(recipe);}}}else progress=0;}}else progress=0;
        setChanged();var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);
    }
    private int duration(CustomMachineRecipe recipe){return (int)Math.max(recipe.duration()/config.recipeSpeedMult,1);}
    private int consumption(CustomMachineRecipe recipe){return (int)Math.max(recipe.power()*config.recipeConsumptionMult,1);}
    private List<CustomMachineRecipe> recipes(){return level==null||config==null?List.of():CustomMachineRecipes.recipes(level.getRecipeManager(),config.recipeKey);}
    public CustomMachineRecipe getMatchingRecipe(){outer:for(var recipe:recipes()){for(int i=0;i<recipe.inputFluids().size();i++){var input=recipe.inputFluids().get(i);if(inputTanks[i].getTankType()!=input.type||inputTanks[i].getPressure()!=input.pressure)continue outer;}for(int i=0;i<recipe.inputItems().size();i++)if(!recipe.inputItems().get(i).matches(slots.get(i+4),true))continue outer;return recipe;}return null;}
    public boolean hasRequiredQuantities(CustomMachineRecipe recipe){for(int i=0;i<recipe.inputFluids().size();i++)if(inputTanks[i].getFill()<recipe.inputFluids().get(i).fill)return false;for(int i=0;i<recipe.inputItems().size();i++)if(!slots.get(i+4).isEmpty()&&slots.get(i+4).getCount()<recipe.inputItems().get(i).count())return false;if(config.fluxMode&&flux<recipe.flux())return false;return !(config.maxHeat>0&&recipe.heat()>0&&heat<recipe.heat());}
    public boolean hasSpace(CustomMachineRecipe recipe){for(int i=0;i<recipe.outputFluids().size();i++){var output=recipe.outputFluids().get(i);if(outputTanks[i].getTankType()==output.type&&outputTanks[i].getFill()+output.fill>outputTanks[i].getMaxFill())return false;}for(int i=0;i<recipe.outputItems().size();i++){var current=slots.get(16+i);var output=recipe.outputItems().get(i).pool().getFirst().stack();if(!current.isEmpty()&&(!com.hbm.pneumatic.SourcePatternItems.sameExact(current,output)||current.getCount()+output.getCount()>current.getMaxStackSize()))return false;}return true;}
    public void useUpInput(CustomMachineRecipe recipe){for(int i=0;i<recipe.inputFluids().size();i++)inputTanks[i].setFill(inputTanks[i].getFill()-recipe.inputFluids().get(i).fill);for(int i=0;i<recipe.inputItems().size();i++)removeItem(i+4,recipe.inputItems().get(i).count());}
    public void processRecipe(CustomMachineRecipe recipe){for(int i=0;i<recipe.outputFluids().size();i++){var output=recipe.outputFluids().get(i);outputTanks[i].setTankType(output.type);outputTanks[i].setFill(outputTanks[i].getFill()+output.fill);}for(int i=0;i<recipe.outputItems().size();i++){var output=recipe.outputItems().get(i).pool().getFirst();if(level.random.nextFloat()<output.chance()){if(slots.get(i+16).isEmpty())slots.set(i+16,output.stack().copy());else slots.get(i+16).grow(output.stack().getCount());}}}
    private void emissions(ServerLevel server,CustomMachineRecipe recipe){if(recipe.pollutionAmount()!=0){var type=PollutionType.valueOf(recipe.pollutionType());if(recipe.pollutionAmount()>0){var tank=type==PollutionType.SOOT?smoke:type==PollutionType.HEAVYMETAL?leaded:poison;tank.setFill(tank.getFill()+(int)Math.ceil(recipe.pollutionAmount()*100));if(tank.getFill()>tank.getMaxFill()){int overflow=tank.getFill()-tank.getMaxFill();tank.setFill(tank.getMaxFill());PollutionSystem.increment(server,worldPosition,type,overflow/100F);if(server.random.nextInt(3)==0)server.playSound(null,worldPosition,SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,.1F,1.5F);}}else if(PollutionSystem.get(server,worldPosition,type)>=-recipe.pollutionAmount())PollutionSystem.decrement(server,worldPosition,type,-recipe.pollutionAmount());}if(recipe.radiationAmount()>0)HbmRadiation.incrementRad(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),recipe.radiationAmount());else if(recipe.radiationAmount()<0)HbmRadiation.decrementRad(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),-recipe.radiationAmount());}
    private void pullHeat(BlockPos pos){if(level.getBlockEntity(pos)instanceof HeatSource source){int difference=source.getHeatStored()-heat;if(difference>0){source.useUpHeat(difference);heat+=difference;if(heat>maxHeat)heat=maxHeat;}}}
    public boolean checkStructure(){structureCheckDelay=300;structureOK=false;if(config==null||level==null)return false;var found=new ArrayList<BlockPos>();for(var component:config.components){var pos=component.at(worldPosition,getBlockState().getValue(CMComponentBlock.META));if(!component.matches(level.getBlockState(pos)))return false;var entity=level.getBlockEntity(pos);if(entity instanceof CMPortEntity proxy){proxy.bind(worldPosition);found.add(pos);}else if(com.hbm.multiblock.proxy.SourceProxyBindings.bind(entity,worldPosition))found.add(pos);if(level.getBlockState(pos).is(CustomMachineContent.COMPONENTS.get(CMComponentBlock.Kind.FLUX).get()))for(var side:Direction.values())fluxPos.add(pos.relative(side));else if(level.getBlockState(pos).is(CustomMachineContent.COMPONENTS.get(CMComponentBlock.Kind.HEAT).get()))for(var side:Direction.values())heatPos.add(pos.relative(side));}found.add(worldPosition);ports=List.copyOf(found);structureOK=true;return true;}
    public void buildStructure(){if(config==null||level==null)return;for(var component:config.components)level.setBlock(component.at(worldPosition,getBlockState().getValue(CMComponentBlock.META)),component.state(component.firstMetadata()),3);}
    private void chargeBattery(){var stack=slots.get(0);if(stack.getItem()instanceof CreativeBatteryItem){power=config.maxPower;return;}if(stack.getItem()instanceof HbmBattery battery){long amount=config.maxPower.subtract(power).max(BigInteger.ZERO).min(BigInteger.valueOf(battery.getDischargeRate(stack))).min(BigInteger.valueOf(battery.getCharge(stack))).longValueExact();battery.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));}}
    private FluidTank[] smokeTanks(){return new FluidTank[]{smoke,leaded,poison};}
    private boolean smokeType(HbmFluidType type){return type==HbmFluids.SMOKE||type==HbmFluids.SMOKE_LEADED||type==HbmFluids.SMOKE_POISON;}
    private void updatePorts(ServerLevel server){if(firstPorts==null)firstPorts=ports;for(var pos:firstPorts){var node=powerNodes.get(pos);if(node==null||node.isExpired()){node=new PowerNode(pos);HbmEnergy.nodespace(server).createNode(node);powerNodes.put(pos,node);}if(node.hasValidNet()){/* Original opposite registration is deliberate: PIFIFO for generator, POFIFO for consumer. */if(config.generatorMode)node.addReceiver(this);else node.addProvider(this);}}
        var types=Collections.newSetFromMap(new IdentityHashMap<HbmFluidType,Boolean>());for(var tank:getReceivingTanks())if(tank.getTankType()!=HbmFluids.NONE)types.add(tank.getTankType());for(var tank:getSendingTanks())if(tank.getTankType()!=HbmFluids.NONE)types.add(tank.getTankType());for(var type:List.copyOf(fluidNodes.keySet()))if(!types.contains(type)){FluidNetworks.nodespace(server,type).destroyNode(fluidNodes.remove(type));}for(var type:types){var positions=new LinkedHashSet<>(firstPorts);if(smokeType(type))positions.addAll(ports);var node=fluidNodes.get(type);if(node==null||node.isExpired()||!node.positions().equals(positions)){if(node!=null)FluidNetworks.nodespace(server,type).destroyNode(node);node=new FluidNode(positions.toArray(BlockPos[]::new));FluidNetworks.nodespace(server,type).createNode(node);fluidNodes.put(type,node);}if(node.hasValidNet()){if(Arrays.stream(getReceivingTanks()).anyMatch(t->t.getTankType()==type))node.addReceiver(this);if(Arrays.stream(getSendingTanks()).anyMatch(t->t.getTankType()==type))node.addProvider(this);}}}
    @Override public FluidTank[] getAllTanks(){var result=new FluidTank[inputTanks.length+outputTanks.length];System.arraycopy(inputTanks,0,result,0,inputTanks.length);System.arraycopy(outputTanks,0,result,inputTanks.length,outputTanks.length);return result;}
    @Override public FluidTank[] getReceivingTanks(){return inputTanks;}
    @Override public FluidTank[] getSendingTanks(){var result=Arrays.copyOf(outputTanks,outputTanks.length+3);System.arraycopy(smokeTanks(),0,result,outputTanks.length,3);return result;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
    @Override public boolean isLoaded(){return level!=null&&!isRemoved()&&level.hasChunkAt(worldPosition);}
    @Override public BigInteger getPowerExact(){return power;}
    @Override public BigInteger getMaxPowerExact(){return config==null?BigInteger.ONE:config.maxPower;}
    @Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public long getPower(){return power.min(BigInteger.valueOf(Long.MAX_VALUE)).max(BigInteger.valueOf(Long.MIN_VALUE)).longValue();}
    @Override public long getMaxPower(){return getMaxPowerExact().min(BigInteger.valueOf(Long.MAX_VALUE)).max(BigInteger.valueOf(Long.MIN_VALUE)).longValue();}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger transferPowerExact(BigInteger amount){if(config!=null&&config.generatorMode)return amount;power=power.add(amount);if(power.compareTo(getMaxPowerExact())>0){var overshoot=power.subtract(getMaxPowerExact());power=getMaxPowerExact();return overshoot;}setChanged();return BigInteger.ZERO;}
    @Override public long transferPower(long value){return transferPowerExact(BigInteger.valueOf(value)).longValueExact();}
    @Override public BigInteger getReceiverSpeedExact(){return config!=null&&!config.generatorMode?getMaxPowerExact():BigInteger.ZERO;}
    @Override public BigInteger getProviderSpeedExact(){return config!=null&&config.generatorMode?getMaxPowerExact():BigInteger.ZERO;}
    @Override public long getReceiverSpeed(){return getReceiverSpeedExact().min(BigInteger.valueOf(Long.MAX_VALUE)).max(BigInteger.valueOf(Long.MIN_VALUE)).longValue();}
    @Override public long getProviderSpeed(){return getProviderSpeedExact().min(BigInteger.valueOf(Long.MAX_VALUE)).max(BigInteger.valueOf(Long.MIN_VALUE)).longValue();}
    @Override protected NonNullList<ItemStack> getItems(){return slots;}
    @Override protected void setItems(NonNullList<ItemStack> value){slots=value;}
    @Override public int getContainerSize(){return 22;}
    @Override protected Component getDefaultName(){return Component.literal(config==null?"INVALID":config.localizedName);}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return config==null?null:new CustomMachineMenu(id,inventory,this);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);buffer.writeUtf(machineType);buffer.writeUtf(config.json.toString(),1048576);}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public int[] getSlotsForFace(Direction side){if(config==null)return new int[0];int count=Math.max(0,Math.min(6,config.itemInCount));int[] result=new int[count+6];for(int i=0;i<count;i++)result[i]=4+i;for(int i=0;i<6;i++)result[count+i]=16+i;return result;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){if(slot<4||slot>9)return false;var filter=slots.get(slot+6);return filter.isEmpty()||matcher.isValidForFilter(filter,slot-4,stack);}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=16&&slot<=21;}
    private void saveTanks(CompoundTag tag){for(int i=0;i<inputTanks.length;i++)inputTanks[i].writeToNBT(tag,"i"+i);for(int i=0;i<outputTanks.length;i++)outputTanks[i].writeToNBT(tag,"o"+i);for(int i=0;i<3;i++)smokeTanks()[i].writeToNBT(tag,"smoke"+i);matcher.write(tag);}
    private void loadTanks(CompoundTag tag){for(int i=0;i<inputTanks.length;i++)inputTanks[i].readFromNBT(tag,"i"+i);for(int i=0;i<outputTanks.length;i++)outputTanks[i].readFromNBT(tag,"o"+i);for(int i=0;i<3;i++)smokeTanks()[i].readFromNBT(tag,"smoke"+i);matcher.read(tag);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);var items=new ListTag();for(int i=0;i<22;i++)if(!slots.get(i).isEmpty()){var item=(CompoundTag)slots.get(i).save(lookup);item.putByte("slot",(byte)i);items.add(item);}tag.put("items",items);if(getCustomName()!=null)tag.putString("name",getCustomName().getString());if(machineType==null||config==null)return;tag.putString("machineType",machineType);saveTanks(tag);tag.putInt("cachedIndex",cachedRecipe==null?-1:recipes().indexOf(cachedRecipe));}
    /** Feed the old literal name through the native private-name loader; native component names win. */
    private static CompoundTag nativeNameTag(CompoundTag tag,HolderLookup.Provider lookup){
        if(tag.contains("CustomName",Tag.TAG_STRING)||!tag.contains("name",Tag.TAG_STRING)||tag.getString("name").isEmpty())return tag;
        var nativeTag=tag.copy();
        nativeTag.putString("CustomName",Component.Serializer.toJson(Component.literal(tag.getString("name")),lookup));
        return nativeTag;
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){machineType=tag.getString("machineType");var definition=CustomMachinesConfig.customMachines.get(machineType);if(definition!=null)init(definition);super.loadAdditional(nativeNameTag(tag,lookup),lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");var items=tag.getList("items",Tag.TAG_COMPOUND);for(var value:items){var item=(CompoundTag)value;int slot=item.getByte("slot");if(slot>=0&&slot<22)slots.set(slot,ItemStack.parseOptional(lookup,item));}if(config!=null){loadTanks(tag);pendingCachedIndex=tag.getInt("cachedIndex");}}
    private int pendingCachedIndex=-1;
    @Override public void onLoad(){super.onLoad();if(level!=null&&!level.isClientSide&&config!=null&&pendingCachedIndex!=-1){cachedRecipe=recipes().get(pendingCachedIndex);pendingCachedIndex=-1;}}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putString("machineType",machineType==null?"":machineType);if(config!=null)tag.putString("definition",config.json.toString());tag.putString("power",power.toString());tag.putInt("progress",progress);tag.putInt("flux",flux);tag.putInt("heat",heat);tag.putBoolean("structureOK",structureOK);tag.putInt("maxProgress",maxProgress);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);saveTanks(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){machineType=tag.getString("machineType");if(config==null&&tag.contains("definition"))init(new CustomMachinesConfig.MachineConfiguration(com.google.gson.JsonParser.parseString(tag.getString("definition")).getAsJsonObject()));power=new BigInteger(tag.getString("power"));progress=tag.getInt("progress");flux=tag.getInt("flux");heat=tag.getInt("heat");structureOK=tag.getBoolean("structureOK");maxProgress=tag.getInt("maxProgress");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");loadTanks(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public long energyStored(){return power.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long energyCapacity(){return getMaxPowerExact().min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.custom_machine.progress",progress,maxProgress,0xffd090));}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:powerNodes.values())HbmEnergy.nodespace(server).destroyNode(node);fluidNodes.forEach((type,node)->FluidNetworks.nodespace(server,type).destroyNode(node));}powerNodes.clear();fluidNodes.clear();firstPorts=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
