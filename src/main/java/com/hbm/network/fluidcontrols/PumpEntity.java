package com.hbm.network.fluidcontrols;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.*;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Literal buffer shrink protection, old FIFO/priority/pressure/redstone semantics. Single unique native FluidNet backend. */
public final class PumpEntity extends BlockEntity implements StandardFluidSender,StandardFluidReceiver,MachineView,MuffledMachine {
 public final boolean legacy;public final FluidTank tank=new FluidTank(HbmFluids.NONE,100);public int bufferSize=100;public ConnectionPriority priority=ConnectionPriority.NORMAL;public boolean redstone,isWorking,topRail,bottomRail=true,muffled,tilted;public float rotor,prevRotor,rotorSpeed;
 private FluidPorts input,output,both;private Direction cachedDirection;
 public PumpEntity(boolean legacy,BlockPos pos,BlockState state){super(legacy?FluidControlsContent.OLD_ENTITY.get():FluidControlsContent.FLOW_ENTITY.get(),pos,state);this.legacy=legacy;}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&(!(state.getBlock() instanceof FlowPumpBlock)||DummyableBlock.isCore(state));}
 public static Direction direction(BlockState state,boolean legacy){int meta=state.getValue(legacy?FluidPumpBlock.META:DummyableBlock.LEGACY_META)-(legacy?0:10);var original=ForgeDirection.getOrientation(meta).getRotation(ForgeDirection.DOWN);return original==ForgeDirection.UNKNOWN?null:original.nativeDirection();}
 public Direction direction(){return direction(getBlockState(),legacy);}public BlockPos outputPos(){return legacy?worldPosition:worldPosition.relative(direction());}public BlockPos inputPos(){return legacy?worldPosition:worldPosition.relative(direction().getOpposite());}
 public boolean bufferMismatch(){return bufferSize!=tank.getMaxFill();}
 private void connect(ServerLevel server){var direction=direction();if(direction==null){disconnect();return;}if(direction!=cachedDirection){disconnect();cachedDirection=direction;}if(legacy){if(both==null)both=new FluidPorts(new BlockPos[]{worldPosition},new NodeConnection[]{new NodeConnection(worldPosition.relative(direction),direction),new NodeConnection(worldPosition.relative(direction.getOpposite()),direction.getOpposite())});both.update(server,this);return;}
  if(input==null)input=new FluidPorts(new BlockPos[]{inputPos()},new NodeConnection[]{new NodeConnection(inputPos().relative(direction.getOpposite()),direction.getOpposite())});
  if(output==null)output=new FluidPorts(new BlockPos[]{outputPos()},new NodeConnection[]{new NodeConnection(outputPos().relative(direction),direction)});
  // Deliberately separate receiver-only and sender-only nodes; no pass-through across the pump body.
  input.update(server,inlet);output.update(server,outlet);
 }
 private final StandardFluidReceiver inlet=new StandardFluidReceiver(){public FluidTank[] getAllTanks(){return PumpEntity.this.getAllTanks();}public FluidTank[] getReceivingTanks(){return getAllTanks();}public boolean isLoaded(){return PumpEntity.this.isLoaded();}public boolean canConnect(HbmFluidType type,Direction side){return side==direction().getOpposite();}public ConnectionPriority getFluidPriority(){return priority;}public long getReceiverSpeed(HbmFluidType type,int pressure){return PumpEntity.this.getReceiverSpeed(type,pressure);}public long transferFluid(HbmFluidType type,int pressure,long amount){return PumpEntity.this.transferFluid(type,pressure,amount);}};
 private final StandardFluidSender outlet=new StandardFluidSender(){public FluidTank[] getAllTanks(){return PumpEntity.this.getAllTanks();}public FluidTank[] getSendingTanks(){return getAllTanks();}public boolean isLoaded(){return PumpEntity.this.isLoaded();}public boolean canConnect(HbmFluidType type,Direction side){return side==direction();}public long getProviderSpeed(HbmFluidType type,int pressure){return PumpEntity.this.getProviderSpeed(type,pressure);}public void useUpFluid(HbmFluidType type,int pressure,long amount){PumpEntity.this.useUpFluid(type,pressure,amount);}};
 public static void tick(Level level,BlockPos pos,BlockState state,PumpEntity pump){if(level instanceof ServerLevel server)pump.tickServer(server);else if(!pump.legacy)pump.tickClient();}
 private void tickServer(ServerLevel server){connect(server);if(bufferMismatch())tank.changeTankSize(Math.max(tank.getFill(),bufferSize));var dir=direction();redstone=server.hasNeighborSignal(worldPosition)||!legacy&&(server.hasNeighborSignal(worldPosition.relative(dir))||server.hasNeighborSignal(worldPosition.relative(dir.getOpposite())));sync();isWorking=false;}
 private void tickClient(){if(level.getGameTime()%20==0){topRail=!level.isEmptyBlock(worldPosition.above());bottomRail=!level.isEmptyBlock(worldPosition.below());}rotorSpeed=net.minecraft.util.Mth.clamp(rotorSpeed+(isWorking?1:-1),0,30);prevRotor=rotor;rotor+=rotorSpeed;if(rotor>=360){rotor-=360;prevRotor-=360;}}
 public void sync(){if(!(level instanceof ServerLevel server))return;setChanged();var packet=getUpdatePacket();double range=legacy?15:50;try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<range*range)player.connection.send(packet);dispatchScope.commit();}}
 @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getSendingTanks(){return getAllTanks();}@Override public FluidTank[] getReceivingTanks(){return getAllTanks();}@Override public boolean canConnect(HbmFluidType type,Direction side){var dir=direction();return dir!=null&&(side==dir||side==dir.getOpposite());}
 @Override public long getReceiverSpeed(HbmFluidType type,int pressure){return redstone||bufferMismatch()?0:1_000_000_000;}@Override public long getProviderSpeed(HbmFluidType type,int pressure){return redstone?0:1_000_000_000;}@Override public ConnectionPriority getFluidPriority(){return priority;}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}
 @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){if(amount>0&&type==tank.getTankType()&&pressure==tank.getPressure()){int used=(int)Math.min(amount,tank.getFill());tank.setFill(tank.getFill()-used);if(!legacy)isWorking=true;setChanged();}}
 public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=128;}
 public void control(CompoundTag tag){if(tag.contains("capacity"))bufferSize=net.minecraft.util.Mth.clamp(tag.getInt("capacity"),0,10_000);if(tag.contains("pressure"))tank.withPressure(net.minecraft.util.Mth.clamp(tag.getByte("pressure"),0,5));if(tag.contains("priority"))priority=safe(tag.getByte("priority"));setChanged();}
 private static ConnectionPriority safe(int n){return ConnectionPriority.values()[Math.abs(n%ConnectionPriority.values().length)];}
 private CompoundTag write(CompoundTag tag){tank.writeToNBT(tag,"t");tag.putInt("buffer",bufferSize);tag.putByte("p",(byte)priority.ordinal());tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);return tag;}
 private void read(CompoundTag tag){tank.readFromNBT(tag,"t");if(tag.contains("t_max")&&tag.getInt("t_max")>=0)tank.changeTankSize(tag.getInt("t_max"));bufferSize=tag.getInt("buffer");priority=safe(tag.getByte("p"));muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");if(!legacy){redstone=tag.getBoolean("hbm_redstone");isWorking=tag.getBoolean("hbm_working");}}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);write(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);read(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=write(new CompoundTag());if(!legacy){tag.putBoolean("hbm_redstone",redstone);tag.putBoolean("hbm_working",isWorking);}return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){read(tag);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){read(packet.getTag());}
 private void disconnect(){if(level instanceof ServerLevel server){if(input!=null)input.remove(server);if(output!=null)output.remove(server);if(both!=null)both.remove(server);}input=output=both=null;cachedDirection=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}@Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
