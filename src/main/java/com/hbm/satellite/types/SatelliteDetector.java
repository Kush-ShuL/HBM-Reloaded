package com.hbm.satellite.types;
import com.hbm.satellite.*;import api.hbm.redstoneoverradio.IRORInteractive;import net.minecraft.server.level.ServerLevel;import net.minecraft.network.chat.Component;import java.util.*;
public final class SatelliteDetector extends SatelliteBase {
 @Override public String getType(){return "UWB_EMISSION_DETECTOR";}
 @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.satellite.detector.name")};}

 public static final String CMD_SURVEY="survey",CMD_COUNT="count",CMD_GETTYPE="gettype",CMD_GETPOSITION="getposition";
 public final List<com.hbm.bomb.nuclear.SatelliteBursts.RadiationBurst> cachedResults=new ArrayList<>();
 @Override public void onCommandImpl(ServerLevel world,String...cmd){if(cmd.length==0)return;if(cmd[0].equals(CMD_SURVEY)){cachedResults.clear();for(var burst:com.hbm.bomb.nuclear.SatelliteBursts.bursts)if(world.dimension().equals(burst.dimension))cachedResults.add(burst);}if(cmd[0].equals(CMD_COUNT)){tx=""+cachedResults.size();return;}if(cmd.length==2&&(cmd[0].equals(CMD_GETTYPE)||cmd[0].equals(CMD_GETPOSITION))){var burst=getBurstFromIndex(cmd[1]);tx=burst==null?"":cmd[0].equals(CMD_GETTYPE)?burst.intensity.name():burst.x+";"+burst.z;}}
 public com.hbm.bomb.nuclear.SatelliteBursts.RadiationBurst getBurstFromIndex(String cmd){return cachedResults.isEmpty()?null:cachedResults.get(IRORInteractive.parseInt(cmd,1,cachedResults.size())-1);}

}
