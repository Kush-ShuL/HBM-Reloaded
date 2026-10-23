package com.hbm.network.rtty.ror;
import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Source actual burst-edge/polling command receiver, persisted receiving flag and first-load grace. */
public final class RadioControllerEntity extends RadioRorEntity {
 public String channel="",prev;private boolean receiving,firstTick=true;
 public RadioControllerEntity(BlockPos pos,BlockState state){super(RadioRorContent.CONTROLLER_ENTITY.get(),pos,state);polling=true;}
 public static void tick(Level level,BlockPos pos,BlockState state,RadioControllerEntity controller){if(level instanceof ServerLevel server)controller.serverTick(server);}
 @Override protected boolean signalTick(ServerLevel server){boolean active=false;if(channel!=null&&!channel.isEmpty()&&support() instanceof IRORInteractive target){var signal=RTTYSystem.listen(server,channel);if(signal!=null){String rec=""+signal.signal;if("selfdestruct".equals(rec)){server.destroyBlock(worldPosition,false);var explosion=new ExplosionVNT(server,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,5,null);explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1,50).setupPiercing(5F,.5F));explosion.setPlayerProcessor(new PlayerProcessorStandard());explosion.setSFX(new ExplosionEffectWeapon(10,2.5F,1F));explosion.explode();return false;}if(signal.timeStamp>=server.getGameTime()-1&&signal.timeStamp!=-1){active=true;boolean changed=!rec.equals(prev);if(polling||!receiving||changed)try{if(!rec.isEmpty())target.runRORFunction(IRORInteractive.PREFIX_FUNCTION+IRORInteractive.getCommand(rec),IRORInteractive.getParams(rec));}catch(RORFunctionException ignored){}if(changed){prev=rec;setChanged();}}}}
  if(!firstTick||active)if(receiving!=active){receiving=active;setChanged();}firstTick=false;return true;
 }
 private void setChannel(String value){if(!value.equals(channel)){channel=value;prev=null;receiving=false;setChanged();}}
 @Override public void control(CompoundTag tag){if(tag.contains("p"))polling=tag.getBoolean("p");if(tag.contains("c"))setChannel(tag.getString("c"));setChanged();}
 @Override protected void saveState(CompoundTag tag){tag.putBoolean("p",polling);tag.putString("c",channel);if(prev!=null)tag.putString("prev",prev);tag.putBoolean("r",receiving);}@Override protected void loadState(CompoundTag tag){polling=tag.getBoolean("p");channel=tag.getString("c");prev=tag.getString("prev");receiving=tag.contains("r")?tag.getBoolean("r"):!prev.isEmpty();}
 @Override public String computerType(){return "radio_controller";}@Override public String[] computerMethods(){return new String[]{"setChannel","getChannel","setPolling","getPolling","send"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){switch(method){case "setChannel"->setChannel(args.checkString(0));case "getChannel"->{return new Object[]{channel};}case "setPolling"->polling=args.checkBoolean(0);case "getPolling"->{return new Object[]{polling};}case "send"->{String command=args.checkString(0);if(channel!=null&&!channel.isEmpty()&&!command.isEmpty()&&level!=null)RTTYSystem.broadcast(level,channel,command);}default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[0];}
}
