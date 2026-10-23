package com.hbm.machine.reactor.iter.auxiliary;
import com.hbm.energy.NodeConnection;
import com.hbm.fusion.klystron.*;
import com.hbm.fusion.plasma.*;
import com.hbm.machine.reactor.iter.torus.TorusEntity;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original plasma→KyU coupler, noHE/fluid conversion, nobuffer or fake autonomous supply. */
public final class FusionCouplerEntity extends BlockEntity implements FusionPowerReceiver, com.hbm.multiblock.MuffledMachine {
 private KlystronNode klystronNode;private PlasmaNode plasmaNode;public boolean muffled,tilted;
 public FusionCouplerEntity(BlockPos pos,BlockState state){super(FusionAuxiliaryContent.COUPLER_ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,FusionCouplerEntity coupler){if(!(level instanceof ServerLevel server))return;var dir=DummyableBlock.facing(state).getOpposite().getClockWise();var ky=KlystronNetworks.nodespace(server);var plasma=PlasmaNetworks.nodespace(server);if(coupler.klystronNode==null||coupler.klystronNode.isExpired()){var origin=pos.relative(dir).above(2);coupler.klystronNode=ky.getNode(origin);if(coupler.klystronNode==null){coupler.klystronNode=new KlystronNode(origin).setConnections(new NodeConnection(origin.relative(dir),dir));ky.createNode(coupler.klystronNode);}}if(coupler.plasmaNode==null||coupler.plasmaNode.isExpired()){var origin=pos.relative(dir.getOpposite()).above(2);coupler.plasmaNode=plasma.getNode(origin);if(coupler.plasmaNode==null){coupler.plasmaNode=new PlasmaNode(origin).setConnections(new NodeConnection(origin.relative(dir.getOpposite()),dir.getOpposite()));plasma.createNode(coupler.plasmaNode);}}if(coupler.klystronNode.hasValidNet())coupler.klystronNode.addProvider(coupler);if(coupler.plasmaNode.hasValidNet())coupler.plasmaNode.addReceiver(coupler);}
 @Override public boolean receivesFusionPower(){return true;}@Override public void receiveFusionPower(long power,double neutrons,float r,float g,float b){if(klystronNode!=null&&klystronNode.hasValidNet())for(var receiver:klystronNode.network().receivers)if(receiver instanceof TorusEntity torus&&torus.isLoaded()&&!torus.isRemoved()){torus.receiveKlystron(power);break;}}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 private void disconnect(){if(level instanceof ServerLevel server){if(klystronNode!=null)KlystronNetworks.nodespace(server).destroyNode(klystronNode);if(plasmaNode!=null)PlasmaNetworks.nodespace(server).destroyNode(plasmaNode);}klystronNode=null;plasmaNode=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
