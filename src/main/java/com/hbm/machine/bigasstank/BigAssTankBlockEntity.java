package com.hbm.machine.bigasstank;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.network.FluidPorts;
import com.hbm.machine.barrel.BarrelBlockEntity;
import com.hbm.multiblock.*;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Real Barrel execution with source16M capacity, fast two-port flow and extra-heavy foundation. */
public final class BigAssTankBlockEntity extends BarrelBlockEntity implements NamedMultiblock {
    private List<BlockPos> currentPorts=List.of();
    private int tiltBlocksChecked,tiltBlocksValid;
    public BigAssTankBlockEntity(BlockPos pos,BlockState state){super(BigAssTankContent.ENTITY.get(),pos,state,16000000);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    public static void tick(Level level,BlockPos pos,BlockState state,BigAssTankBlockEntity machine){if(level instanceof ServerLevel server){machine.checkFoundation(server);machine.serverTick(server);}}
    @Override public Component getDisplayName(){var name=super.getDisplayName();return name.equals(Component.translatable("container.barrel"))?Component.translatable("container.bigAssTank"):name;}
    @Override public int trackingRange(){return 100;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    public static BlockPos[] portPositions(BlockPos core,Direction facing){return new BlockPos[]{core.relative(facing,6),core.relative(facing,-6)};}
    @Override protected void updatePorts(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());var loaded=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition,facing))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos)))loaded.add(pos);if(!loaded.equals(currentPorts)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var pos:loaded){var side=pos.equals(worldPosition.relative(facing,6))?facing:facing.getOpposite();connections.add(new NodeConnection(pos.relative(side),side));}fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}if(fluidPorts!=null)fluidPorts.update(server,this);}
    @Override protected void disconnect(){super.disconnect();currentPorts=List.of();}
    @Override protected void updateComparators(ServerLevel server){for(var pos:portPositions(worldPosition,DummyableBlock.facing(getBlockState()))){server.updateNeighbourForOutputSignal(pos,getBlockState().getBlock());for(var side:Direction.values())server.updateNeighborsAt(pos.relative(side),getBlockState().getBlock());}}
    @Override public long getReceiverSpeed(HbmFluidType type,int pressure){return mode==0||mode==1?Math.max(50000,(tank.getMaxFill()-tank.getFill())/100):0;}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return mode==1||mode==2?Math.max(50000,tank.getFill()/100):0;}
    @Override public void checkFluidInteraction(ServerLevel server){if(tank.getTankType().isAntimatter()){server.removeBlock(worldPosition,false);server.explode(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,10,true,Level.ExplosionInteraction.BLOCK);}}
    private void checkFoundation(ServerLevel server){
        int identity=(worldPosition.getY()+worldPosition.getZ()*27644437)*27644437+worldPosition.getX();if((server.getGameTime()+identity)%20!=0)return;
        if(tiltBlocksChecked>=16){boolean previous=tilted;tilted=tiltBlocksValid<tiltBlocksChecked*.95;if(tilted&&!previous)server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get("block.metalImpact"),SoundSource.BLOCKS,3,1);setChanged();tiltBlocksChecked=0;tiltBlocksValid=0;}
        var pos=worldPosition.offset(-3+(tiltBlocksChecked/4)*2,-1,-3+(tiltBlocksChecked%4)*2);var state=server.getBlockState(pos);tiltBlocksChecked++;
        String material=LegacyMaterials.of(state).name();if(!state.isFaceSturdy(server,pos,Direction.UP)||!state.isSolidRender(server,pos)||Set.of("sand","cloth","ground").contains(material)||state.getBlock().getExplosionResistance()<Blocks.STONE.getExplosionResistance())return;tiltBlocksValid++;
    }
}
