package com.hbm.machine.bat9000;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.network.FluidPorts;
import com.hbm.machine.barrel.BarrelBlockEntity;
import com.hbm.multiblock.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
/** Actual legacy BAT9000 inherits Barrel6slots/four modes/1e9 flow/CC+ROR;not16M tilted bigasstank. */
public final class Bat9000BlockEntity extends BarrelBlockEntity implements NamedMultiblock {
 public static final int CAPACITY=2048000;private List<BlockPos> currentPorts=List.of();
 public Bat9000BlockEntity(BlockPos pos,BlockState state){super(Bat9000Content.ENTITY.get(),pos,state,CAPACITY);}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 public static void tick(Level level,BlockPos pos,BlockState state,Bat9000BlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
 @Override public Component getDisplayName(){var name=super.getDisplayName();return name.equals(Component.translatable("container.barrel"))?Component.translatable("container.bat9000"):name;}
 public static NodeConnection[] connections(BlockPos core){return new NodeConnection[]{new NodeConnection(core.offset(1,0,3),Direction.SOUTH),new NodeConnection(core.offset(-1,0,3),Direction.SOUTH),new NodeConnection(core.offset(1,0,-3),Direction.NORTH),new NodeConnection(core.offset(-1,0,-3),Direction.NORTH),new NodeConnection(core.offset(3,0,1),Direction.EAST),new NodeConnection(core.offset(-3,0,1),Direction.WEST),new NodeConnection(core.offset(3,0,-1),Direction.EAST),new NodeConnection(core.offset(-3,0,-1),Direction.WEST)};}
 public static BlockPos[] portPositions(BlockPos core){return Arrays.stream(connections(core)).map(NodeConnection::origin).toArray(BlockPos[]::new);}
 @Override protected void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition))if(server.hasChunkAt(pos)){var state=server.getBlockState(pos);if(state.is(getBlockState().getBlock())&&DummyableBlock.isPort(state))loaded.add(pos);}if(!currentPorts.equals(loaded)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),Arrays.stream(connections(worldPosition)).filter(c->loaded.contains(c.origin())).toArray(NodeConnection[]::new));}if(fluidPorts!=null)fluidPorts.update(server,this);}
 @Override protected void disconnect(){super.disconnect();currentPorts=List.of();}
 @Override protected void updateComparators(ServerLevel server){for(var connection:connections(worldPosition)){server.updateNeighbourForOutputSignal(connection.origin(),getBlockState().getBlock());server.updateNeighborsAt(connection.target(),getBlockState().getBlock());}}
 @Override public void checkFluidInteraction(ServerLevel server){if(tank.getTankType().isAntimatter()){server.destroyBlock(worldPosition,false);server.explode(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,10,true,Level.ExplosionInteraction.BLOCK);}}
}
