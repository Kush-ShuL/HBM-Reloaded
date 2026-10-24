package com.hbm.machine.arcwelder;

import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.multiblock.*;
import java.util.*;
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

/** Actual eight-slot arc welder: three unordered whole-stack inputs, original upgrade arithmetic and tick order. */
public final class ArcWelderBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver,MachineView,IFluidCopiable,MuffledMachine ,com.hbm.multiblock.proxy.SourceProxyInventoryAccess {
    private final ItemStack[] slots=new ItemStack[8];
    public long power,maxPower=2000,consumption;
    public int progress,processTime=1;
    public boolean muffled,tilted,processing;
    public final FluidTank tank=new FluidTank(HbmFluids.NONE,24000);
    public ItemStack display=ItemStack.EMPTY;
    private ArcWelderRecipe recipe;
    private Component customName;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
    private FluidPorts fluidPorts;
    public ArcWelderBlockEntity(BlockPos pos,BlockState state){super(ArcWelderContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public record Port(BlockPos pos,Direction... sides){}
    /** Six independent original nodes with ten actual outward edges, source UP rotation is clockwise. */
    public static Port[] ports(BlockPos p,Direction d){var r=d.getClockWise();return new Port[]{new Port(p,d),new Port(p.relative(r),d,r),new Port(p.relative(r,-1),d,r.getOpposite()),new Port(p.relative(d,-1),d.getOpposite()),new Port(p.relative(d,-1).relative(r),d.getOpposite(),r),new Port(p.relative(d,-1).relative(r,-1),d.getOpposite(),r.getOpposite())};}
    public int[] slotsAt(BlockPos pos){var d=DummyableBlock.facing(getBlockState());var r=d.getClockWise();if(pos.equals(worldPosition.relative(r))||pos.equals(worldPosition.relative(r,-1).relative(d,-1)))return new int[]{0,3};if(pos.equals(worldPosition.relative(d,-1)))return new int[]{1,3};if(pos.equals(worldPosition.relative(r,-1))||pos.equals(worldPosition.relative(r).relative(d,-1)))return new int[]{2,3};return new int[]{};}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.machineArcWelder"):customName;}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory player,Player opener){return new ArcWelderMenu(id,player,this,worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,ArcWelderBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    private void serverTick(ServerLevel server){
        updatePorts(server);chargeBattery();tank.setType(5,slots);
        recipe=ArcWelderRecipes.recipes(server.getRecipeManager()).stream().filter(value->value.accepts(this::getItem)).findFirst().orElse(null);
        int speed=0,saving=0,overdrive=0;
        for(int i=6;i<=7;i++){var item=slots[i].getItem();if(item instanceof FurnaceUpgradeItem upgrade){if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED)speed+=upgrade.tier();else saving+=upgrade.tier();}else if(item instanceof OverdriveUpgradeItem upgrade)overdrive+=upgrade.tier();}
        speed=Math.min(speed,3);saving=Math.min(saving,3);overdrive=Math.min(overdrive,3);long intendedMaxPower;processing=false;
        if(recipe!=null){
            processTime=recipe.duration()-recipe.duration()*speed/6+recipe.duration()*saving/3;
            consumption=recipe.power()+recipe.power()*speed-recipe.power()*saving/6;
            consumption*=Math.pow(2,overdrive);intendedMaxPower=consumption*20;
            if(canProcess(recipe)){
                progress+=1+overdrive;power-=consumption;processing=true;
                if(progress>=processTime){progress=0;consume(recipe);if(slots[3].isEmpty())slots[3]=recipe.output().copy();else slots[3].grow(recipe.output().getCount());setChanged();}
                if(server.getGameTime()%2==0){var d=DummyableBlock.facing(getBlockState());var packet=new com.hbm.item.tool.WeldingParticlePacket(worldPosition.getX()+.5-d.getStepX()*.5,worldPosition.getY()+1.25,worldPosition.getZ()+.5-d.getStepZ()*.5,server.getGameTime()%20==0?5:0,false);for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625)net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,packet);}
            }else progress=0;
        }else{progress=0;consumption=100;intendedMaxPower=2000;}
        maxPower=Math.max(intendedMaxPower,power);display=ItemStack.EMPTY;var displayRecipe=ArcWelderRecipes.recipes(server.getRecipeManager()).stream().filter(value->value.accepts(this::getItem)).findFirst().orElse(null);if(displayRecipe!=null){var output=displayRecipe.output();display=new ItemStack(output.getItem());if(output.has(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get()))display.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),output.get(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get()));else if(output.isDamageableItem())display.setDamageValue(output.getDamageValue());}
        setChanged();var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625)player.connection.send(packet);dispatchScope.commit();}
    }
    public boolean canProcess(ArcWelderRecipe recipe){
        if(power<consumption)return false;var fluid=recipe.fluid();if(fluid!=null&&(tank.getTankType()!=fluid.type||tank.getFill()<fluid.fill))return false;
        
        return slots[3].isEmpty()||sameLegacy(slots[3],recipe.output())&&slots[3].getCount()+recipe.output().getCount()<=slots[3].getMaxStackSize();
    }
    private void consume(ArcWelderRecipe recipe){consumeLane(recipe.inputItems(),0,3);if(recipe.fluid()!=null)tank.setFill(tank.getFill()-recipe.fluid().fill);}
    private void consumeLane(List<com.hbm.recipe.generic.GenericIngredient> ingredients,int start,int end){for(var ingredient:ingredients)for(int slot=start;slot<end;slot++)if(ingredient.matches(slots[slot],false)){removeItem(slot,ingredient.count());break;}}
    public static boolean sameLegacy(ItemStack a,ItemStack b){return a.is(b.getItem())&&a.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),b.getDamageValue()));}
    private void chargeBattery(){power=getPower();if(slots[4].getItem() instanceof CreativeBatteryItem){power=maxPower;return;}if(slots[4].getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(maxPower-power,battery.getDischargeRate(slots[4])),battery.getCharge(slots[4]));power+=amount;battery.dischargeBattery(slots[4],amount);}}
    private void updatePorts(ServerLevel server){
        if(nodes.isEmpty()||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();var positions=new ArrayList<BlockPos>();var edges=new ArrayList<NodeConnection>();for(var port:ports(worldPosition,DummyableBlock.facing(getBlockState()))){var connections=Arrays.stream(port.sides()).map(side->new NodeConnection(port.pos().relative(side),side)).toArray(NodeConnection[]::new);var node=new PowerNode(port.pos()).setConnections(connections);HbmEnergy.nodespace(server).createNode(node);nodes.put(port.pos(),node);positions.add(port.pos());edges.addAll(Arrays.asList(connections));}fluidPorts=new FluidPorts(positions.toArray(BlockPos[]::new),edges.toArray(NodeConnection[]::new));}
        fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public long getPower(){return Math.max(Math.min(power,maxPower),0);}@Override public long getMaxPower(){return maxPower;}@Override public void setPower(long value){power=value;setChanged();}
    @Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return getAllTanks();}@Override public FluidTank getTankToPaste(){return tank;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return maxPower;}@Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    @Override public List<ProgressView> progressBars(){return recipe!=null&&(processing||progress>0)?List.of(new ProgressView("jade.hbm.arcwelder.processing",progress,Math.max(processTime,1),0x71C78B)):List.of();}
    private void writeState(CompoundTag tag){tag.putLong("power",power);tag.putLong("maxPower",maxPower);tag.putLong("consumption",consumption);tag.putInt("progress",progress);tag.putInt("processTime",processTime);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tank.writeToNBT(tag,"t");}
    private void readState(CompoundTag tag){power=tag.getLong("power");maxPower=tag.getLong("maxPower");consumption=tag.getLong("consumption");progress=tag.getInt("progress");processTime=tag.getInt("processTime");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");tank.readFromNBT(tag,"t");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=NonNullList.withSize(8,ItemStack.EMPTY);for(int i=0;i<8;i++)list.set(i,slots[i]);ContainerHelper.saveAllItems(tag,list,lookup);writeState(tag);tag.remove("consumption");if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var list=NonNullList.withSize(8,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,list,lookup);for(int i=0;i<8;i++)slots[i]=list.get(i);readState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeState(tag);tag.putBoolean("processing",processing);if(!display.isEmpty())tag.put("display",display.save(lookup));return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);processing=tag.getBoolean("processing");display=tag.contains("display")?ItemStack.parseOptional(lookup,tag.getCompound("display")):ItemStack.EMPTY;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 8;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}
    @Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>64)stack.setCount(64);if(slot>=6&&slot<=7&&ArcWelderMenu.upgrade(stack)&&level!=null&&!level.isClientSide)level.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("item.upgradePlug"),net.minecraft.sounds.SoundSource.BLOCKS,1,1);setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}@Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot>=0&&slot<3;}@Override public int[] getSlotsForFace(Direction side){return new int[]{1,3};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return slot<3;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==3;}

 @Override public int[] sourceProxySlots(net.minecraft.core.BlockPos position,net.minecraft.core.Direction side){return slotsAt(position);}
}
