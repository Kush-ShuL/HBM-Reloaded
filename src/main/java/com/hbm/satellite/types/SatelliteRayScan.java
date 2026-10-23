package com.hbm.satellite.types;
import com.hbm.satellite.*;import api.hbm.redstoneoverradio.IRORInteractive;import net.minecraft.server.level.ServerLevel;import net.minecraft.network.chat.Component;import java.util.*;
public final class SatelliteRayScan extends SatelliteBase {
 @Override public String getType(){return "NB_RAY_SCANNER";}
 @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.satellite.ray_scan.name")};}

 public static final int MAX_SCAN_RANGE=250;public static final String CMD_SURVEY="survey",CMD_COUNT="count",CMD_GETINFO="getinfo",CMD_GETPOSITION="getposition";
 public final List<com.hbm.satellite.SatelliteRayEvents.RayEvent> cachedResults=new ArrayList<>();
 @Override public void onCommandImpl(ServerLevel world,String...cmd){if(cmd.length==0)return;if(cmd[0].equals(CMD_SURVEY)){cachedResults.clear();for(var entry:com.hbm.satellite.SatelliteRayEvents.rayEvent.entrySet()){var pos=entry.getKey();if(!pos.dimension().equals(world.dimension()))continue;int dx=pos.position().getX()-targetX,dz=pos.position().getZ()-targetZ;if(dx*dx+dz*dz<=MAX_SCAN_RANGE*MAX_SCAN_RANGE)cachedResults.add(entry.getValue());}return;}if(cmd[0].equals(CMD_COUNT)){tx=""+cachedResults.size();return;}if(cmd.length==2&&(cmd[0].equals(CMD_GETINFO)||cmd[0].equals(CMD_GETPOSITION))){var event=getEventFromIndex(cmd[1]);tx=event==null?"":cmd[0].equals(CMD_GETINFO)?event.info:event.x+";"+event.z;}}
 public com.hbm.satellite.SatelliteRayEvents.RayEvent getEventFromIndex(String cmd){return cachedResults.isEmpty()?null:cachedResults.get(IRORInteractive.parseInt(cmd,1,cachedResults.size())-1);}

}
