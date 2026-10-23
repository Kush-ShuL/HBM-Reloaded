package com.hbm.satellite.types;
import com.hbm.satellite.*;import api.hbm.redstoneoverradio.IRORInteractive;import net.minecraft.server.level.ServerLevel;import net.minecraft.network.chat.Component;import java.util.*;
public final class SatelliteRadar extends SatelliteBase {
 @Override public String getType(){return "LEO_RADAR";}
 @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.satellite.radar.name")};}

 public static final int MAX_SCAN_RANGE=1000;public static final api.hbm.entity.IRadarDetectableNT.RadarScanParams scanParams=new api.hbm.entity.IRadarDetectableNT.RadarScanParams(true,true,true,false);
 public static final String CMD_SURVEY="survey",CMD_FILTER="filter",CMD_COUNT="count",CMD_GETTARGETID="gettargetid",CMD_GETPOSITION="getposition",CMD_GETNAME="getname";
 public List<net.minecraft.world.entity.Entity> cachedRadarResults=new ArrayList<>(),filteredRadarResults=new ArrayList<>();
 @Override public void onCommandImpl(ServerLevel world,String...cmd){if(cmd.length==0)return;if(cmd[0].equals(CMD_SURVEY)){cachedRadarResults.clear();for(var entity:com.hbm.radar.RadarTargetCache.matchingEntities()){if(entity.level()!=world)continue;int x=(int)Math.floor(entity.getX()),z=(int)Math.floor(entity.getZ());double dx=x-targetX,dz=z-targetZ;if(dx*dx+dz*dz<=MAX_SCAN_RANGE*MAX_SCAN_RANGE)cachedRadarResults.add(entity);}filteredRadarResults=new ArrayList<>(cachedRadarResults);return;}if(cmd[0].equals(CMD_FILTER)&&cmd.length==2){filteredRadarResults.clear();String filter=cmd[1].toLowerCase(Locale.US);for(var entity:cachedRadarResults){if(entity.isRemoved())continue;String name=entity.getClass().getSimpleName().toLowerCase(Locale.US);if(name.contains(filter))filteredRadarResults.add(entity);}return;}if(cmd[0].equals(CMD_COUNT)){tx=""+filteredRadarResults.size();return;}if(cmd.length==2&&(cmd[0].equals(CMD_GETTARGETID)||cmd[0].equals(CMD_GETPOSITION)||cmd[0].equals(CMD_GETNAME))){var entity=getTargetFromIndex(cmd[1]);if(entity==null){tx="";return;}tx=cmd[0].equals(CMD_GETTARGETID)?""+entity.getId():cmd[0].equals(CMD_GETPOSITION)?(int)Math.floor(entity.getX())+";"+(int)Math.floor(entity.getY())+";"+(int)Math.floor(entity.getZ()):entity.getClass().getSimpleName().toLowerCase(Locale.US);}}
 public net.minecraft.world.entity.Entity getTargetFromIndex(String cmd){if(filteredRadarResults.isEmpty())return null;int index=IRORInteractive.parseInt(cmd,1,filteredRadarResults.size())-1;var target=filteredRadarResults.get(index);return target.isRemoved()?null:target;}

}
