package com.hbm.satellite.types;
import com.hbm.satellite.*;import api.hbm.redstoneoverradio.IRORInteractive;import net.minecraft.server.level.ServerLevel;import net.minecraft.network.chat.Component;import java.util.*;
public final class SatelliteRelay extends SatelliteBase {
 @Override public String getType(){return "DIMENSIONAL_RELAY";}
 @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.satellite.relay.name")};}

 public static final String CMD_RELAY="relay";
 @Override public void onCommandImpl(ServerLevel world,String...cmd){if(cmd.length==0)return;if(cmd[0].equals(CMD_RELAY)&&cmd.length>3){int dim=IRORInteractive.parseInt(cmd[1]);var key=SatelliteDimensions.resolve(dim);var target=key==null?null:world.getServer().getLevel(key);if(target!=null){var signal=new StringBuilder();for(int i=3;i<cmd.length;i++){if(i>3)signal.append(" ");signal.append(cmd[i]);}com.hbm.world.structure.templates.radio.RTTYSystem.broadcast(target,cmd[2],signal.toString());}}}

}
