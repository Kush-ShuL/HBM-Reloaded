package com.hbm.machine.hephaestus;

import com.hbm.api.machine.IFluidCopiable;
import com.hbm.api.machine.MachineView;
import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.*;
import com.hbm.registry.HbmContent;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** TileEntityMachineHephaestus: environmental heat is a renewable operation budget, not stored TU. */
public final class HephaestusEntity extends BlockEntity implements StandardFluidTransceiver,IFluidCopiable,MachineView,MuffledMachine {
    public final FluidTank input = new FluidTank(HbmFluids.OIL,24_000);
    public final FluidTank output = new FluidTank(HbmFluids.HOTOIL,24_000);
    public int bufferedHeat;
    public float rot,prevRot;
    public boolean muffled,tilted;
    private final int[] heat = new int[10];
    private long fissureScanTime;
    private CompoundTag inputSnapshot;
    private FluidPorts fluidPorts;
    private List<NodeConnection> active = List.of();
    public static Consumer<HephaestusEntity> clientTick = machine -> {};
    public static Consumer<HephaestusEntity> clientRemoved = machine -> {};

    public HephaestusEntity(BlockPos pos,BlockState state){super(HephaestusContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    /** Eight actual TilePort definitions, independent of the authored model's facing. */
    public static List<NodeConnection> connections(BlockPos core){
        var result = new ArrayList<NodeConnection>(8);
        for(int y:new int[]{0,11})for(var direction:new Direction[]{Direction.EAST,Direction.WEST,Direction.SOUTH,Direction.NORTH}){
            var origin=core.above(y).relative(direction);
            result.add(new NodeConnection(origin.relative(direction),direction));
        }
        return List.copyOf(result);
    }
    public static void tick(Level level,BlockPos pos,BlockState state,HephaestusEntity machine){
        if(level instanceof ServerLevel server)machine.serverTick(server);else machine.tickClient();
    }
    private void serverTick(ServerLevel server){
        // Source setupFluidPorts/updatePortFIFO precede tank validation and this tick's conversion.
        updatePorts(server);
        setupTanks();
        int height=(int)(server.getGameTime()%10);
        int y=worldPosition.getY()-1-height;
        heat[height]=0;
        // Original y>=0 is the 1.7 build floor; the ten real layers remain ten in modern negative Y.
        if(y>=server.getMinBuildHeight()){
            for(int x=-7;x<=7;x++)for(int z=-7;z<=7;z++)
                heat[height]+=heatFromBlock(server,worldPosition.getX()+x,y,worldPosition.getZ()+z);
        }
        inputSnapshot=new CompoundTag();
        input.writeToNBT(inputSnapshot,"0"); // Original input packet is captured BEFORE heatFluid.
        heatFluid();
        bufferedHeat=getTotalHeat();
        setChanged();
        var packet=getUpdatePacket();
        for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<150D*150D)player.connection.send(packet);
    }
    private int heatFromBlock(ServerLevel server,int x,int y,int z){
        var block=server.getBlockState(new BlockPos(x,y,z)).getBlock();
        if(block==Blocks.LAVA)return 5; // Native still/flowing states share the actual lava block.
        if(block==HbmWorldFluids.block("volcanic_lava_block"))return 150;
        if(block==HbmContent.block("ore_volcano")){fissureScanTime=server.getGameTime();return 300;}
        return 0;
    }
    public int getTotalHeat(){
        int total=0;
        for(int value:heat)total+=value;
        // Preserve default timestamp zero, including the source's first-20-world-ticks multiplier.
        if(level!=null&&level.getGameTime()-fissureScanTime<20)total*=3;
        return total;
    }
    private void setupTanks(){
        var trait=input.getTankType().getTrait(FluidTraits.Heatable.class);
        if(trait!=null&&trait.getEfficiency(FluidTraits.HeatingType.HEATEXCHANGER)>0){
            output.setTankType(trait.getFirstStep().typeProduced());return;
        }
        input.setTankType(HbmFluids.NONE);
        output.setTankType(HbmFluids.NONE);
    }
    private void heatFluid(){
        var trait=input.getTankType().getTrait(FluidTraits.Heatable.class);
        if(trait==null)return;
        var step=trait.getFirstStep();
        int inputOps=input.getFill()/step.amountReq();
        int outputOps=(output.getMaxFill()-output.getFill())/step.amountProduced();
        int heatOps=getTotalHeat()/step.heatReq();
        int ops=Math.min(Math.min(inputOps,outputOps),heatOps);
        input.setFill(input.getFill()-step.amountReq()*ops);
        output.setFill(output.getFill()+step.amountProduced()*ops);
        // Do not deduct environmental heat, or multiply it by the admission-only efficiency.
        setChanged();
    }
    private void tickClient(){
        prevRot=rot;
        if(bufferedHeat>0)rot+=.5F;
        clientTick.accept(this); // Sound and the one-in-seven vanilla CLOUD use the actual client RNG.
        if(rot>=360F){prevRot-=360F;rot-=360F;}
    }
    private void updatePorts(ServerLevel server){
        var loaded=connections(worldPosition).stream().filter(c->server.hasChunkAt(c.origin())
            &&server.getBlockState(c.origin()).is(getBlockState().getBlock())
            &&DummyableBlock.isPort(server.getBlockState(c.origin()))).toList();
        if(!loaded.equals(active)){
            disconnect();active=List.copyOf(loaded);
            if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.stream().map(NodeConnection::origin).toArray(BlockPos[]::new),loaded.toArray(NodeConnection[]::new));
        }
        if(fluidPorts!=null)fluidPorts.update(server,this);
    }
    private void disconnect(){if(fluidPorts!=null&&level instanceof ServerLevel server)fluidPorts.remove(server);fluidPorts=null;active=List.of();}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side.getAxis()!=Direction.Axis.Y;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{input,output};}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{input};}
    @Override public FluidTank[] getSendingTanks(){return new FluidTank[]{output};}
    @Override public FluidTank getTankToPaste(){return null;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    @Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    private void writeState(CompoundTag tag){input.writeToNBT(tag,"0");output.writeToNBT(tag,"1");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    private void readState(CompoundTag tag){input.readFromNBT(tag,"0");output.readFromNBT(tag,"1");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);writeState(tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readState(tag);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeState(tag);if(inputSnapshot!=null)tag.merge(inputSnapshot.copy());tag.putInt("bufferedHeat",bufferedHeat);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);bufferedHeat=tag.getInt("bufferedHeat");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
