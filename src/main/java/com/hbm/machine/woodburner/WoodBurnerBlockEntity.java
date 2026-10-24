package com.hbm.machine.woodburner;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.multiblock.*;
import com.hbm.pollution.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 wood burner: source fuel loading, battery charge,100HE solid burn and flammable-fluid mode. */
public final class WoodBurnerBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,EnergyProvider,EnergyConductor,StandardFluidReceiver,MachineView,com.hbm.api.machine.IFluidCopiable {
    public static final long MAX_POWER=100000;
    private final ItemStack[] slots=new ItemStack[6];
    private final FluidTank tank=new FluidTank(HbmFluids.WOODOIL,16000);
    private PowerNode node; private FluidPorts fluidPorts; private List<BlockPos> currentPorts=List.of();
    private long power; private int burnTime,maxBurnTime,powerGen; private boolean on,liquidBurn; private final int[] ashLevels=new int[3]; private Component customName;
    private final ContainerData data=new ContainerData() {
        @Override public int get(int index) {
            if(index<4) return (int)(power>>>(index*16))&65535;
            if(index<6) return burnTime>>>((index-4)*16)&65535;
            if(index<8) return maxBurnTime>>>((index-6)*16)&65535;
            if(index<10) return powerGen>>>((index-8)*16)&65535;
            return switch(index) { case 10->on?1:0; case 11->liquidBurn?1:0; case 12,13->tank.getFill()>>>((index-12)*16)&65535; case 14,15->tank.getMaxFill()>>>((index-14)*16)&65535; case 16->tank.getTankType().getID(); default->tank.getPressure(); };
        }
        @Override public void set(int index,int value) {}
        @Override public int getCount() { return 18; }
    };
    public WoodBurnerBlockEntity(BlockPos pos,BlockState state) { super(WoodBurnerContent.ENTITY.get(),pos,state); Arrays.fill(slots,ItemStack.EMPTY); }
    @Override public boolean isValidBlockState(BlockState state) { return super.isValidBlockState(state)&&DummyableBlock.isCore(state); }
    public ContainerData menuData() { return data; } public FluidTank tank() { return tank; } public int powerGenerated() { return powerGen; } public int burnTime() { return burnTime; } public int maxBurnTime() { return maxBurnTime; } public boolean on() { return on; } public boolean liquidBurn() { return liquidBurn; }
    public boolean control(int button) { if(button==0) on=!on; else if(button==1) liquidBurn=!liquidBurn; else return false; setChanged(); return true; }
    @Override public void setCustomName(Component name) { customName=name; setChanged(); }
    @Override public Component getDisplayName() { return customName==null?Component.translatable("container.machineWoodBurner"):customName; }
    @Override public AbstractContainerMenu createMenu(int id,Inventory player,Player viewer) { return new WoodBurnerMenu(id,player,this,data,worldPosition); }
    public static BlockPos[] portPositions(BlockPos pos,BlockState state) { Direction dir=DummyableBlock.facing(state),rot=dir.getClockWise(); BlockPos back=pos.relative(dir.getOpposite()); return new BlockPos[]{back,back.relative(rot)}; }
    public static void tick(Level level,BlockPos pos,BlockState state,WoodBurnerBlockEntity machine) {
        if(level instanceof ServerLevel server) machine.serverTick(server);
        else if(machine.powerGen>0) { Direction dir=DummyableBlock.facing(state),rot=dir.getClockWise(); level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,pos.getX()+.5-dir.getStepX()+rot.getStepX(),pos.getY()+4,pos.getZ()+.5-dir.getStepZ()+rot.getStepZ(),0,.05,0); }
    }
    private void serverTick(ServerLevel server) {
        updatePorts(server); long previousPower=power; int previousBurn=burnTime,previousFill=tank.getFill(); HbmFluidType previousType=tank.getTankType();
        powerGen=0;
        boolean changed=tank.setType(2,slots); changed|=tank.loadTank(3,4,slots); chargeBattery();
        if(!liquidBurn) {
            if(burnTime<=0) {
                if(!slots[0].isEmpty()) { int burn=WoodBurnerFuel.burnTime(slots[0]); if(burn>0) {
                    ashLevels[WoodBurnerFuel.ashType(slots[0])]+=burn;
                    for(int type=0;type<3;type++) while(processAsh(ashLevels[type],type)) ashLevels[type]-=2000;
                    maxBurnTime=burnTime=burn; ItemStack remainder=slots[0].getCraftingRemainingItem(); removeItem(0,1); if(slots[0].isEmpty()&&!remainder.isEmpty()) slots[0]=remainder.copy(); changed=true;
                } }
            } else if(power<MAX_POWER&&on) { burnTime--; powerGen+=100; if(server.getGameTime()%20==0) PollutionSystem.increment(server,worldPosition,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND); }
        } else if(power<MAX_POWER&&tank.getFill()>0&&on) {
            var trait=tank.getTankType().getTrait(FluidTraits.Flammable.class);
            if(trait!=null) { int amount=Math.min(tank.getFill(),2); if(amount>0) { powerGen+=trait.getHeatEnergy()*amount/2000L; tank.setFill(tank.getFill()-amount); if(server.getGameTime()%20==0) PollutionSystem.increment(server,worldPosition,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND*amount/2F); } }
        }
        power+=powerGen; if(power>MAX_POWER) power=MAX_POWER;
        if(changed||power!=previousPower||burnTime!=previousBurn||tank.getFill()!=previousFill||tank.getTankType()!=previousType) setChanged();
        var packet=getUpdatePacket(); for(var player:server.players()) if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625) player.connection.send(packet);
    }
    private void chargeBattery() {
        if(power<0) { power=0; return; } if(power>MAX_POWER) { power=MAX_POWER; return; }
        if(slots[5].getItem() instanceof CreativeBatteryItem) { power=0; return; }
        if(slots[5].getItem() instanceof HbmBattery battery) { long amount=Math.min(Math.min(power,battery.getChargeRate(slots[5])),battery.getMaxCharge(slots[5])-battery.getCharge(slots[5])); power-=amount; battery.chargeBattery(slots[5],amount); if(amount!=0) setChanged(); }
    }
    private boolean processAsh(int level,int type) {
        if(level<2000) return false;
        Item ash=com.hbm.item.ash.FlatAshContent.ASHES.get(type).get(); if(ash==Items.AIR) return false;
        if(slots[1].isEmpty()) { slots[1]=new ItemStack(ash); return true; }
        if(slots[1].getCount()<slots[1].getMaxStackSize()&&slots[1].getItem()==ash) { slots[1].grow(1); return true; } return false;
    }
    private void updatePorts(ServerLevel server) {
        var loaded=new ArrayList<BlockPos>();
        for(BlockPos port:portPositions(worldPosition,getBlockState())) if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port))) loaded.add(port);
        if(!currentPorts.equals(loaded)||node==null||node.isExpired()) {
            disconnect(); currentPorts=List.copyOf(loaded);
            if(!loaded.isEmpty()) {
                Direction back=DummyableBlock.facing(getBlockState()).getOpposite(); NodeConnection[] connections=loaded.stream().map(port->new NodeConnection(port.relative(back),back)).toArray(NodeConnection[]::new); BlockPos[] positions=loaded.toArray(BlockPos[]::new);
                node=new PowerNode(positions).setConnections(connections); HbmEnergy.nodespace(server).createNode(node); fluidPorts=new FluidPorts(positions,connections);
            }
        }
        if(node!=null&&node.hasValidNet()) node.addProvider(this);
        if(fluidPorts!=null) fluidPorts.update(server,this);
    }
    private void disconnect() { if(level instanceof ServerLevel server) { if(node!=null) HbmEnergy.nodespace(server).destroyNode(node); if(fluidPorts!=null) fluidPorts.remove(server); } node=null; fluidPorts=null; currentPorts=List.of(); }
    @Override public void onChunkUnloaded() { disconnect(); super.onChunkUnloaded(); }
    @Override public void setRemoved() { disconnect(); super.setRemoved(); }
    @Override public PowerNode energyNode() { return node; }
    @Override public long getPower() { return power; }
    @Override public long getMaxPower() { return MAX_POWER; }
    @Override public void setPower(long power) { this.power=power; setChanged(); }
    @Override public boolean isLoaded() { return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition); }
    @Override public boolean canConnect(Direction side) { return side==DummyableBlock.facing(getBlockState()).getOpposite(); }
    @Override public boolean canConnect(HbmFluidType type,Direction side) { return canConnect(side); }
    @Override public FluidTank getTankToPaste() { return tank; }
    @Override public FluidTank[] getAllTanks() { return new FluidTank[]{tank}; }
    @Override public FluidTank[] getReceivingTanks() { return getAllTanks(); }
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount) { long remaining=StandardFluidReceiver.super.transferFluid(type,pressure,amount); if(remaining!=amount) setChanged(); return remaining; }
    @Override public java.util.List<ProgressView> progressBars() { return java.util.List.of(new ProgressView("jade.hbm.fuel_remaining",burnTime,maxBurnTime,0xE69B51)); }
    @Override public long energyStored() { return power; } @Override public long energyCapacity() { return MAX_POWER; }
    @Override public List<TankView> tanks() { return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())); }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.saveAdditional(tag,lookup); var items=NonNullList.withSize(6,ItemStack.EMPTY); for(int i=0;i<6;i++) items.set(i,slots[i]); ContainerHelper.saveAllItems(tag,items,lookup); tag.putLong("power",power); tag.putInt("burnTime",burnTime); tag.putInt("maxBurnTime",maxBurnTime); tag.putBoolean("isOn",on); tag.putBoolean("liquidBurn",liquidBurn); tank.writeToNBT(tag,"t"); if(customName!=null) tag.putString("CustomName",Component.Serializer.toJson(customName,lookup)); }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.loadAdditional(tag,lookup); var items=NonNullList.withSize(6,ItemStack.EMPTY); ContainerHelper.loadAllItems(tag,items,lookup); for(int i=0;i<6;i++) slots[i]=items.get(i); power=tag.getLong("power"); burnTime=tag.getInt("burnTime"); maxBurnTime=tag.getInt("maxBurnTime"); on=tag.getBoolean("isOn"); liquidBurn=tag.getBoolean("liquidBurn"); tank.readFromNBT(tag,"t"); if(tag.contains("CustomName")) customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag=new CompoundTag(); tag.putLong("power",power); tag.putInt("burnTime",burnTime); tag.putInt("maxBurnTime",maxBurnTime); tag.putInt("powerGen",powerGen); tag.putBoolean("isOn",on); tag.putBoolean("liquidBurn",liquidBurn); tank.writeToNBT(tag,"t"); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup) { power=tag.getLong("power"); burnTime=tag.getInt("burnTime"); maxBurnTime=tag.getInt("maxBurnTime"); powerGen=tag.getInt("powerGen"); on=tag.getBoolean("isOn"); liquidBurn=tag.getBoolean("liquidBurn"); tank.readFromNBT(tag,"t"); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(),lookup); }
    @Override public int getContainerSize() { return slots.length; }
    @Override public boolean isEmpty() { return Arrays.stream(slots).allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slots[slot]; }
    @Override public ItemStack removeItem(int slot,int count) { ItemStack result=slots[slot].split(count); if(!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { ItemStack stack=slots[slot]; slots[slot]=ItemStack.EMPTY; return stack; }
    @Override public void setItem(int slot,ItemStack stack) { slots[slot]=stack; if(stack.getCount()>64) stack.setCount(64); setChanged(); }
    @Override public void clearContent() { Arrays.fill(slots,ItemStack.EMPTY); setChanged(); }
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack) { return slot==0&&WoodBurnerFuel.burnTime(stack)>0; }
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0,1}; }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side) { return canPlaceItem(slot,stack); }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side) { return slot==1; }
}
