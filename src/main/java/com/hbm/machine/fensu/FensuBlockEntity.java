package com.hbm.machine.fensu;

import com.hbm.api.computer.*;
import com.hbm.api.machine.ICopiable;
import com.hbm.energy.*;
import com.hbm.item.battery.*;
import com.hbm.multiblock.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Actual unbounded REDD storage. HE getters intentionally retain the source's capped provision/connection-speed view. */
public final class FensuBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyConductor,EnergyProvider,EnergyReceiver,ComputerControl,ICopiable, com.hbm.multiblock.MuffledMachine {
    public static final long SPEED=Long.MAX_VALUE/100L;
    private static final BigInteger BI_SPEED=BigInteger.valueOf(SPEED),BI_HALF=BigInteger.valueOf(SPEED/2);
    public static Consumer<FensuBlockEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
    private final NonNullList<ItemStack> inventory=NonNullList.withSize(2,ItemStack.EMPTY);
    private final BigInteger[] log=new BigInteger[20];
    private BigInteger power=BigInteger.ZERO;
    public BigInteger delta=BigInteger.ZERO;
    public float prevRotation,rotation;
    public short redLow=0,redHigh=2;
    public byte lastRedstone;
    public long prevPowerState;
    public boolean muffled,tilted;
    private short modeCache;
    private PowerNode node;
    private Component customName;
    private ConnectionPriority priority=ConnectionPriority.LOW;
    public FensuBlockEntity(BlockPos pos,BlockState state){super(FensuContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public BigInteger storedPower(){return power;}
    public List<FensuPorts.Port> ports(){return FensuPorts.at(worldPosition,DummyableBlock.facing(getBlockState()));}
    public static void tick(Level level,BlockPos pos,BlockState state,FensuBlockEntity machine){if(level instanceof ServerLevel server)machine.tickServer(server);else{machine.prevRotation=machine.rotation;machine.rotation+=machine.speed();if(machine.rotation>=360){machine.rotation-=360;machine.prevRotation-=360;}CLIENT_TICK.accept(machine);}}
    private void tickServer(ServerLevel server){
        BigInteger previous=power;
        // Source TileEntityBatteryBase.updateEntity, including the BEFORE-item-charge network snapshot.
        if(priority==null||priority==ConnectionPriority.LOWEST||priority==ConnectionPriority.HIGHEST)priority=ConnectionPriority.LOW;
        if(node==null||node.isExpired()){
            var ports=ports();node=new PowerNode(ports.stream().map(FensuPorts.Port::pos).toArray(BlockPos[]::new)).setConnections(ports.stream().map(port->new NodeConnection(port.pos().relative(port.outward()),port.outward())).toArray(NodeConnection[]::new));
            HbmEnergy.nodespace(server).createNode(node);
        }
        if(node.hasValidNet())switch(relevantMode(false)){
            case 0->{node.removeProvider(this);node.addReceiver(this);}
            case 1->{node.addProvider(this);node.addReceiver(this);}
            case 2->{node.addProvider(this);node.removeReceiver(this);}
            case 3->{node.removeProvider(this);node.removeReceiver(this);}
            default->{}
        }
        byte comparator=(byte)comparatorPower();
        if(comparator!=lastRedstone)for(var port:ports())if(server.hasChunkAt(port.pos())){var proxy=server.getBlockEntity(port.pos());if(proxy!=null)proxy.setChanged();server.updateNeighbourForOutputSignal(port.pos(),getBlockState().getBlock());}
        lastRedstone=comparator;prevPowerState=getPower();FensuPackets.send(this);
        // Exact Library.chargeTEFromItems(slot0,0,SPEED), followed by chargeItemsFromTE(slot1,getPower(),SPEED).
        var source=inventory.get(0);long add=0;
        if(source.getItem() instanceof CreativeBatteryItem)add=SPEED;
        else if(source.getItem() instanceof HbmBattery battery){add=Math.min(Math.min(SPEED,battery.getDischargeRate(source)),battery.getCharge(source));battery.dischargeBattery(source,add);}
        if(add>0)power=power.add(BigInteger.valueOf(add));
        var target=inventory.get(1);long provision=getPower(),after=provision;
        if(target.getItem() instanceof CreativeBatteryItem)after=0;
        else if(target.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(provision,battery.getChargeRate(target)),battery.getMaxCharge(target)-battery.getCharge(target));battery.chargeBattery(target,amount);after-=amount;}
        long remove=provision-after;if(remove>0)power=power.subtract(BigInteger.valueOf(remove));
        BigInteger average=power.add(previous).divide(BigInteger.TWO);delta=average.subtract(log[0]==null?BigInteger.ZERO:log[0]);System.arraycopy(log,1,log,0,19);log[19]=average;
        if(!power.equals(previous))setChanged();
    }
    public float speed(){return(float)Math.min(Math.pow(Math.log(power.doubleValue()*.05+1)*.05F,5),15F);}
    public short relevantMode(boolean cached){if(cached)return modeCache;boolean powered=false;if(level!=null)for(var port:ports())if(level.hasChunkAt(port.pos())&&level.hasNeighborSignal(port.pos())){powered=true;break;}return modeCache=powered?redHigh:redLow;}
    public int comparatorPower(){return Mth.clamp((int)Math.round((double)getPower()/Math.max(getMaxPower(),1)*15),0,15);}
    @Override public long getPower(){return power.min(BI_HALF).longValue();}
    @Override public long getMaxPower(){return SPEED;}
    @Override public void setPower(long value){/* Source explicitly has no long setter: usePower/transferPower own real bigint. */}
    @Override public BigInteger getPowerExact(){return BigInteger.valueOf(getPower());}
    @Override public BigInteger getMaxPowerExact(){return BI_SPEED;}
    @Override public void setPowerExact(BigInteger value){power=Objects.requireNonNull(value);setChanged();}
    @Override public long transferPower(long amount){power=power.add(BigInteger.valueOf(amount));setChanged();return 0;}
    @Override public BigInteger transferPowerExact(BigInteger amount){if(amount.signum()<0)throw new IllegalArgumentException("Negative HE transfer");power=power.add(amount);setChanged();return BigInteger.ZERO;}
    @Override public void usePower(long amount){power=power.subtract(BigInteger.valueOf(amount));setChanged();}
    @Override public void usePowerExact(BigInteger amount){if(amount.signum()<0)throw new IllegalArgumentException("Negative HE debit");power=power.subtract(amount);setChanged();}
    @Override public long getProviderSpeed(){return SPEED;}@Override public long getReceiverSpeed(){return SPEED;}
    @Override public BigInteger getProviderSpeedExact(){return BI_SPEED;}@Override public BigInteger getReceiverSpeedExact(){return BI_SPEED;}
    @Override public ConnectionPriority getPriority(){return priority;}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public PowerNode energyNode(){return node;}
    private void disconnect(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.batteryREDD"):customName;}
    @Override public void setCustomName(Component value){customName=value;setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory player,Player owner){return new FensuMenu(id,player,this,worldPosition);}
    public boolean cycle(int button){switch(button){case 0->{redLow++;if(redLow>3)redLow=0;}case 1->{redHigh++;if(redHigh>3)redHigh=0;}case 2->{int next=priority.ordinal()+1;if(next>ConnectionPriority.HIGH.ordinal())next=ConnectionPriority.LOW.ordinal();priority=ConnectionPriority.values()[next];}default->{return false;}}setChanged();return true;}
    public float volume(float base){return muffled?base*.1F:base;}
    @Override public String computerType(){return "ntm_energy_storage";}
    @Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getModeInfo","setModeLow","setModeHigh","setPriority","getInfo","getEnergyInfoExact","getInfoExact"};}
    @Override public Object[] callComputer(String method,ComputerArguments arguments){return switch(method){
        case "getEnergyInfo"->new Object[]{power.doubleValue(),delta.longValue()};
        case "getModeInfo"->new Object[]{redLow,redHigh,priority.ordinal()-1};
        case "getInfo"->new Object[]{power.doubleValue(),delta.longValue(),redLow,redHigh,priority.ordinal()-1};
        case "getEnergyInfoExact"->new Object[]{power.toString(),delta.toString()};
        case "getInfoExact"->new Object[]{power.toString(),delta.toString(),redLow,redHigh,priority.ordinal()-1};
        case "setModeLow","setModeHigh"->{short next=(short)arguments.checkInteger(0);if(next<0||next>3)yield new Object[]{"Invalid mode"};if(method.equals("setModeLow"))redLow=next;else redHigh=next;setChanged();yield new Object[]{};}
        case "setPriority"->{int next=arguments.checkInteger(0);if(next<0||next>2)yield new Object[]{"Invalid mode"};priority=ConnectionPriority.values()[next+1];setChanged();yield new Object[]{};}
        default->throw new ComputerException("No such method: "+method);
    };}
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putShort("redLow",redLow);tag.putShort("redHigh",redHigh);tag.putByte("priority",(byte)priority.ordinal());return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){if(tag.contains("redLow"))redLow=tag.getShort("redLow");if(tag.contains("redHigh"))redHigh=tag.getShort("redHigh");if(tag.contains("priority"))priority=readPriority(tag.getByte("priority"));setChanged();}
    private static ConnectionPriority readPriority(int raw){return ConnectionPriority.values()[Math.abs(raw%ConnectionPriority.values().length)];}
    private static BigInteger readPower(CompoundTag tag){byte[] bytes=tag.getByteArray("power");return bytes.length==0?BigInteger.ZERO:new BigInteger(bytes);}
    public ItemStack dropStack(){var result=new ItemStack(FensuContent.ITEM.get());var tag=new CompoundTag();var persistent=new CompoundTag();persistent.putByteArray("power",power.toByteArray());persistent.putBoolean("muffled",muffled);tag.put("persistent",persistent);result.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return result;}
    public void readItem(ItemStack stack){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();if(tag.contains("persistent",Tag.TAG_COMPOUND)){var persistent=tag.getCompound("persistent");power=readPower(persistent);muffled=persistent.getBoolean("muffled");setChanged();}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,inventory,lookup);tag.putByteArray("power",power.toByteArray());tag.putShort("redLow",redLow);tag.putShort("redHigh",redHigh);tag.putByte("lastRedstone",lastRedstone);tag.putByte("priority",(byte)priority.ordinal());tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,inventory,lookup);power=readPower(tag);redLow=tag.getShort("redLow");redHigh=tag.getShort("redHigh");lastRedstone=tag.getByte("lastRedstone");priority=readPriority(tag.getByte("priority"));muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");if(tag.contains("CustomName",Tag.TAG_STRING))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    public void applyState(FensuPackets.State snapshot){power=snapshot.power();delta=snapshot.delta();redLow=snapshot.redLow();redHigh=snapshot.redHigh();priority=readPriority(snapshot.priority());muffled=snapshot.muffled();tilted=snapshot.tilted();}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putByteArray("power",power.toByteArray());tag.putByteArray("delta",delta.toByteArray());tag.putShort("redLow",redLow);tag.putShort("redHigh",redHigh);tag.putByte("priority",(byte)priority.ordinal());tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){byte[] bytes=tag.getByteArray("delta");applyState(new FensuPackets.State(worldPosition,readPower(tag),bytes.length==0?BigInteger.ZERO:new BigInteger(bytes),tag.getShort("redLow"),tag.getShort("redHigh"),tag.getByte("priority"),tag.getBoolean("muffled"),tag.getBoolean("tilted")));}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public int getContainerSize(){return 2;}@Override public boolean isEmpty(){return inventory.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return inventory.get(slot);}
    @Override public ItemStack removeItem(int slot,int amount){var removed=ContainerHelper.removeItem(inventory,slot,amount);if(!removed.isEmpty())setChanged();return removed;}
    @Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(inventory,slot);}@Override public void setItem(int slot,ItemStack stack){inventory.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){inventory.clear();setChanged();}@Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return stack.getItem() instanceof HbmBattery;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
