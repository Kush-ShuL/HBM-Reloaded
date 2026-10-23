package com.hbm.machine.reactor.iter.utilities;
import com.hbm.energy.NodeConnection;
import com.hbm.fusion.klystron.*;
import com.hbm.machine.reactor.iter.FusionRecipeConfig;
import com.hbm.machine.reactor.iter.torus.TorusEntity;
import com.hbm.multiblock.DummyableBlock;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original creative Klystron provides actual FusionRecipes.maxInput to exactly the first loaded true torus; no invented HE source or craft. */
public final class CreativeKlystronEntity extends FusionUtilityEntity {
 private KlystronNode node;public boolean isConnected;public float fan,prevFan,fanSpeed;public static Consumer<CreativeKlystronEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
 public CreativeKlystronEntity(BlockPos pos,BlockState state){super(FusionUtilityContent.CREATIVE_ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,CreativeKlystronEntity machine){if(level instanceof ServerLevel server){var dir=DummyableBlock.facing(state).getOpposite();var space=KlystronNetworks.nodespace(server);if(machine.node==null||machine.node.isExpired()){var origin=pos.relative(dir,4).above(2);machine.node=space.getNode(origin);if(machine.node==null){machine.node=new KlystronNode(origin).setConnections(new NodeConnection(origin.relative(dir),dir));space.createNode(machine.node);}}machine.isConnected=false;if(machine.node.hasValidNet()){machine.node.addProvider(machine);for(var receiver:machine.node.network().receivers)if(receiver instanceof TorusEntity torus&&torus.isLoaded()&&!torus.isRemoved()){torus.receiveKlystron(FusionRecipeConfig.maxInput());machine.isConnected=true;break;}}machine.sync(100);}else{machine.fanSpeed+=machine.isConnected?.125F:-.125F;machine.fanSpeed=net.minecraft.util.Mth.clamp(machine.fanSpeed,0,5);machine.prevFan=machine.fan;machine.fan+=machine.fanSpeed;if(machine.fan>=360){machine.fan-=360;machine.prevFan-=360;}CLIENT_TICK.accept(machine);}}
 @Override protected void saveMachine(CompoundTag tag){}@Override protected void loadMachine(CompoundTag tag){} // Source does not persist connected/fan state.
 @Override protected void writeSync(CompoundTag tag){tag.putBoolean("connected",isConnected);}@Override protected void readSync(CompoundTag tag){isConnected=tag.getBoolean("connected");}
 private void disconnect(){if(level instanceof ServerLevel server&&node!=null)KlystronNetworks.nodespace(server).destroyNode(node);node=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
