package com.hbm.world.structure.templates.heatboiler;
import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.computer.*;
import com.hbm.api.machine.*;
import com.hbm.energy.NodeConnection;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.HeatSource;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.saveddata.TomSaveData;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original first-step BOILER conversion, heat diffusion, exact packet snapshots and persistent burst body. */
public final class HeatBoilerBlockEntity extends BlockEntity implements StandardFluidTransceiver,IFluidCopiable,MachineView,IRORValueProvider,ComputerControl, com.hbm.multiblock.MuffledMachine {
    public static Consumer<HeatBoilerBlockEntity> clientTick=boiler->{},clientRemoved=boiler->{};
    public final FluidTank[] tanks={new FluidTank(HbmFluids.WATER,16000),new FluidTank(HbmFluids.STEAM,1600000)};
    public int heat;public boolean isOn,hasExploded,muffled,tilted;
    private FluidPorts ports;private List<BlockPos> currentPorts=List.of();private CompoundTag inletSnapshot,lastPacket;private int snapshotHeat;
    public HeatBoilerBlockEntity(BlockPos pos,BlockState state){super(HeatBoilerContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public static Direction side(BlockState state){return DummyableBlock.facing(state).getClockWise();}
    public static BlockPos[] portPositions(BlockPos core,BlockState state){var direction=side(state);return new BlockPos[]{core.relative(direction),core.relative(direction.getOpposite()),core.above(3)};}
    public static Direction portSide(BlockPos core,BlockState state,BlockPos port){if(core.above(3).equals(port))return Direction.UP;var direction=side(state);if(core.relative(direction).equals(port))return direction;if(core.relative(direction.getOpposite()).equals(port))return direction.getOpposite();return null;}
    public static void tick(Level level,BlockPos pos,BlockState state,HeatBoilerBlockEntity boiler){if(level instanceof ServerLevel server)boiler.serverTick(server);else clientTick.accept(boiler);}
    private void serverTick(ServerLevel server){updatePorts(server);inletSnapshot=null;if(!hasExploded){setupTanks();tryPullHeat(server);snapshotHeat=heat;if(server.getBrightness(LightLayer.SKY,worldPosition)>7&&TomSaveData.forWorld(server).fire>1e-5)heat+=(HeatBoilerConfig.maxHeat-heat)*.000005D;inletSnapshot=new CompoundTag();tanks[0].writeToNBT(inletSnapshot,"water");isOn=false;tryConvert(server);}setChanged();var data=packetTag();if(lastPacket!=null&&lastPacket.equals(data)&&server.getGameTime()%20!=0)return;lastPacket=data.copy();var packet=ClientboundBlockEntityDataPacket.create(this,(machine,lookup)->data);for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625D)player.connection.send(packet);}
    private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition,getBlockState()))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos)))loaded.add(pos);if(!loaded.equals(currentPorts)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var pos:loaded){var direction=portSide(worldPosition,getBlockState(),pos);connections.add(new NodeConnection(pos.relative(direction),direction));}ports=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}if(ports!=null)ports.update(server,this);}
    private void tryPullHeat(ServerLevel server){if(server.getBlockEntity(worldPosition.below()) instanceof HeatSource source){int difference=source.getHeatStored()-heat;if(difference==0)return;if(difference>0){difference=(int)Math.ceil(difference*HeatBoilerConfig.diffusion);difference=Math.min(difference,HeatBoilerConfig.maxHeat-heat);source.useUpHeat(difference);heat+=difference;if(heat>HeatBoilerConfig.maxHeat)heat=HeatBoilerConfig.maxHeat;return;}}heat=Math.max(heat-Math.max(heat/1000,1),0);}
    private void setupTanks(){var trait=tanks[0].getTankType().getTrait(FluidTraits.Heatable.class);if(trait!=null&&trait.getEfficiency(FluidTraits.HeatingType.BOILER)>0){var entry=trait.getFirstStep();tanks[1].setTankType(entry.typeProduced());tanks[1].changeTankSize(tanks[0].getMaxFill()*entry.amountProduced()/entry.amountReq());return;}tanks[0].setTankType(HbmFluids.NONE);tanks[1].setTankType(HbmFluids.NONE);}
    private void tryConvert(ServerLevel server){var trait=tanks[0].getTankType().getTrait(FluidTraits.Heatable.class);if(trait==null||trait.getEfficiency(FluidTraits.HeatingType.BOILER)<=0)return;var entry=trait.getFirstStep();int required=(int)Math.max(entry.heatReq()/trait.getEfficiency(FluidTraits.HeatingType.BOILER),1),inputOps=tanks[0].getFill()/entry.amountReq(),outputOps=(tanks[1].getMaxFill()-tanks[1].getFill())/entry.amountProduced(),heatOps=heat/required,ops=Math.min(inputOps,Math.min(outputOps,heatOps));tanks[0].setFill(tanks[0].getFill()-entry.amountReq()*ops);tanks[1].setFill(tanks[1].getFill()+entry.amountProduced()*ops);heat-=required*ops;if(ops>0&&server.random.nextInt(400)==0)server.playSound(null,worldPosition.getX()+.5D,worldPosition.getY()+2D,worldPosition.getZ()+.5D,HbmSounds.get("block.boilerGroan"),SoundSource.BLOCKS,.5F,1F);if(ops>0)isOn=true;if(outputOps==0&&HeatBoilerConfig.canExplode)burst(server);}
    private void burst(ServerLevel server){hasExploded=true;DummyableBlock.safely(()->{for(int x=-1;x<=1;x++)for(int y=2;y<=3;y++)for(int z=-1;z<=1;z++)server.removeBlock(worldPosition.offset(x,y,z),false);server.removeBlock(worldPosition.above(),false);new ExplosionVNT(server,worldPosition.getX()+.5D,worldPosition.getY()+2D,worldPosition.getZ()+.5D,5F).setEntityProcessor(new EntityProcessorStandard().withRangeMod(3F)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectStandard()).explode();});}
    public boolean identify(HbmFluidType type){var trait=type.getTrait(FluidTraits.Heatable.class);if(trait==null||trait.getEfficiency(FluidTraits.HeatingType.BOILER)<=0)return false;tanks[0].setTankType(type);setChanged();return true;}
    @Override public FluidTank[] getAllTanks(){return tanks;}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}
    @Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
    @Override public List<TankView> tanks(){return hasExploded?List.of():List.of(new TankView(tanks[0].getTankType(),tanks[0].getFill(),tanks[0].getMaxFill()),new TankView(tanks[1].getTankType(),tanks[1].getFill(),tanks[1].getMaxFill()));}
    @Override public List<ProgressView> progressBars(){return hasExploded?List.of():List.of(new ProgressView("jade.hbm.crucible.heat",heat,HeatBoilerConfig.maxHeat,0xe69b51));}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"input",PREFIX_VALUE+"output"};}
    @Override public String provideRORValue(String name){if(hasExploded)return (PREFIX_VALUE+"input").equals(name)||(PREFIX_VALUE+"output").equals(name)?"0":null;return switch(name){case "VAL:input"->Integer.toString(tanks[0].getFill());case "VAL:output"->Integer.toString(tanks[1].getFill());default->null;};}
    @Override public String computerType(){return "ntm_boiler";}
    @Override public String[] computerMethods(){return new String[]{"getFunctionInfo","provideRORValue"};}
    @Override public Object[] callComputer(String method,ComputerArguments arguments){return switch(method){case "getFunctionInfo"->new Object[]{Map.of(1,getFunctionInfo()[0],2,getFunctionInfo()[1])};case "provideRORValue"->new Object[]{provideRORValue(arguments.checkString(0))};default->throw new ComputerException("Unknown method");};}
    private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;currentPorts=List.of();}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.onChunkUnloaded();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tanks[0].writeToNBT(tag,"water");tanks[1].writeToNBT(tag,"steam");tag.putInt("heat",heat);tag.putBoolean("exploded",hasExploded);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");heat=tag.getInt("heat");hasExploded=tag.getBoolean("exploded");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    private CompoundTag packetTag(){var tag=new CompoundTag();tag.putBoolean("exploded",hasExploded);if(!hasExploded){tag.putInt("heat",inletSnapshot==null?heat:snapshotHeat);if(inletSnapshot==null)tanks[0].writeToNBT(tag,"water");else tag.merge(inletSnapshot);tanks[1].writeToNBT(tag,"steam");}tag.putBoolean("muffled",muffled);tag.putBoolean("isOn",isOn);return tag;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return packetTag();}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){hasExploded=tag.getBoolean("exploded");if(!hasExploded){heat=tag.getInt("heat");tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");muffled=tag.getBoolean("muffled");isOn=tag.getBoolean("isOn");}}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
