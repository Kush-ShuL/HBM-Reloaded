package com.hbm.satellite;
import net.minecraft.server.level.ServerLevel;import net.minecraft.world.item.ItemStack;
/** One actual pod transport binding; absence is a failed spawn and never consumes the real satellite inventory. */
public final class SatelliteCargo {
 @FunctionalInterface public interface Transport{boolean spawn(ServerLevel level,int x,int callerY,int z,ItemStack[] cargo);}
 public static Transport transport;
 public static boolean request(ServerLevel level,int x,int y,int z,ItemStack[] cargo){return transport!=null&&transport.spawn(level,x,y,z,cargo);}
 private SatelliteCargo(){}
}
