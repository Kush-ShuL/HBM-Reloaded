package com.hbm.machine.converter;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.EnergyConductor;
import com.hbm.energy.EnergyHandler;
import com.hbm.energy.EnergyProvider;
import com.hbm.energy.EnergyReceiver;
import com.hbm.energy.HbmEnergy;
import com.hbm.energy.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

/** Source5M HE +1M RF buffers; the modern FE bridge exists only on these original converter blocks. */
public abstract class ConverterBlockEntity extends BlockEntity implements EnergyHandler,EnergyConductor,MachineView, com.hbm.multiblock.MuffledMachine {
    public static final long MAX_POWER=5_000_000L;
    public static final int MAX_RF=1_000_000;
    protected long power;
    private final Buffer storage=new Buffer();
    private PowerNode node;
    private boolean loaded=true;
    public boolean muffled,tilted;
    protected ConverterBlockEntity(BlockPos pos,BlockState state){super(ConverterContent.ENTITY.get(),pos,state);}
    public abstract boolean heToRf();
    protected abstract void convert(ServerLevel level);
    public int rfStored(){return storage.getEnergyStored();}
    protected void setRf(int value){storage.set(value);}
    protected int extractRf(int value,boolean simulate){return storage.extractEnergy(value,simulate);}
    public IEnergyStorage forgeEnergy(){return forgeEnergy;}
    private final IEnergyStorage forgeEnergy=new IEnergyStorage(){
        @Override public int receiveEnergy(int amount,boolean simulate){if(heToRf()||!isLoaded())return 0;int accepted=storage.receiveEnergy(amount,simulate);if(accepted>0&&!simulate)setChanged();return accepted;}
        @Override public int extractEnergy(int amount,boolean simulate){if(!heToRf()||!isLoaded())return 0;int extracted=storage.extractEnergy(amount,simulate);if(extracted>0&&!simulate)setChanged();return extracted;}
        @Override public int getEnergyStored(){return storage.getEnergyStored();}
        @Override public int getMaxEnergyStored(){return MAX_RF;}
        @Override public boolean canExtract(){return heToRf()&&isLoaded();}
        @Override public boolean canReceive(){return !heToRf()&&isLoaded();}
    };
    public static void tick(Level level,BlockPos pos,BlockState state,ConverterBlockEntity converter){if(level instanceof ServerLevel server)converter.tickServer(server);}
    private void tickServer(ServerLevel server){
        if(node==null||node.isExpired()){
            node=HbmEnergy.nodespace(server).getNode(worldPosition);
            if(node==null||node.isExpired()){node=new PowerNode(worldPosition);HbmEnergy.nodespace(server).createNode(node);}
        }
        // Original TilePort's first node update waits for an actual network before subscription.
        if(node.hasValidNet()){if(this instanceof EnergyReceiver receiver)node.addReceiver(receiver);if(this instanceof EnergyProvider provider)node.addProvider(provider);}
        convert(server);
        var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225)player.connection.send(packet);dispatchScope.commit();}
    }
    @Override public long getPower(){return power;}
    @Override public long getMaxPower(){return MAX_POWER;}
    @Override public void setPower(long value){if(power!=value){power=value;setChanged();}}
    @Override public long energyStored(){return power;}
    @Override public long energyCapacity(){return MAX_POWER;}
    @Override public PowerNode energyNode(){return node;}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public boolean isLoaded(){return loaded&&!isRemoved()&&level!=null;}
    private void removeNode(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void onLoad(){super.onLoad();loaded=true;}
    @Override public void onChunkUnloaded(){loaded=false;removeNode();super.onChunkUnloaded();}
    @Override public void setRemoved(){loaded=false;removeNode();super.setRemoved();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putLong("power",power);tag.putInt("Energy",rfStored());tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);power=tag.getLong("power");setRf(tag.getInt("Energy"));muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    private static final class Buffer extends EnergyStorage{
        Buffer(){super(MAX_RF,MAX_RF,MAX_RF);}
        void set(int value){energy=Math.max(0,Math.min(value,capacity));}
    }

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
