package com.hbm.machine.soyuzlauncher;

import api.hbm.item.IDesignatorItem;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.entity.rocket.EntityRocketSoyuz;
import com.hbm.fluid.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.*;
import com.hbm.machine.storage.CrateStackData;
import com.hbm.multiblock.*;
import com.hbm.registry.*;
import com.hbm.satellite.SatelliteKind;
import com.hbm.world.soyuz.SoyuzCapsulesContent;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source27slot state; original launch predicates, exact consumption and transient countdown. */
public final class SoyuzLauncherBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyReceiver,SidedEnergyConnector,MachineView,MuffledMachine {
    public static final long MAX_POWER=1_000_000;
    public static final int MAX_COUNT=600;
    public final ItemStack[] slots=new ItemStack[27];
    public final FluidTank[] tanks={new FluidTank(HbmFluids.KEROSENE,128_000),new FluidTank(HbmFluids.OXYGEN,128_000)};
    public long power;public byte mode,rocketType=-1;public boolean starting,muffled,tilted;public int countdown;
    private String customName;
    CompoundTag lastSync;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
    public static Consumer<SoyuzLauncherBlockEntity> CLIENT_TICK=machine->{},CLIENT_REMOVE=machine->{};
    public SoyuzLauncherBlockEntity(BlockPos pos,BlockState state){super(SoyuzLauncherContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public static void tick(Level level,BlockPos pos,BlockState state,SoyuzLauncherBlockEntity machine){
        if(level instanceof ServerLevel server)machine.serverTick(server);else{if(!machine.starting)machine.countdown=MAX_COUNT;else if(machine.countdown>0)machine.countdown--;CLIENT_TICK.accept(machine);}
    }
    private void serverTick(ServerLevel server){
        updatePorts(server);tanks[0].loadTank(4,5,slots);tanks[1].loadTank(6,7,slots);chargeBattery();
        if(!starting||!canLaunch()){countdown=MAX_COUNT;starting=false;}else if(countdown>0){countdown--;if(countdown%100==0&&countdown>0)server.playSound(null,worldPosition,HbmSounds.get("alarm.hatch"),SoundSource.BLOCKS,100,1.1F);}else liftOff();
        rocketType=getRocketSkinType();SoyuzLauncherSync.send(server,this);setChanged();
    }
    private void chargeBattery(){
        if(power<0){power=0;return;}if(power>MAX_POWER){power=MAX_POWER;return;}var stack=slots[8];
        if(stack.getItem() instanceof CreativeBatteryItem){power=MAX_POWER;return;}
        if(stack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(MAX_POWER-power,battery.getDischargeRate(stack)),battery.getCharge(stack));power+=amount;battery.dischargeBattery(stack,amount);}
    }
    public void startCountdown(){if(canLaunch())starting=true;}
    public void liftOff(){
        if(!(level instanceof ServerLevel server))return;starting=false;int fuel=getFuelRequired(),requiredPower=getPowerRequired();
        var rocket=new EntityRocketSoyuz(level);rocket.setSkin(getRocketSkinType());rocket.mode=mode;rocket.moveTo(worldPosition.getX()+.5,worldPosition.getY()+1,worldPosition.getZ()+.5,0,0);server.addFreshEntity(rocket);
        server.playSound(null,worldPosition,HbmSounds.get("entity.soyuzTakeoff"),SoundSource.BLOCKS,100,1.1F);
        tanks[0].setFill(tanks[0].getFill()-fuel);tanks[1].setFill(tanks[1].getFill()-fuel);power-=requiredPower;
        if(mode==0){rocket.setSat(slots[2]);if(orbital()==2)slots[3]=ItemStack.EMPTY;slots[2]=ItemStack.EMPTY;}
        if(mode==1){var payload=new ArrayList<ItemStack>();for(int i=9;i<27;i++){payload.add(slots[i]);slots[i]=ItemStack.EMPTY;}var target=CrateStackData.data(slots[1]);rocket.targetX=target.getInt("xCoord");rocket.targetZ=target.getInt("zCoord");rocket.setPayload(payload);}
        slots[0]=ItemStack.EMPTY;setChanged();
    }
    /** Source repeats hasRocket and omits hasOxy; do not silently change its actual launch requirement. */
    public boolean canLaunch(){return hasRocket()&&hasFuel()&&hasRocket()&&hasPower()&&designator()!=1&&orbital()!=1&&satellite()!=1;}
    public boolean hasRocket(){return slots[0].is(SoyuzCapsulesContent.SOYUZ.get());}
    public boolean hasFuel(){return tanks[0].getFill()>=getFuelRequired();}
    public boolean hasOxy(){return tanks[1].getFill()>=getFuelRequired();}
    public boolean hasPower(){return power>=getPowerRequired();}
    public int getPowerRequired(){return (int)(MAX_POWER*.75);}
    public int getFuelRequired(){return mode==1?Math.min(5000+getDist(),128_000):128_000;}
    public int getDist(){if(designator()!=2)return 0;var tag=CrateStackData.data(slots[1]);double x=worldPosition.getX()-tag.getInt("xCoord"),z=worldPosition.getZ()-tag.getInt("zCoord");return (int)Math.sqrt(x*x+z*z);}
    /** Source byte skin/type query; named separately from modern BlockEntity.getType(). */
    public byte getRocketSkinType(){return !hasRocket()?-1:(byte)(int)slots[0].getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
    public int designator(){if(mode==0)return 0;return slots[1].getItem() instanceof IDesignatorItem designator&&designator.isReady(level,slots[1],worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())?2:1;}
    public int satellite(){return mode==1?0:slots[2].isEmpty()?1:2;}
    public int orbital(){
        if(mode==1)return 0;
        // Original compares raw itemDamage with MINER_LUNAR regardless of the item class.
        if(!slots[2].isEmpty()&&(slots[2].is(HbmContent.item("sat_gerald"))||slots[2].getOrDefault(HbmHazards.LEGACY_METADATA.get(),slots[2].getDamageValue())==SatelliteKind.MINER_LUNAR.ordinal()))return slots[3].is(SoyuzCapsulesContent.orbitalModule())?2:1;
        return 0;
    }
    private void updatePorts(ServerLevel server){
        if(nodes.isEmpty()||nodes.values().stream().anyMatch(PowerNode::isExpired)){
            disconnect();
            // Only real upgraded proxy cells are HE ports. The old unused getConPos() is not a second IO layout.
            var origins=new LinkedHashSet<BlockPos>();origins.add(worldPosition);
            for(int x=-6;x<=6;x++)for(int z=-6;z<=6;z++)if(x==6||x==-6||z==6||z==-6){
                var p=worldPosition.offset(x,0,z);var state=server.getBlockState(p);
                if(state.is(SoyuzLauncherContent.LAUNCHER.get())&&DummyableBlock.isPort(state))origins.add(p);
            }
            for(var origin:origins){var links=Arrays.stream(Direction.values()).filter(side->{var adjacent=server.getBlockState(origin.relative(side));return !adjacent.is(SoyuzLauncherContent.LAUNCHER.get())||!DummyableBlock.isPort(adjacent);}).map(side->new NodeConnection(origin.relative(side),side)).toArray(NodeConnection[]::new);var node=new PowerNode(origin).setConnections(links);HbmEnergy.nodespace(server).createNode(node);nodes.put(origin,node);}
        }
        for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
    }
    private void disconnect(){if(level instanceof ServerLevel server)for(var node:nodes.values())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear();}
    @Override public void onChunkUnloaded(){disconnect();CLIENT_REMOVE.accept(this);super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();CLIENT_REMOVE.accept(this);super.setRemoved();}
    public CompoundTag clientState(){var tag=new CompoundTag();tag.putLong("power",power);tag.putByte("mode",mode);tag.putBoolean("starting",starting);tag.putInt("countdown",countdown);tag.putByte("rocketType",level!=null&&!level.isClientSide?getRocketSkinType():rocketType);tanks[0].writeToNBT(tag,"fuel");tanks[1].writeToNBT(tag,"oxidizer");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(customName!=null)tag.putString("name",customName);return tag;}
    public void receiveClient(CompoundTag tag){power=tag.getLong("power");mode=tag.getByte("mode");starting=tag.getBoolean("starting");countdown=tag.getInt("countdown");rocketType=tag.getByte("rocketType");tanks[0].readFromNBT(tag,"fuel");tanks[1].readFromNBT(tag,"oxidizer");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");if(tag.contains("name"))customName=tag.getString("name");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putLong("power",power);tag.putByte("mode",mode);tanks[0].writeToNBT(tag,"fuel");tanks[1].writeToNBT(tag,"oxidizer");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(customName!=null)tag.putString("name",customName);var list=new ListTag();for(int i=0;i<27;i++)if(!slots[i].isEmpty()){var entry=(CompoundTag)slots[i].save(lookup);entry.putByte("slot",(byte)i);list.add(entry);}tag.put("items",list);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);power=tag.getLong("power");mode=tag.getByte("mode");tanks[0].readFromNBT(tag,"fuel");tanks[1].readFromNBT(tag,"oxidizer");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");customName=tag.contains("name")?tag.getString("name"):null;Arrays.fill(slots,ItemStack.EMPTY);for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var entry=(CompoundTag)value;int i=entry.getByte("slot");if(i>=0&&i<27)slots[i]=ItemStack.parseOptional(lookup,entry);}starting=false;countdown=0;rocketType=-1;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return clientState();}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){receiveClient(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public long getPower(){return power;}@Override public long getMaxPower(){return MAX_POWER;}@Override public void setPower(long value){power=value;setChanged();}@Override public boolean isLoaded(){return !isRemoved()&&level!=null;}@Override public boolean canConnect(Direction side){return side!=null;}
    @Override public long energyStored(){return power;}@Override public long energyCapacity(){return MAX_POWER;}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return starting?List.of(new ProgressView("gui.hbm.soyuz.countdown",MAX_COUNT-countdown,MAX_COUNT,0xFFAA22)):List.of();}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public Component getDisplayName(){return customName==null||customName.isEmpty()?Component.translatable("container.soyuzLauncher"):Component.literal(customName);}@Override public void setCustomName(Component value){customName=value.getString();setChanged();}@Override public AbstractContainerMenu createMenu(int id,Inventory player,Player opener){return new SoyuzLauncherMenu(id,player,this,worldPosition);}
    @Override public int getContainerSize(){return 27;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int amount){var result=slots[slot].split(amount);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;stack.limitSize(64);setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
}
