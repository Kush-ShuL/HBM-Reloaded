package com.hbm.machine.chimney;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.ashpit.AshpitBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.pollution.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original immediately consumed smoke, per-tick chimney ash delivery and source pollution reduction. */
public final class ChimneyBlockEntity extends BlockEntity implements StandardFluidReceiver,MachineView {
 public long ashTick,sootTick;public int onTicks;public final boolean industrial;private FluidPorts ports;
 private final FluidTank[] tanks={new FluidTank(HbmFluids.SMOKE,1000000),new FluidTank(HbmFluids.SMOKE_LEADED,1000000),new FluidTank(HbmFluids.SMOKE_POISON,1000000)};
 public ChimneyBlockEntity(BlockPos pos,BlockState state){super(ChimneyContent.ENTITY.get(),pos,state);industrial=state.is(ChimneyContent.INDUSTRIAL.get());}
 @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
 public static void tick(Level level,BlockPos pos,BlockState state,ChimneyBlockEntity self){if(level instanceof ServerLevel server){if(self.ports==null){var positions=new BlockPos[]{pos.north(),pos.east(),pos.south(),pos.west()};self.ports=new FluidPorts(positions,new NodeConnection[]{new NodeConnection(pos.north(2),Direction.NORTH),new NodeConnection(pos.east(2),Direction.EAST),new NodeConnection(pos.south(2),Direction.SOUTH),new NodeConnection(pos.west(2),Direction.WEST)});}self.ports.update(server,self);if(self.ashTick>0||self.sootTick>0){if(server.getBlockEntity(pos.below()) instanceof AshpitBlockEntity ashpit)ashpit.addSmokeAsh(self.ashTick,self.sootTick);self.ashTick=0;self.sootTick=0;}var packet=self.getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<22500)player.connection.send(packet);if(self.onTicks>0)self.onTicks--;}else if(self.onTicks>0&&level.getGameTime()%2==0)LegacyFluidEffects.tower(level,pos.getX()+.5,pos.getY()+(self.industrial?22:12),pos.getZ()+.5,new LegacyFluidEffects.Tower(10,self.industrial?.75F:.5F,3,250+level.random.nextInt(50)).color(0x404040));}
 @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side.getAxis().isHorizontal()&&HbmFluids.isSmoke(type);}@Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return tanks;}@Override public long getDemand(HbmFluidType type,int pressure){return 1000000;}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){if(!HbmFluids.isSmoke(type))return amount;onTicks=20;ashTick+=amount;if(industrial)sootTick+=amount;amount*=ChimneyConfig.pollutionMod(industrial);if(level instanceof ServerLevel server)PollutionSystem.increment(server,worldPosition,type==HbmFluids.SMOKE?PollutionType.SOOT:type==HbmFluids.SMOKE_LEADED?PollutionType.HEAVYMETAL:PollutionType.POISON,amount/100F);return 0;}
 private void disconnect(){if(level instanceof ServerLevel server&&ports!=null)ports.remove(server);ports=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putInt("onTicks",onTicks);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){onTicks=tag.getInt("onTicks");}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
}
