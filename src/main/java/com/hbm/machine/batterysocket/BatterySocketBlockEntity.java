package com.hbm.machine.batterysocket;

import com.hbm.api.computer.ComputerArguments;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.computer.ComputerException;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.ConnectionPriority;
import com.hbm.energy.EnergyConductor;
import com.hbm.energy.EnergyProvider;
import com.hbm.energy.EnergyReceiver;
import com.hbm.energy.HbmEnergy;
import com.hbm.energy.NodeConnection;
import com.hbm.energy.PowerNode;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.battery.SelfChargingBatteryItem;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.multiblock.NamedMultiblock;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 BatteryBase + BatterySocket, with no machine-side energy buffer. */
public final class BatterySocketBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyProvider,EnergyReceiver,EnergyConductor,MachineView,ComputerControl, com.hbm.multiblock.MuffledMachine {
    public static final int INPUT=0,BUFFER=1,OUTPUT=2,NONE=3;
    private final NonNullList<ItemStack> slots=NonNullList.withSize(1,ItemStack.EMPTY);
    private final long[] log=new long[20];
    private PowerNode node;
    private short redLow=INPUT,redHigh=OUTPUT,modeCache;
    private ConnectionPriority priority=ConnectionPriority.LOW;
    private byte lastRedstone;
    private long prevPowerState,delta,syncPower,syncMaxPower;
    private ItemStack syncStack=ItemStack.EMPTY;
    private Component customName;
    private boolean loaded=true,frame;
    public boolean muffled,tilted;
    private int damageTimer,damageTarget;
    private double scPowerMult=1D;
    private final ContainerData data=new ContainerData() {
        @Override public int get(int index) {
            if(index<4) return word(powerFromStack(slots.getFirst()),index);
            if(index<8) return word(getMaxPower(),index-4);
            if(index<12) return word(delta,index-8);
            return switch(index) { case 12->redLow;case 13->redHigh;case 14->priority.ordinal();default->0; };
        }
        @Override public void set(int index,int value) {}
        @Override public int getCount() { return 15; }
    };
    public BatterySocketBlockEntity(BlockPos pos,BlockState state) { super(BatterySocketContent.ENTITY.get(),pos,state); }
    private static int word(long value,int word) { return (int)(value>>>(word*16)&65535L); }
    public ContainerData menuData() { return data; }
    public int redLow() { return redLow; }
    public int redHigh() { return redHigh; }
    public long delta() { return delta; }
    public boolean frame() { return frame; }
    public long displayedPower() { return level!=null&&level.isClientSide?syncPower:getPower(); }
    public long displayedMaxPower() { return level!=null&&level.isClientSide?syncMaxPower:getMaxPower(); }
    public ItemStack displayedStack() { return level!=null&&level.isClientSide?syncStack:slots.getFirst(); }
    public double scPowerMultiplier() { return scPowerMult; }
    public int damageTimer() { return damageTimer; }
    public int damageTarget() { return damageTarget; }
    @Override public void setCustomName(Component name) { customName=name;setChanged(); }
    @Override public Component getDisplayName() { return customName==null?Component.translatable("container.batterySocket"):customName; }
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player) { return new BatterySocketMenu(id,inventory,this,data,worldPosition); }
    public static BlockPos[] portPositions(BlockPos pos,BlockState state) {
        Direction dir=DummyableBlock.facing(state),rot=dir.getClockWise();
        return new BlockPos[]{pos,pos.relative(dir.getOpposite()),pos.relative(rot),pos.relative(dir.getOpposite()).relative(rot)};
    }
    public static NodeConnection[] connectionPositions(BlockPos pos,BlockState state) {
        Direction dir=DummyableBlock.facing(state),rot=dir.getClockWise();var result=new ArrayList<NodeConnection>(8);
        for(BlockPos port:portPositions(pos,state)) for(Direction side:Direction.Plane.HORIZONTAL) {
            BlockPos target=port.relative(side);boolean internal=false;
            for(BlockPos other:portPositions(pos,state)) if(other.equals(target)) {internal=true;break;}
            if(!internal) result.add(new NodeConnection(target,side));
        }
        return result.toArray(NodeConnection[]::new);
    }
    public static void tick(Level level,BlockPos pos,BlockState state,BatterySocketBlockEntity socket) {
        if(level.isClientSide) { if(level.getGameTime()%20==0) socket.frame=!level.getBlockState(pos.above(2)).isAir(); }
        else if(level instanceof ServerLevel server) socket.serverTick(server);
    }
    private void serverTick(ServerLevel server) {
        long previous=getPower();
        if(priority==null||priority==ConnectionPriority.LOWEST||priority==ConnectionPriority.HIGHEST) priority=ConnectionPriority.LOW;
        if(node==null||node.isExpired()) {
            node=HbmEnergy.nodespace(server).getNode(worldPosition);
            if(node==null||node.isExpired()) { node=new PowerNode(portPositions(worldPosition,getBlockState())).setConnections(connectionPositions(worldPosition,getBlockState()));HbmEnergy.nodespace(server).createNode(node); }
        }
        if(node.hasValidNet()) {
            int mode=relevantMode(false);
            if(mode==OUTPUT||mode==BUFFER) node.addProvider(this);else node.removeProvider(this);
            if(mode==INPUT||mode==BUFFER) node.addReceiver(this);else node.removeReceiver(this);
        }
        byte comparator=(byte)comparatorPower();
        if(comparator!=lastRedstone) for(BlockPos port:portPositions(worldPosition,getBlockState())) { if(level.getBlockEntity(port)!=null) level.getBlockEntity(port).setChanged();level.updateNeighbourForOutputSignal(port,getBlockState().getBlock()); }
        lastRedstone=comparator;prevPowerState=getPower();
        // Source BatteryBase.networkPackNT(100) runs before SC fluctuation and this tick's log update.
        var packet=getUpdatePacket();for(var player:server.players()) if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000) player.connection.send(packet);
        if(hasSCLoaded()) {
            if(damageTarget==0) pickNewSCTarget(server);
            damageTimer++;
            if(damageTimer>=damageTarget) { pickNewSCTarget(server);SocketDischarge.discharge(server,worldPosition,getBlockState()); }
            scPowerMult+=.01D*(server.random.nextDouble()*2-1);scPowerMult=Mth.clamp(scPowerMult,.1D,1D);
            setChanged();
        }
        long average=(getPower()+previous)/2;delta=average-log[0];System.arraycopy(log,1,log,0,19);log[19]=average;
    }
    private void pickNewSCTarget(ServerLevel server) { damageTimer=0;damageTarget=1200+server.random.nextInt(2400);setChanged(); }
    public boolean hasSCLoaded() { ItemStack stack=slots.getFirst();return stack.getItem() instanceof SelfChargingBatteryItem item&&item.variant(stack)!=SelfChargingBatteryItem.EnumBatterySC.EMPTY; }
    public short relevantMode(boolean cached) {
        if(cached) return modeCache;
        boolean powered=false;
        if(level!=null) for(BlockPos pos:portPositions(worldPosition,getBlockState())) if(level.hasNeighborSignal(pos)) {powered=true;break;}
        return modeCache=powered?redHigh:redLow;
    }
    public int comparatorPower() { return Mth.clamp((int)Math.round((double)getPower()/Math.max(getMaxPower(),1)*15D),0,15); }
    public static long powerFromStack(ItemStack stack) { return stack.getItem() instanceof HbmBattery battery?battery.getCharge(stack):0; }
    public static long maxPowerFromStack(ItemStack stack) { return stack.getItem() instanceof HbmBattery battery?battery.getMaxCharge(stack):0; }
    @Override public long getPower() { long power=powerFromStack(slots.getFirst());if(hasSCLoaded()) power*=scPowerMult;return power; }
    @Override public long getMaxPower() { return maxPowerFromStack(slots.getFirst()); }
    @Override public void setPower(long power) { if(slots.getFirst().getItem() instanceof HbmBattery battery) {battery.setCharge(slots.getFirst(),power);setChanged();} }
    @Override public long getProviderSpeed() { if(!(slots.getFirst().getItem() instanceof HbmBattery battery)) return 0;int mode=relevantMode(true);return mode==OUTPUT||mode==BUFFER?battery.getDischargeRate(slots.getFirst()):0; }
    @Override public long getReceiverSpeed() { if(!(slots.getFirst().getItem() instanceof HbmBattery battery)) return 0;int mode=relevantMode(true);return mode==INPUT||mode==BUFFER?battery.getChargeRate(slots.getFirst()):0; }
    @Override public ConnectionPriority getPriority() { return priority; }
    @Override public PowerNode energyNode() { return node; }
    @Override public boolean isLoaded() { return loaded&&!isRemoved()&&level!=null; }
    @Override public boolean canConnect(Direction side) { return side!=null; }
    @Override public long energyStored() { return getPower(); }
    @Override public long energyCapacity() { return getMaxPower(); }
    public boolean cycleButton(int button) {
        switch(button) { case 0->redLow=(short)((redLow+1)%4);case 1->redHigh=(short)((redHigh+1)%4);case 2->priority=ConnectionPriority.values()[priority.ordinal()>=3?1:priority.ordinal()+1];default->{return false;} }
        setChanged();return true;
    }
    public CompoundTag settings() { var tag=new CompoundTag();tag.putShort("redLow",redLow);tag.putShort("redHigh",redHigh);tag.putByte("priority",(byte)priority.ordinal());return tag; }
    public void pasteSettings(CompoundTag tag) { if(tag.contains("redLow")) redLow=tag.getShort("redLow");if(tag.contains("redHigh")) redHigh=tag.getShort("redHigh");if(tag.contains("priority")) priority=priority(tag.getByte("priority"));setChanged(); }
    private static ConnectionPriority priority(int value) { return value>=0&&value<ConnectionPriority.values().length?ConnectionPriority.values()[value]:ConnectionPriority.LOWEST; }
    @Override public String computerType() { return "ntm_energy_storage"; }
    @Override public String[] computerMethods() { return new String[]{"getEnergyInfo","getPackInfo","getModeInfo","setModeLow","setModeHigh","setPriority","getInfo"}; }
    private String packName() { ItemStack stack=slots.getFirst();if(stack.isEmpty()||!(stack.getItem() instanceof HbmBattery)) return "";if(stack.getItem() instanceof com.hbm.item.battery.BatteryPackItem pack)return "item.battery_pack."+pack.type().name().toLowerCase(java.util.Locale.ROOT);if(stack.getItem() instanceof SelfChargingBatteryItem sc)return "item.battery_sc."+sc.variant(stack).name().toLowerCase(java.util.Locale.ROOT);return stack.getDescriptionId().replace("item.hbm.","item."); }
    private Object[] packInfo() { ItemStack stack=slots.getFirst();return stack.getItem() instanceof HbmBattery battery?new Object[]{packName(),battery.getChargeRate(stack),battery.getDischargeRate(stack)}:new Object[]{"",0,0}; }
    @Override public Object[] callComputer(String method,ComputerArguments arguments) throws ComputerException {
        return switch(method) {
            case "getEnergyInfo"->new Object[]{getPower(),getMaxPower(),delta};
            case "getPackInfo"->packInfo();
            case "getModeInfo"->new Object[]{redLow,redHigh,priority.ordinal()-1};
            case "setModeLow","setModeHigh"->{short value=(short)arguments.checkInteger(0);if(value<0||value>3) yield new Object[]{"Invalid mode"};if(method.equals("setModeLow")) redLow=value;else redHigh=value;setChanged();yield new Object[0];}
            case "setPriority"->{int value=arguments.checkInteger(0);if(value<0||value>2) yield new Object[]{"Invalid mode"};priority=ConnectionPriority.values()[value+1];setChanged();yield new Object[0];}
            case "getInfo"->{Object[] pack=packInfo();yield new Object[]{getPower(),getMaxPower(),delta,redLow,redHigh,priority.ordinal()-1,pack[0],pack[1],pack[2]};}
            default->throw new ComputerException("No such method: "+method);
        };
    }
    public String[] getFunctionInfo() { return new String[]{"VAL:fill","VAL:maxfill","VAL:fillpercent","VAL:delta","FUN:setmode!mode (0-3)","FUN:setmode!mode:fallback (0-3)","FUN:setredmode!mode (0-3)","FUN:setredmode!mode:fallback (0-3)","FUN:setpriority!priority (0-2)"}; }
    public String provideRORValue(String name) { return switch(name) {case "VAL:fill"->""+getPower();case "VAL:maxfill"->""+getMaxPower();case "VAL:fillpercent"->""+(getPower()*100/Math.max(getMaxPower(),1));case "VAL:delta"->""+delta;default->null;}; }
    public String runRORFunction(String name,String[] parameters) {
        if(parameters.length==0) return null;
        if(name.equals("FUN:setmode")||name.equals("FUN:setredmode")) {
            short value=(short)rorInt(parameters[0],0,3),old=name.equals("FUN:setmode")?redLow:redHigh;
            if(value==old&&parameters.length>1) value=(short)rorInt(parameters[1],0,3);
            if(name.equals("FUN:setmode")) redLow=value;else redHigh=value;
            setChanged();
        } else if(name.equals("FUN:setpriority")) {priority=ConnectionPriority.values()[rorInt(parameters[0],0,2)+1];setChanged();}
        return null;
    }
    private static int rorInt(String text,int min,int max) {int result;try{result=Integer.parseInt(text);}catch(Exception error){try{result=(int)Math.round(Double.parseDouble(text));}catch(Exception invalid){throw new IllegalArgumentException("Exception: Parameter in Invalid Format");}}if(result<min||result>max)throw new IllegalArgumentException("Exception: Parameter in Invalid Format");return result;}
    public long energyControlDelta() { return (log[0]-log[19])/20L; }
    private void removeNode() { if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server) HbmEnergy.nodespace(server).destroyNode(node);node=null; }
    @Override public void onLoad() { super.onLoad();loaded=true; }
    @Override public void onChunkUnloaded() { loaded=false;removeNode();super.onChunkUnloaded(); }
    @Override public void setRemoved() { loaded=false;removeNode();super.setRemoved(); }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup) {
        super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,slots,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putShort("redLow",redLow);tag.putShort("redHigh",redHigh);tag.putByte("lastRedstone",lastRedstone);tag.putByte("priority",(byte)priority.ordinal());tag.putInt("damageTimer",damageTimer);tag.putInt("damageTarget",damageTarget);tag.putDouble("scPowerMult",scPowerMult);if(customName!=null) tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup) {
        super.loadAdditional(tag,lookup);slots.clear();ContainerHelper.loadAllItems(tag,slots,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");redLow=tag.getShort("redLow");redHigh=tag.getShort("redHigh");lastRedstone=tag.getByte("lastRedstone");priority=priority(tag.getByte("priority"));damageTimer=tag.getInt("damageTimer");damageTarget=tag.getInt("damageTarget");scPowerMult=tag.getDouble("scPowerMult");customName=tag.contains("CustomName")?Component.Serializer.fromJson(tag.getString("CustomName"),lookup):tag.contains("name")?Component.literal(tag.getString("name")):null;
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        var tag=new CompoundTag();tag.putShort("redLow",redLow);tag.putShort("redHigh",redHigh);tag.putByte("priority",(byte)priority.ordinal());tag.putLong("delta",delta);tag.putLong("syncPower",getPower());tag.putLong("syncMaxPower",getMaxPower());
        if(!slots.getFirst().isEmpty()) { ItemStack visual=new ItemStack(slots.getFirst().getItem());int meta=slots.getFirst().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);if(meta!=0) visual.set(HbmHazards.LEGACY_METADATA.get(),meta);tag.put("syncStack",visual.save(lookup)); }
        return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup) { redLow=tag.getShort("redLow");redHigh=tag.getShort("redHigh");priority=priority(tag.getByte("priority"));delta=tag.getLong("delta");syncPower=tag.getLong("syncPower");syncMaxPower=tag.getLong("syncMaxPower");syncStack=tag.contains("syncStack")?ItemStack.parseOptional(lookup,tag.getCompound("syncStack")):ItemStack.EMPTY; }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(),lookup); }
    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return slots.getFirst().isEmpty(); }
    @Override public ItemStack getItem(int slot) { return slots.get(slot); }
    @Override public ItemStack removeItem(int slot,int count) { ItemStack stack=ContainerHelper.removeItem(slots,slot,count);if(!stack.isEmpty()) setChanged();return stack; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(slots,slot); }
    @Override public void setItem(int slot,ItemStack stack) { slots.set(slot,stack);if(stack.getCount()>getMaxStackSize()) stack.setCount(getMaxStackSize());setChanged(); }
    @Override public void clearContent() { slots.clear();setChanged(); }
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0}; }
    @Override public boolean canPlaceItem(int slot,ItemStack stack) { return stack.getItem() instanceof HbmBattery; }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side) { return canPlaceItem(slot,stack); }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side) { if(!(stack.getItem() instanceof HbmBattery battery)) return false;int mode=relevantMode(false);return mode==OUTPUT&&battery.getCharge(stack)==0||mode==INPUT&&battery.getCharge(stack)==battery.getMaxCharge(stack); }

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
