package com.hbm.network.rtty;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original timestamp/state-change/polling, highest-index mapping and real entity-only selfdestruct VNT. */
public final class RadioTorchReceiverEntity extends RadioTorchEntity {
 public RadioTorchReceiverEntity(BlockPos pos,BlockState state){super(RadioTorchContent.RECEIVER_ENTITY.get(),pos,state);}
 @Override public void onLoad(){super.onLoad();if(!loadedFromTag&&level!=null)lastUpdate=level.getGameTime();}
 public static void tick(Level level,BlockPos pos,BlockState state,RadioTorchReceiverEntity receiver){if(level instanceof ServerLevel server)receiver.tickRadio(server);}
 @Override protected boolean signalTick(ServerLevel level){if(!channel.isEmpty()||polling){var signal=channel.isEmpty()?null:RTTYSystem.listen(level,channel);if(signal!=null&&(polling||signal.timeStamp>lastUpdate-1&&signal.timeStamp!=-1)){String message=""+signal.signal;lastUpdate=level.getGameTime();int next=0;if(message.equals("selfdestruct")){level.destroyBlock(worldPosition,false);var explosion=new ExplosionVNT(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,5,null);explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1,50).setupPiercing(5F,.5F));explosion.setPlayerProcessor(new PlayerProcessorStandard());explosion.setSFX(new ExplosionEffectWeapon(10,2.5F,1));explosion.explode();return false;}if(customMap){for(int i=15;i>=0;i--)if(message.equals(mapping[i])){next=i;break;}}else{int value=0;try{value=Integer.parseInt(message);}catch(NumberFormatException ignored){}next=Mth.clamp(value,0,15);}if(signal.timeStamp<lastUpdate-1&&polling)next=0;if(lastState!=next){lastState=next;stateChanged(true);}}else if(polling&&lastState!=0){lastState=0;stateChanged(true);}}return true;}
}
