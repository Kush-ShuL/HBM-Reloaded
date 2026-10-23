package com.hbm.machine.orbus;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.network.FluidPorts;
import com.hbm.machine.barrel.BarrelBlockEntity;
import com.hbm.multiblock.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Actual TileEntityMachineOrbus extends the one real Barrel backend, including its six-slot GUI/CC/ROR. */
public final class OrbusEntity extends BarrelBlockEntity implements NamedMultiblock,MuffledMachine {
 private List<NodeConnection> currentPorts=List.of();private boolean muffled;
 public OrbusEntity(BlockPos pos,BlockState state){super(OrbusContent.ENTITY.get(),pos,state,512000);}@Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 public static void tick(Level level,BlockPos pos,BlockState state,OrbusEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
 @Override public Component getDisplayName(){var name=super.getDisplayName();return name.equals(Component.translatable("container.barrel"))?Component.translatable("container.orbus"):name;}
 @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
 /** Modern reciprocal nodes must occupy the actual source y0/y4 proxy cells, not its stale y3 upper PortDef. */
 public static List<NodeConnection> connections(BlockPos core,Direction facing){var result=new ArrayList<NodeConnection>();for(var origin:proxyPositions(core,facing)){var side=origin.getY()==core.getY()?Direction.DOWN:Direction.UP;result.add(new NodeConnection(origin.relative(side),side));}return List.copyOf(result);}
 public static List<BlockPos> proxyPositions(BlockPos core,Direction facing){var dir=facing.getOpposite();var rot=dir.getCounterClockWise();var result=new ArrayList<BlockPos>();for(int y:new int[]{0,4})for(var p:List.of(core,core.relative(dir),core.relative(rot),core.relative(dir).relative(rot)))result.add(p.above(y));return List.copyOf(result);}
 @Override protected void updatePorts(ServerLevel server){var loaded=connections(worldPosition,DummyableBlock.facing(getBlockState())).stream().filter(c->server.hasChunkAt(c.origin())&&server.getBlockState(c.origin()).is(getBlockState().getBlock())).toList();if(!loaded.equals(currentPorts)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.stream().map(NodeConnection::origin).toArray(BlockPos[]::new),loaded.toArray(NodeConnection[]::new));}if(fluidPorts!=null)fluidPorts.update(server,this);}
 @Override protected void disconnect(){super.disconnect();currentPorts=List.of();}
 @Override protected void updateComparators(ServerLevel server){for(var c:connections(worldPosition,DummyableBlock.facing(getBlockState()))){server.updateNeighbourForOutputSignal(c.origin(),getBlockState().getBlock());server.updateNeighborsAt(c.target(),getBlockState().getBlock());}}
 @Override public long getReceiverSpeed(HbmFluidType type,int pressure){return mode==0||mode==1?Math.max(1000,(tank.getMaxFill()-tank.getFill())/100):0;}
 @Override public long getProviderSpeed(HbmFluidType type,int pressure){return mode==1||mode==2?Math.max(1000,tank.getFill()/100):0;}
 @Override public void checkFluidInteraction(ServerLevel server){} // Source override: NO! No heat/corrosion/antimatter/firestorm reaction.
 @Override protected void readItemData(CompoundTag tag){tank.readFromNBT(tag,"tank");mode=tag.getShort("nbt");} // Source writes mode, but restores nbt.
 @Override protected void saveState(CompoundTag tag){super.saveState(tag);tag.putBoolean("muffled",muffled);}@Override protected void loadState(CompoundTag tag){super.loadState(tag);muffled=tag.getBoolean("muffled");}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
