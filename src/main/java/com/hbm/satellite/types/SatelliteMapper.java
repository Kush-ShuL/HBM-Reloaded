package com.hbm.satellite.types;
import com.hbm.satellite.*;import api.hbm.redstoneoverradio.IRORInteractive;import net.minecraft.server.level.ServerLevel;import net.minecraft.network.chat.Component;import java.util.*;
public final class SatelliteMapper extends SatelliteBase {
 @Override public String getType(){return "NOT_A_SPY_SATELLITE_:)";}
 @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.satellite.spy.name")};}

 public static final String CMD_TARGET_LOADED="targetloaded",CMD_GETSMOG="getsmog",CMD_SPOT_PLAYER="spotplayers";public static final int SPOT_PLAYER_MAX_RANGE=250;
 @Override public void onCommandImpl(ServerLevel world,String...cmd){if(cmd.length==0)return;if(cmd[0].equals(CMD_TARGET_LOADED)){tx=(""+world.getChunkSource().hasChunk(targetX>>4,targetZ>>4)).toUpperCase(Locale.US);return;}if(cmd[0].equals(CMD_GETSMOG)){Float soot=com.hbm.pollution.PollutionSystem.getIfPresent(world,new net.minecraft.core.BlockPos(targetX,255,targetZ),com.hbm.pollution.PollutionType.SOOT);if(soot!=null)tx=""+(int)Math.ceil(soot);return;}if(cmd[0].equals(CMD_SPOT_PLAYER)){List<String> names=new ArrayList<>();for(var player:world.players()){int x=(int)Math.floor(player.getX()),z=(int)Math.floor(player.getZ());double dx=x-targetX,dz=z-targetZ;if(dx*dx+dz*dz<=SPOT_PLAYER_MAX_RANGE*SPOT_PLAYER_MAX_RANGE){int height=com.hbm.world.SourceWorldHeight.get(world,x,z);if(height<player.getY()+2)names.add(player.getGameProfile().getName());}}tx=names.isEmpty()?"NONE":String.join(";",names);}}

}
