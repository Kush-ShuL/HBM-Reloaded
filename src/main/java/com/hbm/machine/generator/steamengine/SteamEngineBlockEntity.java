package com.hbm.machine.generator.steamengine;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import java.math.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Exact active piston-steam conversion, per-tick HE buffer and three-ply synchronized rotor. */
public final class SteamEngineBlockEntity extends BlockEntity implements EnergyProvider,SidedEnergyConnector,StandardFluidTransceiver,IFluidCopiable,MachineView, com.hbm.multiblock.MuffledMachine {
    public final FluidTank[] tanks={new FluidTank(HbmFluids.STEAM,SteamEngineConfig.steamCapacity),new FluidTank(HbmFluids.SPENTSTEAM,SteamEngineConfig.spentCapacity)};
    private BigInteger powerBuffer=BigInteger.ZERO;public float rotor,lastRotor;private float syncRotor,acceleration;private int turnProgress;public boolean muffled,tilted;
    private List<PowerNode> nodes=List.of();private FluidPorts ports;private List<BlockPos> activePorts=List.of();private CompoundTag inletSnapshot;
    public SteamEngineBlockEntity(BlockPos pos,BlockState state){super(SteamEngineContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public static Direction portSide(BlockState state){return DummyableBlock.facing(state).getCounterClockWise();}
    public static BlockPos[] portPositions(BlockPos core,BlockState state){Direction front=DummyableBlock.facing(state),side=portSide(state);BlockPos middle=core.relative(side).above();return new BlockPos[]{middle,middle.relative(front),middle.relative(front.getOpposite())};}
    private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition,getBlockState()))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos)))loaded.add(pos);
        if(!loaded.equals(activePorts)||nodes.stream().anyMatch(PowerNode::isExpired)){disconnect();activePorts=List.copyOf(loaded);Direction side=portSide(getBlockState());var rebuiltPower=new ArrayList<PowerNode>();var connections=new ArrayList<NodeConnection>();for(var pos:loaded){var connection=new NodeConnection(pos.relative(side),side);connections.add(connection);var node=new PowerNode(pos).setConnections(connection);HbmEnergy.nodespace(server).createNode(node);rebuiltPower.add(node);}nodes=List.copyOf(rebuiltPower);if(!loaded.isEmpty())ports=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}
        for(var node:nodes)if(node.hasValidNet())node.addProvider(this);if(ports!=null)ports.update(server,this);
    }
    public static BigInteger energy(int operations,FluidTraits.Coolable trait){double efficiency=trait.getEfficiency(FluidTraits.CoolingType.TURBINE)*SteamEngineConfig.efficiency;BigInteger heat=BigInteger.valueOf(operations).multiply(BigInteger.valueOf(trait.heatEnergy()));double original=heat.doubleValue()*efficiency;if(heat.bitLength()<63&&Double.isFinite(original)&&original>=-0x1.0p63&&original<0x1.0p63)return BigInteger.valueOf((long)original);return new BigDecimal(heat).multiply(BigDecimal.valueOf(efficiency)).toBigInteger();}
    public static void tick(Level level,BlockPos pos,BlockState state,SteamEngineBlockEntity engine){if(level instanceof ServerLevel server)engine.serverTick(server);else engine.clientTick();}
    private void serverTick(ServerLevel server){updatePorts(server);powerBuffer=BigInteger.ZERO;tanks[0].setTankType(HbmFluids.STEAM);tanks[1].setTankType(HbmFluids.SPENTSTEAM);inletSnapshot=new CompoundTag();tanks[0].writeToNBT(inletSnapshot,"s");var trait=tanks[0].getTankType().getTrait(FluidTraits.Coolable.class);int inputOps=tanks[0].getFill()/trait.amountReq(),outputOps=(tanks[1].getMaxFill()-tanks[1].getFill())/trait.amountProduced(),operations=Math.min(inputOps,outputOps);tanks[0].setFill(tanks[0].getFill()-operations*trait.amountReq());tanks[1].setFill(tanks[1].getFill()+operations*trait.amountProduced());powerBuffer=energy(operations,trait);
        acceleration=Mth.clamp(acceleration+(operations>0?.1F:-.1F),0,40);rotor+=acceleration;if(rotor>=360){rotor-=360;server.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("block.steamEngineOperate"),SoundSource.BLOCKS,muffled?.1F:1F,.5F+acceleration/80F);}setChanged();var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);
    }
    private void clientTick(){lastRotor=rotor;if(turnProgress>0){double difference=Mth.wrapDegrees((double)syncRotor-rotor);rotor=(float)(rotor+difference/turnProgress);--turnProgress;}else rotor=syncRotor;}
    @Override public boolean canConnect(Direction side){return side!=null&&side.getAxis().isHorizontal();}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    private static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getPower(){return legacy(powerBuffer);}@Override public long getMaxPower(){return getPower();}@Override public long getProviderSpeed(){return getPower();}@Override public BigInteger getPowerExact(){return powerBuffer;}@Override public BigInteger getMaxPowerExact(){return powerBuffer;}@Override public BigInteger getProviderSpeedExact(){return powerBuffer;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public void setPowerExact(BigInteger value){powerBuffer=value;setChanged();}@Override public void usePower(long value){usePowerExact(BigInteger.valueOf(value));}@Override public void usePowerExact(BigInteger value){if(value.signum()<0||value.compareTo(powerBuffer)>0)throw new IllegalArgumentException("Unavailable HE requested");powerBuffer=powerBuffer.subtract(value);setChanged();}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public FluidTank getTankToPaste(){return null;}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remaining=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(remaining!=amount)setChanged();return remaining;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getPower();}@Override public BigInteger energyStoredExact(){return powerBuffer;}@Override public BigInteger energyCapacityExact(){return powerBuffer;}@Override public List<TankView> tanks(){return List.of(new TankView(tanks[0].getTankType(),tanks[0].getFill(),tanks[0].getMaxFill()),new TankView(tanks[1].getTankType(),tanks[1].getFill(),tanks[1].getMaxFill()));}
    private static BigInteger readHE(CompoundTag tag){return tag.contains("powerBuffer",Tag.TAG_STRING)?new BigInteger(tag.getString("powerBuffer")):BigInteger.valueOf(tag.getLong("powerBuffer"));}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putString("powerBuffer",powerBuffer.toString());tag.putFloat("acceleration",acceleration);tanks[0].writeToNBT(tag,"s");tanks[1].writeToNBT(tag,"w");tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);powerBuffer=readHE(tag);acceleration=tag.getFloat("acceleration");tanks[0].readFromNBT(tag,"s");tanks[1].readFromNBT(tag,"w");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();if(inletSnapshot!=null)tag.merge(inletSnapshot);else tanks[0].writeToNBT(tag,"s");tanks[1].writeToNBT(tag,"w");tag.putString("powerBuffer",powerBuffer.toString());tag.putFloat("rotor",rotor);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){tanks[0].readFromNBT(tag,"s");tanks[1].readFromNBT(tag,"w");powerBuffer=readHE(tag);syncRotor=tag.getFloat("rotor");turnProgress=3;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:nodes)if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);if(ports!=null)ports.remove(server);}nodes=List.of();ports=null;activePorts=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
