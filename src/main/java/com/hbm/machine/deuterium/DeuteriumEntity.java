package com.hbm.machine.deuterium;

import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.*;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Original zero-slot extractor; tower changes only tanks/capacity/ports, keeping the same source conversion arithmetic. */
public class DeuteriumEntity extends BlockEntity implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable,MachineView,WorldlyContainer,NamedMultiblock,MuffledMachine {
    public final FluidTank[] tanks;
    private final boolean tower;
    private BigInteger power=BigInteger.ZERO;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
    private FluidPorts fluidPorts;
    private CompoundTag lastPacked;
    public boolean muffled,tilted;
    private Component customName;
    public DeuteriumEntity(BlockPos pos,BlockState state){this(DeuteriumContent.EXTRACTOR_ENTITY.get(),pos,state,false);}
    protected DeuteriumEntity(BlockEntityType<?> type,BlockPos pos,BlockState state,boolean tower){super(type,pos,state);this.tower=tower;tanks=new FluidTank[]{new FluidTank(HbmFluids.WATER,tower?50000:1000),new FluidTank(HbmFluids.fromName("HEAVYWATER"),tower?5000:100)};}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&(!state.hasProperty(DummyableBlock.LEGACY_META)||DummyableBlock.isCore(state));}
    public boolean isTower(){return tower;}
    public long usage(){return getMaxPower()/20;}
    public boolean hasPower(){return power.compareTo(BigInteger.valueOf(usage()))>=0;}
    public static void tick(Level level,BlockPos pos,BlockState state,DeuteriumEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    private void serverTick(ServerLevel server){
        updatePorts(server);
        if(hasPower()&&tanks[0].getFill()>=DeuteriumProcess.MINIMUM_WATER&&tanks[1].getMaxFill()>tanks[1].getFill()){
            int convert=DeuteriumProcess.convert(tanks[0].getFill(),tanks[1].getFill(),tanks[1].getMaxFill());
            tanks[0].setFill(tanks[0].getFill()-convert*DeuteriumProcess.WATER_PER_HEAVY);
            tanks[1].setFill(tanks[1].getFill()+convert);power=power.subtract(BigInteger.valueOf(usage()));setChanged();
        }
        CompoundTag packet=getUpdateTag(server.registryAccess());
        if(server.getGameTime()%20==0||!packet.equals(lastPacked)){
            lastPacked=packet.copy();var update=ClientboundBlockEntityDataPacket.create(this);
            for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(update);
        }
    }
    private NodeConnection[] connections(){
        if(tower)return DeuteriumTowerEntity.connections(worldPosition,DummyableBlock.facing(getBlockState()));
        return Arrays.stream(Direction.values()).map(d->new NodeConnection(worldPosition.relative(d),d)).toArray(NodeConnection[]::new);
    }
    private void updatePorts(ServerLevel server){
        if(fluidPorts==null||nodes.values().stream().anyMatch(PowerNode::isExpired)){
            disconnect();var connections=connections();var grouped=new LinkedHashMap<BlockPos,List<NodeConnection>>();
            for(var c:connections)grouped.computeIfAbsent(c.origin(),unused->new ArrayList<>()).add(c);
            for(var entry:grouped.entrySet()){
                var node=new PowerNode(entry.getKey()).setConnections(entry.getValue().toArray(NodeConnection[]::new));
                HbmEnergy.nodespace(server).createNode(node);nodes.put(entry.getKey(),node);
            }
            fluidPorts=new FluidPorts(grouped.keySet().toArray(BlockPos[]::new),connections);
        }
        fluidPorts.update(server,this);for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){nodes.values().forEach(n->HbmEnergy.nodespace(server).destroyNode(n));if(fluidPorts!=null)fluidPorts.remove(server);}nodes.clear();fluidPorts=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public long getPower(){return power.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getMaxPower(){return tower?100000:10000;}
    @Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(getMaxPower());}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}
    @Override public FluidTank getTankToPaste(){return null;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount!=0)setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}
    @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    private void writeState(CompoundTag tag){if(power.bitLength()<64)tag.putLong("power",power.longValue());else tag.putString("power",power.toString());tanks[0].writeToNBT(tag,"water");tanks[1].writeToNBT(tag,"heavyWater");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    private void readState(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"heavyWater");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);writeState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeState(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    public Component getDisplayName(){return customName==null?Component.translatable("container.deuterium"):customName;}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public int getContainerSize(){return 0;}@Override public boolean isEmpty(){return true;}@Override public ItemStack getItem(int slot){return ItemStack.EMPTY;}@Override public ItemStack removeItem(int slot,int count){return ItemStack.EMPTY;}@Override public ItemStack removeItemNoUpdate(int slot){return ItemStack.EMPTY;}@Override public void setItem(int slot,ItemStack stack){}@Override public void clearContent(){}
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
}
