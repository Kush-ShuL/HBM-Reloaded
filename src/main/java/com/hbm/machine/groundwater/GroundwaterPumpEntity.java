package com.hbm.machine.groundwater;

import com.hbm.api.machine.*;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.*;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import io.netty.buffer.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Exact source TileEntityMachinePumpBase. Its missing resource NBT persistence is intentional source behavior. */
public abstract class GroundwaterPumpEntity extends BlockEntity implements StandardFluidTransceiver,IFluidCopiable,MachineView,MuffledMachine {
    public final GroundwaterPumpKind kind;
    public final FluidTank water;
    public boolean isOn,onGround,muffled,tilted;
    public float rotor,lastRotor;
    public int groundCheckDelay;
    private FluidPorts fluidPorts;
    private CompoundTag lastPacked;
    private static Set<Block> soils;
    protected GroundwaterPumpEntity(BlockEntityType<?> type,BlockPos pos,BlockState state,GroundwaterPumpKind kind){
        super(type,pos,state);this.kind=kind;water=new FluidTank(HbmFluids.WATER,kind.production()*100);
    }
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    public static NodeConnection[] connections(BlockPos core){
        return new NodeConnection[]{new NodeConnection(core.north(2),Direction.NORTH),new NodeConnection(core.east(2),Direction.EAST),new NodeConnection(core.south(2),Direction.SOUTH),new NodeConnection(core.west(2),Direction.WEST)};
    }
    private void updatePorts(ServerLevel server){
        if(fluidPorts==null){var cons=connections(worldPosition);fluidPorts=new FluidPorts(Arrays.stream(cons).map(NodeConnection::origin).toArray(BlockPos[]::new),cons);}
        fluidPorts.update(server,this); // Original FIFO before receiver-power subscription.
        updatePowerPorts(server);
    }
    protected void updatePowerPorts(ServerLevel server){}
    protected void removePowerPorts(ServerLevel server){}
    public static void tick(Level level,BlockPos pos,BlockState state,GroundwaterPumpEntity pump){
        if(level instanceof ServerLevel server)pump.serverTick(server);
        else{
            pump.lastRotor=pump.rotor;if(pump.isOn)pump.rotor+=10F;
            if(pump.rotor>=360F){pump.rotor-=360F;pump.lastRotor-=360F;
                level.playLocalSound(pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("block.steamEngineOperate"),SoundSource.BLOCKS,.5F,.75F,false);
                level.playLocalSound(pos.getX(),pos.getY(),pos.getZ(),SoundEvents.GENERIC_SPLASH,SoundSource.NEUTRAL,1F,.5F,false);
            }
        }
    }
    private void serverTick(ServerLevel server){
        updatePorts(server);
        if(groundCheckDelay>0)groundCheckDelay--;else onGround=checkGround();
        isOn=false;
        if(canOperate()&&worldPosition.getY()<=GroundwaterPumpSettings.values().groundHeight()&&onGround){isOn=true;operate();}
        var data=getUpdateTag(server.registryAccess());
        if(server.getGameTime()%20==0||!data.equals(lastPacked)){
            lastPacked=data.copy();var packet=getUpdatePacket();
            for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);
        }
    }
    protected abstract boolean canOperate();
    protected abstract void operate();
    private static synchronized Set<Block> soils(){
        if(soils==null){
            var actual=new HashSet<Block>(List.of(Blocks.GRASS_BLOCK,Blocks.DIRT,Blocks.COARSE_DIRT,Blocks.PODZOL,Blocks.SAND,Blocks.RED_SAND,Blocks.MYCELIUM));
            for(var name:List.of("waste_earth","dirt_dead","dirt_oily","sand_dirty","sand_dirty_red"))actual.add(HbmContent.block(name));
            soils=Set.copyOf(actual);
        }
        return soils;
    }
    protected boolean checkGround(){
        if(!level.dimensionType().hasSkyLight())return false;
        int valid=0,invalid=0;
        for(int x=-1;x<=1;x++)for(int y=-1;y>=-GroundwaterPumpSettings.values().groundDepth();y--)for(int z=-1;z<=1;z++){
            var pos=worldPosition.offset(x,y,z);var state=level.getBlockState(pos);
            if(y==-1&&!LegacyBlockTraits.isNormalCube(level,pos,state))return false;
            if(soils().contains(state.getBlock()))valid++;else invalid++;
        }
        return valid>=invalid;
    }
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{water};}
    @Override public FluidTank[] getSendingTanks(){return new FluidTank[]{water};}
    @Override public FluidTank[] getReceivingTanks(){return FluidTank.EMPTY_ARRAY;}
    @Override public FluidTank getTankToPaste(){return null;}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    @Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    protected void writeExtra(ByteBuf buffer){}
    protected void readExtra(ByteBuf buffer){}
    protected void writeExactPower(CompoundTag tag){}
    protected void readExactPower(CompoundTag tag){}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){
        var tag=new CompoundTag();var buffer=Unpooled.buffer();
        try{buffer.writeBoolean(isOn).writeBoolean(onGround);water.serialize(buffer);writeExtra(buffer);var data=new byte[buffer.readableBytes()];buffer.readBytes(data);tag.putByteArray("sourcePacket",data);}finally{buffer.release();}
        writeExactPower(tag);return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){
        if(!tag.contains("sourcePacket",7))return;
        var buffer=Unpooled.wrappedBuffer(tag.getByteArray("sourcePacket"));
        try{isOn=buffer.readBoolean();onGround=buffer.readBoolean();water.deserialize(buffer);readExtra(buffer);}finally{buffer.release();}
        readExactPower(tag);
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    private void disconnect(){if(level instanceof ServerLevel server){if(fluidPorts!=null)fluidPorts.remove(server);removePowerPorts(server);}fluidPorts=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
