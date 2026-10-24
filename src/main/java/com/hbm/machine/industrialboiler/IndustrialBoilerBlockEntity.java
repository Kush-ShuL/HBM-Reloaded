package com.hbm.machine.industrialboiler;
import com.hbm.api.machine.*;
import com.hbm.energy.NodeConnection;
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
/** Source IndustrialBoiler: first BOILER step, exact TU diffusion and original TOM sky heating branch. */
public final class IndustrialBoilerBlockEntity extends BlockEntity implements StandardFluidTransceiver,IFluidCopiable,MachineView, com.hbm.multiblock.MuffledMachine {
 public static Consumer<IndustrialBoilerBlockEntity> clientTick=machine->{},clientRemoved=machine->{};
 public final FluidTank[] tanks={new FluidTank(HbmFluids.WATER,64000),new FluidTank(HbmFluids.STEAM,6400000)};public int heat;public boolean isOn,muffled,tilted;
 private FluidPorts ports;private List<BlockPos> currentPorts=List.of();private CompoundTag inletSnapshot;private int snapshotHeat;
 public IndustrialBoilerBlockEntity(BlockPos pos,BlockState state){super(IndustrialBoilerContent.ENTITY.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
 public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.east(),core.west(),core.south(),core.north(),core.above(4)};}
 public static Direction portSide(BlockPos core,BlockPos pos){if(core.above(4).equals(pos))return Direction.UP;for(var side:Direction.Plane.HORIZONTAL)if(core.relative(side).equals(pos))return side;return null;}
 public static void tick(Level level,BlockPos pos,BlockState state,IndustrialBoilerBlockEntity boiler){if(level instanceof ServerLevel server)boiler.serverTick(server);else clientTick.accept(boiler);}
 private void serverTick(ServerLevel server){updatePorts(server);setupTanks();tryPullHeat(server);snapshotHeat=heat;if(server.getBrightness(LightLayer.SKY,worldPosition)>7&&TomSaveData.forWorld(server).fire>1e-5)heat+=(IndustrialBoilerConfig.maxHeat-heat)*.000005D;inletSnapshot=new CompoundTag();tanks[0].writeToNBT(inletSnapshot,"water");isOn=false;tryConvert(server);setChanged();var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625)player.connection.send(packet);dispatchScope.commit();}}
 private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos)))loaded.add(pos);if(!loaded.equals(currentPorts)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var pos:loaded){var side=portSide(worldPosition,pos);connections.add(new NodeConnection(pos.relative(side),side));}ports=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}if(ports!=null)ports.update(server,this);}
 private void tryPullHeat(ServerLevel server){if(server.getBlockEntity(worldPosition.below()) instanceof HeatSource source){int diff=source.getHeatStored()-heat;if(diff==0)return;if(diff>0){diff=(int)Math.ceil(diff*IndustrialBoilerConfig.diffusion);diff=Math.min(diff,IndustrialBoilerConfig.maxHeat-heat);source.useUpHeat(diff);heat+=diff;if(heat>IndustrialBoilerConfig.maxHeat)heat=IndustrialBoilerConfig.maxHeat;return;}}heat=Math.max(heat-Math.max(heat/1000,1),0);}
 private void setupTanks(){var trait=tanks[0].getTankType().getTrait(FluidTraits.Heatable.class);if(trait!=null&&trait.getEfficiency(FluidTraits.HeatingType.BOILER)>0){var step=trait.getFirstStep();tanks[1].setTankType(step.typeProduced());tanks[1].changeTankSize(tanks[0].getMaxFill()*step.amountProduced()/step.amountReq());return;}tanks[0].setTankType(HbmFluids.NONE);tanks[1].setTankType(HbmFluids.NONE);}
 private void tryConvert(ServerLevel server){var trait=tanks[0].getTankType().getTrait(FluidTraits.Heatable.class);if(trait==null||trait.getEfficiency(FluidTraits.HeatingType.BOILER)<=0)return;var step=trait.getFirstStep();int required=(int)Math.max(step.heatReq()/trait.getEfficiency(FluidTraits.HeatingType.BOILER),1);int inputOps=tanks[0].getFill()/step.amountReq(),outputOps=(tanks[1].getMaxFill()-tanks[1].getFill())/step.amountProduced(),heatOps=heat/required;int ops=Math.min(inputOps,Math.min(outputOps,heatOps));tanks[0].setFill(tanks[0].getFill()-step.amountReq()*ops);tanks[1].setFill(tanks[1].getFill()+step.amountProduced()*ops);heat-=required*ops;if(ops>0&&server.random.nextInt(400)==0)server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+2,worldPosition.getZ()+.5,HbmSounds.get("block.boilerGroan"),SoundSource.BLOCKS,.5F,1);if(ops>0)isOn=true;}
 public boolean identify(HbmFluidType type){var trait=type.getTrait(FluidTraits.Heatable.class);if(trait==null||trait.getEfficiency(FluidTraits.HeatingType.BOILER)<=0)return false;tanks[0].setTankType(type);setChanged();return true;}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){return List.of(new TankView(tanks[0].getTankType(),tanks[0].getFill(),tanks[0].getMaxFill()),new TankView(tanks[1].getTankType(),tanks[1].getFill(),tanks[1].getMaxFill()));}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.crucible.heat",heat,IndustrialBoilerConfig.maxHeat,0xE69B51));}
 public String[] getFunctionInfo(){return new String[]{"VAL:input","VAL:output"};}public String provideRORValue(String name){return switch(name){case "VAL:input"->Integer.toString(tanks[0].getFill());case "VAL:output"->Integer.toString(tanks[1].getFill());default->null;};}
 private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;currentPorts=List.of();}@Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.onChunkUnloaded();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tanks[0].writeToNBT(tag,"water");tanks[1].writeToNBT(tag,"steam");tag.putInt("heat",heat);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");heat=tag.getInt("heat");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();if(inletSnapshot!=null)tag.merge(inletSnapshot);else tanks[0].writeToNBT(tag,"water");tanks[1].writeToNBT(tag,"steam");tag.putInt("heat",inletSnapshot==null?heat:snapshotHeat);tag.putBoolean("isOn",isOn);tag.putBoolean("muffled",muffled);return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){tanks[0].readFromNBT(tag,"water");tanks[1].readFromNBT(tag,"steam");heat=tag.getInt("heat");isOn=tag.getBoolean("isOn");muffled=tag.getBoolean("muffled");}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
