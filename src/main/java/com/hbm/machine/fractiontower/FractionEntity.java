package com.hbm.machine.fractiontower;
import com.hbm.api.machine.*;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original move-up/pull-down then setup and ten-tick100mB fractionation, without heat or energy requirements. */
public final class FractionEntity extends BlockEntity implements StandardFluidTransceiver,IFluidCopiable,MachineView {
 public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("HEAVYOIL"),4000),new FluidTank(HbmFluids.fromName("BITUMEN"),4000),new FluidTank(HbmFluids.fromName("SMEAR"),4000)};
 private FluidPorts ports;private List<BlockPos> connected=List.of();private CompoundTag lastSync;
 public FractionEntity(BlockPos pos,BlockState state){super(FractionContent.ENTITY.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.north(),core.east(),core.south(),core.west()};}
 public static Direction portSide(BlockPos core,BlockPos pos){for(var side:Direction.Plane.HORIZONTAL)if(core.relative(side).equals(pos))return side;return null;}
 private void updatePorts(ServerLevel server){var active=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition))if(server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos)))active.add(pos);if(!active.equals(connected)){disconnect();connected=List.copyOf(active);if(!active.isEmpty()){var links=active.stream().map(pos->new NodeConnection(pos.relative(portSide(worldPosition,pos)),portSide(worldPosition,pos))).toArray(NodeConnection[]::new);ports=new FluidPorts(active.toArray(BlockPos[]::new),links);}}if(ports!=null)ports.update(server,this);}
 public static void tick(Level level,BlockPos pos,BlockState state,FractionEntity tower){if(level instanceof ServerLevel server)tower.serverTick(server);}
 private void serverTick(ServerLevel server){updatePorts(server);
  if(server.getBlockEntity(worldPosition.above(3)) instanceof FractionEntity upper){for(int i=0;i<3;i++)upper.tanks[i].setTankType(tanks[i].getTankType());int oil=Math.min(tanks[0].getFill(),upper.tanks[0].getMaxFill()-upper.tanks[0].getFill()),left=Math.min(upper.tanks[1].getFill(),tanks[1].getMaxFill()-tanks[1].getFill()),right=Math.min(upper.tanks[2].getFill(),tanks[2].getMaxFill()-tanks[2].getFill());tanks[0].setFill(tanks[0].getFill()-oil);tanks[1].setFill(tanks[1].getFill()+left);tanks[2].setFill(tanks[2].getFill()+right);upper.tanks[0].setFill(upper.tanks[0].getFill()+oil);upper.tanks[1].setFill(upper.tanks[1].getFill()-left);upper.tanks[2].setFill(upper.tanks[2].getFill()-right);upper.setChanged();}
  var recipe=FractionRecipeConfig.find(server.getRecipeManager(),tanks[0].getTankType());
  if(recipe!=null){var out=recipe.outputFluids();tanks[1].setTankType(out.get(0).type);tanks[2].setTankType(out.get(1).type);
   if(server.getGameTime()%10==0&&tanks[0].getFill()>=100&&tanks[1].getFill()+out.get(0).fill<=4000&&tanks[2].getFill()+out.get(1).fill<=4000){tanks[0].setFill(tanks[0].getFill()-100);tanks[1].setFill(tanks[1].getFill()+out.get(0).fill);tanks[2].setFill(tanks[2].getFill()+out.get(1).fill);}
  }else for(var tank:tanks)tank.setTankType(HbmFluids.NONE);
  setChanged();var state=getUpdateTag(server.registryAccess());if(!state.equals(lastSync)||server.getGameTime()%20==0){lastSync=state.copy();var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);}
 }
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1],tanks[2]};}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side.getAxis().isHorizontal();}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
 private void write(CompoundTag tag){for(int i=0;i<3;i++)tanks[i].writeToNBT(tag,"tank"+i);}private void read(CompoundTag tag){for(int i=0;i<3;i++)tanks[i].readFromNBT(tag,"tank"+i);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);write(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);read(tag);}@Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();write(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){read(tag);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;connected=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
