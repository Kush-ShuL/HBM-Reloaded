package com.hbm.machine.turbine;
import com.hbm.api.computer.*;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.DummyableBlock;
import java.math.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Actual TileEntityTurbineBase plus IndustrialMk2 flywheel, using exact native HE and private fluids. */
public final class IndustrialTurbineBlockEntity extends BlockEntity implements EnergyProvider,EnergyConductor,StandardFluidTransceiver,IFluidCopiable,MachineView,ComputerControl, com.hbm.multiblock.MuffledMachine {
    public static final BigInteger FLYWHEEL_MASS=BigInteger.valueOf(50000000);
    public static Consumer<IndustrialTurbineBlockEntity> clientTick=machine->{},clientRemoved=machine->{};
    public final FluidTank[] tanks={new FluidTank(HbmFluids.STEAM,IndustrialTurbineConfig.inputTankSize),new FluidTank(HbmFluids.SPENTSTEAM,IndustrialTurbineConfig.outputTankSize)};
    private BigInteger powerBuffer=BigInteger.ZERO,maxPower=BigInteger.ZERO,lastPowerTarget=BigInteger.ZERO,flywheelEnergy=BigInteger.ZERO;
    public double spin;public float rotor,lastRotor;public final float audioDesync=new Random().nextFloat()*.05F;
    public boolean operational,muffled,tilted;
    private PowerNode node;private FluidPorts ports;private List<BlockPos> activeFluidPorts=List.of();private BlockPos activePowerPort;
    private CompoundTag inletSnapshot,outletSnapshot;
    private int inputConsumed,outputProduced;private BigInteger generated=BigInteger.ZERO;
    public IndustrialTurbineBlockEntity(BlockPos pos,BlockState state){super(IndustrialTurbineContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public static BlockPos[] fluidPortPositions(BlockPos core,BlockState state){Direction direction=DummyableBlock.facing(state),side=direction.getClockWise();return new BlockPos[]{core.relative(direction,3).relative(side),core.relative(direction,3).relative(side.getOpposite()),core.relative(direction.getOpposite()).relative(side),core.relative(direction.getOpposite()).relative(side.getOpposite()),core.relative(direction,3).above(2),core.relative(direction.getOpposite()).above(2)};}
    public static BlockPos powerPortPosition(BlockPos core,BlockState state){return core.relative(DummyableBlock.facing(state).getOpposite(),3).above();}
    public static Direction fluidPortSide(BlockPos core,BlockState state,BlockPos port){var points=fluidPortPositions(core,state);Direction side=DummyableBlock.facing(state).getClockWise();for(int i=0;i<points.length;i++)if(points[i].equals(port))return i>=4?Direction.UP:i%2==0?side:side.getOpposite();return null;}
    public static void tick(Level level,BlockPos pos,BlockState state,IndustrialTurbineBlockEntity turbine){if(level instanceof ServerLevel server)turbine.serverTick(server);else turbine.clientTick();}
    private boolean livePort(ServerLevel server,BlockPos pos){return server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos));}
    private void updatePorts(ServerLevel server){
        BlockPos power=powerPortPosition(worldPosition,getBlockState());
        if(!livePort(server,power)){removePowerNode();}else if(node==null||node.isExpired()||!power.equals(activePowerPort)){removePowerNode();activePowerPort=power;Direction side=DummyableBlock.facing(getBlockState()).getOpposite();node=new PowerNode(power).setConnections(new NodeConnection(power.relative(side),side));HbmEnergy.nodespace(server).createNode(node);}
        if(node!=null&&node.hasValidNet())node.addProvider(this);
        var loaded=new ArrayList<BlockPos>();for(var pos:fluidPortPositions(worldPosition,getBlockState()))if(livePort(server,pos))loaded.add(pos);
        if(!loaded.equals(activeFluidPorts)){removeFluidPorts();activeFluidPorts=List.copyOf(loaded);if(!loaded.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var pos:loaded){Direction side=fluidPortSide(worldPosition,getBlockState(),pos);connections.add(new NodeConnection(pos.relative(side),side));}ports=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}
        if(ports!=null)ports.update(server,this);
    }
    private void serverTick(ServerLevel server){
        updatePorts(server);powerBuffer=BigInteger.ZERO;inputConsumed=outputProduced=0;generated=BigInteger.ZERO;
        inletSnapshot=new CompoundTag();tanks[0].writeToNBT(inletSnapshot,"water");operational=false;
        var trait=tanks[0].getTankType().getTrait(FluidTraits.Coolable.class);boolean valid=false;
        if(trait!=null){double efficiency=trait.getEfficiency(FluidTraits.CoolingType.TURBINE)*IndustrialTurbineConfig.efficiency;if(efficiency>0){
            tanks[1].setTankType(trait.coolsTo());
            int inputOps=(int)(Math.min(Math.ceil(tanks[0].getFill()*.2D),tanks[0].getFill())/trait.amountReq());
            int outputOps=(tanks[1].getMaxFill()-tanks[1].getFill())/trait.amountProduced();int operations=Math.min(inputOps,outputOps);
            if(operations>0){inputConsumed=operations*trait.amountReq();outputProduced=operations*trait.amountProduced();tanks[0].setFill(tanks[0].getFill()-inputConsumed);tanks[1].setFill(tanks[1].getFill()+outputProduced);
                generated=scaled(BigInteger.valueOf(operations).multiply(BigInteger.valueOf(trait.heatEnergy())),efficiency);
                int maxOperations=(int)Math.ceil(tanks[0].getMaxFill()*.2D/trait.amountReq());maxPower=scaled(BigInteger.valueOf(maxOperations).multiply(BigInteger.valueOf(trait.heatEnergy())),efficiency);flywheelEnergy=flywheelEnergy.add(generated);
            }valid=true;operational=operations>0;
        }}
        spin=flywheelEnergy.doubleValue()/FLYWHEEL_MASS.doubleValue();
        // Same source max(spin,.05) discharge rule; exact rational fallback retains huge-HE support.
        BigInteger target=Double.isFinite(spin)?scaled(maxPower,Math.max(spin,.05)):maxPower.multiply(flywheelEnergy).divide(FLYWHEEL_MASS);
        lastPowerTarget=target.min(flywheelEnergy);flywheelEnergy=flywheelEnergy.subtract(lastPowerTarget);powerBuffer=lastPowerTarget;
        outletSnapshot=new CompoundTag();tanks[1].writeToNBT(outletSnapshot,"steam");if(!valid)tanks[1].setTankType(HbmFluids.NONE);
        setChanged();var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);
    }
    private static BigInteger scaled(BigInteger value,double factor){double original=value.doubleValue()*factor;if(value.bitLength()<63&&Double.isFinite(original)&&original>=-0x1.0p63&&original<0x1.0p63)return BigInteger.valueOf((long)original);return new BigDecimal(value).multiply(BigDecimal.valueOf(factor)).toBigInteger();}
    private void clientTick(){lastRotor=rotor;float speed=spin>=.5?30:(float)(Math.pow(spin*2,.5)*30);rotor+=speed;if(rotor>=360){lastRotor-=360;rotor-=360;}clientTick.accept(this);}
    public void onLeverPull(){var type=tanks[0].getTankType();int resize=0;
        if(type==HbmFluids.STEAM){tanks[0].setTankType(HbmFluids.HOTSTEAM);tanks[1].setTankType(HbmFluids.STEAM);resize=10;}
        else if(type==HbmFluids.HOTSTEAM){tanks[0].setTankType(HbmFluids.SUPERHOTSTEAM);tanks[1].setTankType(HbmFluids.HOTSTEAM);resize=10;}
        else if(type==HbmFluids.SUPERHOTSTEAM){tanks[0].setTankType(HbmFluids.ULTRAHOTSTEAM);tanks[1].setTankType(HbmFluids.SUPERHOTSTEAM);resize=10;}
        else if(type==HbmFluids.ULTRAHOTSTEAM){tanks[0].setTankType(HbmFluids.STEAM);tanks[1].setTankType(HbmFluids.SPENTSTEAM);resize=-1000;}
        else{tanks[0].setTankType(HbmFluids.STEAM);tanks[1].setTankType(HbmFluids.SPENTSTEAM);}
        if(resize>0){tanks[0].changeTankSize(tanks[0].getMaxFill()/resize);tanks[1].changeTankSize(tanks[1].getMaxFill()/resize);}else if(resize<0){tanks[0].changeTankSize(tanks[0].getMaxFill()*-resize);tanks[1].changeTankSize(tanks[1].getMaxFill()*-resize);}setChanged();
    }
    @Override public boolean canConnect(Direction side){return side==DummyableBlock.facing(getBlockState()).getOpposite();}
    @Override public boolean canConnect(HbmFluidType type,Direction side){if(type.getTrait(FluidTraits.Coolable.class)==null&&type!=HbmFluids.SPENTSTEAM)return false;Direction direction=DummyableBlock.facing(getBlockState());return side!=null&&side!=direction&&side!=direction.getOpposite();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public PowerNode energyNode(){return node;}
    private static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getPower(){return legacy(powerBuffer);}@Override public long getMaxPower(){return getPower();}@Override public long getProviderSpeed(){return getPower();}
    @Override public BigInteger getPowerExact(){return powerBuffer;}@Override public BigInteger getMaxPowerExact(){return powerBuffer;}@Override public BigInteger getProviderSpeedExact(){return powerBuffer;}
    @Override public void setPower(long power){setPowerExact(BigInteger.valueOf(power));}@Override public void setPowerExact(BigInteger power){powerBuffer=power;setChanged();}
    @Override public void usePower(long amount){usePowerExact(BigInteger.valueOf(amount));}@Override public void usePowerExact(BigInteger amount){if(amount.signum()<0||amount.compareTo(powerBuffer)>0)throw new IllegalArgumentException("Unavailable HE requested");powerBuffer=powerBuffer.subtract(amount);setChanged();}
    public BigInteger flywheelEnergy(){return flywheelEnergy;}public BigInteger theoreticalPower(){return maxPower;}public BigInteger generatedPower(){return generated;}public int inputConsumed(){return inputConsumed;}public int outputProduced(){return outputProduced;}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public FluidTank getTankToPaste(){return null;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return legacy(maxPower);}@Override public BigInteger energyStoredExact(){return powerBuffer;}@Override public BigInteger energyCapacityExact(){return maxPower;}
    @Override public List<TankView> tanks(){return List.of(new TankView(tanks[0].getTankType(),tanks[0].getFill(),tanks[0].getMaxFill()),new TankView(tanks[1].getTankType(),tanks[1].getFill(),tanks[1].getMaxFill()));}
    @Override public List<ProgressView> progressBars(){double colorSpin=Math.max(0,Math.min(1,spin));int color=((int)(255-255*colorSpin)<<16)|((int)(255*colorSpin)<<8);return List.of(new ProgressView("jade.hbm.industrial_turbine.flywheel",spin,1,color));}
    public String[] getFunctionInfo(){return new String[]{"VAL:output","VAL:flywheel"};}public String provideRORValue(String name){return switch(name){case "VAL:output"->Integer.toString(powerBuffer.intValue());case "VAL:flywheel"->Integer.toString((int)(spin*100));default->null;};}
    private int steamType(){var type=tanks[0].getTankType();return type==HbmFluids.HOTSTEAM?1:type==HbmFluids.SUPERHOTSTEAM?2:type==HbmFluids.ULTRAHOTSTEAM?3:0;}
    private Object computerPower(){return powerBuffer.bitLength()<64?powerBuffer.longValue():powerBuffer.toString();}
    @Override public String computerType(){return "ntm_turbine";}@Override public String[] computerMethods(){return new String[]{"getFluid","getType","getPower","getFlywheel","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getFluid"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};case "getType"->new Object[]{steamType()};case "getPower"->new Object[]{computerPower()};case "getFlywheel"->new Object[]{(int)(spin*100)};case "getInfo"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill(),steamType(),computerPower(),(int)(spin*100)};default->throw new ComputerException("No such method: "+method);};}
    private void removePowerNode(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;activePowerPort=null;}
    private void removeFluidPorts(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;activeFluidPorts=List.of();}
    private void disconnect(){removePowerNode();removeFluidPorts();if(level!=null&&level.isClientSide)clientRemoved.accept(this);}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    private static BigInteger readHE(CompoundTag tag,String name){return tag.contains(name,Tag.TAG_STRING)?new BigInteger(tag.getString(name)):BigInteger.valueOf(tag.getLong(name));}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tanks[0].writeToNBT(tag,"water");tanks[1].writeToNBT(tag,"steam");tag.putString("power",powerBuffer.toString());tag.putString("lastPowerTarget",lastPowerTarget.toString());tag.putString("flywheel_energy",flywheelEnergy.toString());tag.putString("maxPower",maxPower.toString());tag.putDouble("spin",spin);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");powerBuffer=readHE(tag,"power");lastPowerTarget=readHE(tag,"lastPowerTarget");flywheelEnergy=readHE(tag,"flywheel_energy");maxPower=readHE(tag,"maxPower");spin=tag.getDouble("spin");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();if(inletSnapshot!=null)tag.merge(inletSnapshot);else tanks[0].writeToNBT(tag,"water");if(outletSnapshot!=null)tag.merge(outletSnapshot);else tanks[1].writeToNBT(tag,"steam");tag.putString("power",powerBuffer.toString());tag.putString("maxPower",maxPower.toString());tag.putDouble("spin",spin);tag.putBoolean("operational",operational);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");powerBuffer=readHE(tag,"power");maxPower=readHE(tag,"maxPower");spin=tag.getDouble("spin");operational=tag.getBoolean("operational");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
