package com.hbm.machine.reactor.iter.auxiliary;
import com.hbm.energy.NodeConnection;
import com.hbm.fusion.plasma.*;
import com.hbm.machine.reactor.iter.torus.FusionNeutronCollector;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Real passive source network observer. Typed bridge rejects plasma energy; only actual Torus collector identity gives bonus. */
public final class FusionCollectorEntity extends BlockEntity implements FusionPowerReceiver,FusionNeutronCollector {
 private PlasmaNode plasmaNode;
 public FusionCollectorEntity(BlockPos pos,BlockState state){super(FusionAuxiliaryContent.COLLECTOR_ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,FusionCollectorEntity collector){if(!(level instanceof ServerLevel server))return;var dir=DummyableBlock.facing(state).getOpposite();var space=PlasmaNetworks.nodespace(server);if(collector.plasmaNode==null||collector.plasmaNode.isExpired()){var origin=pos.relative(dir,2).above(2);collector.plasmaNode=space.getNode(origin);if(collector.plasmaNode==null){collector.plasmaNode=new PlasmaNode(origin).setConnections(new NodeConnection(origin.relative(dir),dir));space.createNode(collector.plasmaNode);}}if(collector.plasmaNode.hasValidNet())collector.plasmaNode.addReceiver(collector);}
 @Override public boolean receivesFusionPower(){return false;}@Override public void receiveFusionPower(long power,double neutrons,float r,float g,float b){} // Original class is an observer, not IFusionPowerReceiver.
 private void disconnect(){if(level instanceof ServerLevel server&&plasmaNode!=null)PlasmaNetworks.nodespace(server).destroyNode(plasmaNode);plasmaNode=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
