package com.hbm.machine.drain;
import com.hbm.api.machine.*;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.energy.ConnectionPriority;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
/** Original low-priority2k tank, real spill traits, antimatter explosion and three horizontal inlet faces. */
public final class DrainEntity extends BlockEntity implements StandardFluidReceiver,IFluidCopiable,MachineView {
 public final FluidTank tank=new FluidTank(HbmFluids.NONE,2000);
 public static Consumer<DrainEntity> CLIENT_TICK=drain->{};
 /** Client-only visual latch: a later empty snapshot must not erase an already received positive release. */
 private HbmFluidType clientEmissionType;
 public HbmFluidType consumeClientEmissionType(){var result=tank.getFill()>0?tank.getTankType():clientEmissionType;clientEmissionType=null;return result;}
 private FluidPorts ports;
 public DrainEntity(BlockPos pos,BlockState state){super(DrainContent.ENTITY.get(),pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 public static void tick(Level level,BlockPos pos,BlockState state,DrainEntity drain){if(level instanceof ServerLevel server)drain.serverTick(server);else CLIENT_TICK.accept(drain);}
 private void serverTick(ServerLevel server){
  if(ports==null){var dir=DummyableBlock.facing(getBlockState());ports=new FluidPorts(new BlockPos[]{worldPosition},new NodeConnection[]{new NodeConnection(worldPosition.relative(dir),dir),new NodeConnection(worldPosition.relative(dir.getClockWise()),dir.getClockWise()),new NodeConnection(worldPosition.relative(dir.getCounterClockWise()),dir.getCounterClockWise())});}
  ports.update(server,this);
  var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);dispatchScope.commit();}
  if(tank.getFill()<=0)return;
  var type=tank.getTankType();if(type.isAntimatter()){server.explode(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,10,true,Level.ExplosionInteraction.BLOCK);return;}
  int amount=Math.max(tank.getFill()/2,1);tank.setFill(tank.getFill()-amount);type.release(server,worldPosition,tank,amount,FluidTraits.Release.SPILL);setChanged();
  if(amount>=100&&server.random.nextInt(20)==0&&type.hasTrait("liquid")&&type.hasTrait("viscous")&&type.hasTrait(FluidTraits.Flammable.class)){
   var direction=DummyableBlock.facing(getBlockState());var start=Vec3.atCenterOf(worldPosition).add(-direction.getStepX()*3,0,-direction.getStepZ()*3);var end=start.add(server.random.nextGaussian()*5,-25,server.random.nextGaussian()*5);
   var hit=server.clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,(net.minecraft.world.entity.Entity)null));
   if(hit.getType()==HitResult.Type.BLOCK&&hit.getDirection()==Direction.UP){var pos=hit.getBlockPos().above();var state=server.getBlockState(pos);var spill=DrainContent.OIL.get().defaultBlockState();if(!LegacyMaterials.of(state).isLiquid()&&state.canBeReplaced()&&spill.canSurvive(server,pos))server.setBlock(pos,spill,3);}
  }
 }
 @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return getAllTanks();}
 @Override public FluidTank getTankToPaste(){return tank;}
 @Override public ConnectionPriority getFluidPriority(){return ConnectionPriority.LOW;}
 @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side.getAxis().isHorizontal();}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remaining=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(remaining!=amount)setChanged();return remaining;}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
 @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
 private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;}
 @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tank.writeToNBT(tag,"t");}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);tank.readFromNBT(tag,"t");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tank.writeToNBT(tag,"t");return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){tank.readFromNBT(tag,"t");if(tank.getFill()>0)clientEmissionType=tank.getTankType();}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
 @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
