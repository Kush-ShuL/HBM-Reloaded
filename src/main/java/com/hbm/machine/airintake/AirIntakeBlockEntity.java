package com.hbm.machine.airintake;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.DummyableBlock;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Actual source AIR production; no inventory, GUI, environmental prerequisite or OC methods exist. */
public final class AirIntakeBlockEntity extends BlockEntity implements EnergyReceiver,SidedEnergyConnector,StandardFluidSender,MachineView, com.hbm.multiblock.MuffledMachine {
    public static final BigInteger CAPACITY=BigInteger.valueOf(2000),CONSUMPTION=BigInteger.valueOf(100);
    public static Consumer<AirIntakeBlockEntity> clientTick=machine->{},clientRemoved=machine->{};
    public final FluidTank compair=new FluidTank(HbmFluids.AIR,1000);
    private BigInteger power=BigInteger.ZERO;
    public float fan,prevFan;
    public boolean muffled,tilted;
    private List<PowerNode> nodes=List.of();
    private FluidPorts fluidPorts;
    private List<BlockPos> activePorts=List.of();
    private CompoundTag lastPacked;
    public AirIntakeBlockEntity(BlockPos pos,BlockState state){super(AirIntakeContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public record Port(BlockPos position,Direction first,Direction second){NodeConnection[] connections(){return new NodeConnection[]{new NodeConnection(position.relative(first),first),new NodeConnection(position.relative(second),second)};}}
    /** TilePortShapes.solderer; Forge1.7 UP uses its left-hand matrix (modern clockwise). */
    public static List<Port> portDefinitions(BlockPos core,BlockState state){var dir=DummyableBlock.facing(state);var rot=dir.getClockWise();return List.of(new Port(core,dir,rot.getOpposite()),new Port(core.relative(rot),dir,rot),new Port(core.relative(dir.getOpposite()),dir.getOpposite(),rot.getOpposite()),new Port(core.relative(dir.getOpposite()).relative(rot),dir.getOpposite(),rot));}
    public boolean portConnects(BlockPos position,Direction side){return side!=null&&portDefinitions(worldPosition,getBlockState()).stream().anyMatch(port->port.position().equals(position)&&(port.first()==side||port.second()==side));}
    private void updatePorts(ServerLevel server){var valid=portDefinitions(worldPosition,getBlockState()).stream().filter(port->server.hasChunkAt(port.position())&&server.getBlockState(port.position()).is(getBlockState().getBlock())&&(DummyableBlock.isCore(server.getBlockState(port.position()))||DummyableBlock.isPort(server.getBlockState(port.position())))).toList();var positions=valid.stream().map(Port::position).toList();
        if(!positions.equals(activePorts)||nodes.stream().anyMatch(PowerNode::isExpired)){disconnect();activePorts=List.copyOf(positions);var rebuilt=new ArrayList<PowerNode>();var connections=new ArrayList<NodeConnection>();for(var port:valid){var node=new PowerNode(port.position()).setConnections(port.connections());HbmEnergy.nodespace(server).createNode(node);rebuilt.add(node);connections.addAll(Arrays.asList(port.connections()));}nodes=List.copyOf(rebuilt);if(!positions.isEmpty())fluidPorts=new FluidPorts(positions.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}
        // Original HE manyToMany4 separate nodes, fluid oneToMany1 combined node for the one sending tank.
        for(var node:nodes)if(node.hasValidNet())node.addReceiver(this);if(fluidPorts!=null)fluidPorts.update(server,this);
    }
    public static void tick(Level level,BlockPos pos,BlockState state,AirIntakeBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else machine.animate();}
    private void serverTick(ServerLevel server){updatePorts(server);if(power.compareTo(CONSUMPTION)>=0){compair.setFill(compair.getMaxFill());power=power.subtract(CONSUMPTION);}setChanged();var packed=getUpdateTag(server.registryAccess());if(!packed.equals(lastPacked)||server.getGameTime()%20==0){lastPacked=packed.copy();var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);dispatchScope.commit();}}}
    private void animate(){prevFan=fan;if(power.compareTo(CONSUMPTION)>=0){fan+=45;if(fan>=360){fan-=360;prevFan-=360;}}clientTick.accept(this);}
    public boolean running(){return power.compareTo(CONSUMPTION)>=0;}
    private static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getPower(){return legacy(power);}@Override public long getMaxPower(){return 2000;}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return CAPACITY;}@Override public void setPower(long amount){setPowerExact(BigInteger.valueOf(amount));}@Override public void setPowerExact(BigInteger amount){power=amount;setChanged();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side!=null&&side.getAxis().isHorizontal();}@Override public boolean canConnect(HbmFluidType type,Direction side){return type==HbmFluids.AIR&&canConnect(side);}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{compair};}@Override public FluidTank[] getSendingTanks(){return getAllTanks();}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidSender.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 2000;}@Override public List<TankView> tanks(){return List.of(new TankView(compair.getTankType(),compair.getFill(),compair.getMaxFill()));}
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes)if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}nodes=List.of();activePorts=List.of();fluidPorts=null;}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.onChunkUnloaded();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putString("powerExact",power.toString());if(power.bitLength()<64)tag.putLong("power",power.longValue());compair.writeToNBT(tag,"compair");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);power=tag.contains("powerExact")?new BigInteger(tag.getString("powerExact")):BigInteger.valueOf(tag.getLong("power"));compair.readFromNBT(tag,"compair");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveAdditional(tag,lookup);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadAdditional(tag,lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
