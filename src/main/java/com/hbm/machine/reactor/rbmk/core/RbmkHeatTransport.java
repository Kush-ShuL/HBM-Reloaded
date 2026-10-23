package com.hbm.machine.reactor.rbmk.core;
import java.util.*;import com.hbm.machine.reactor.rbmk.RbmkDials;import net.minecraft.core.Direction;import net.minecraft.server.level.ServerLevel;
/** Exact TileEntityRBMKBase moveHeat/boilWater order, N/E/S/W cache and integer remainders. Each real column owns one instance. */
public final class RbmkHeatTransport {
 private static final Direction[] DIRECTIONS={Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};private final RbmkColumnState[] neighbors=new RbmkColumnState[4];
 public void tick(ServerLevel world,RbmkColumnState self){boolean reasim=RbmkDials.getReasimBoilers(world);var receivers=new ArrayList<RbmkColumnState>();receivers.add(self);double totalHeat=self.heat();int totalWater=self.reasimWater(),totalSteam=self.reasimSteam();for(int i=0;i<4;i++){if(neighbors[i]!=null&&neighbors[i].entity().isRemoved())neighbors[i]=null;if(neighbors[i]==null){var pos=self.entity().getBlockPos().relative(DIRECTIONS[i]);if(world.hasChunkAt(pos)&&world.getBlockEntity(pos) instanceof RbmkColumnState neighbor)neighbors[i]=neighbor;}}
  for(var neighbor:neighbors)if(neighbor!=null){receivers.add(neighbor);totalHeat+=neighbor.heat();if(reasim){totalWater+=neighbor.reasimWater();totalSteam+=neighbor.reasimSteam();}}
  int members=receivers.size();double step=RbmkDials.getColumnHeatFlow(world);if(members>1){double targetHeat=totalHeat/members;int targetWater=totalWater/members,remainderWater=totalWater%members,targetSteam=totalSteam/members,remainderSteam=totalSteam%members;for(var column:receivers){column.heat(column.heat()+(targetHeat-column.heat())*step);if(reasim){column.reasimWater(targetWater);column.reasimSteam(targetSteam);}}if(reasim){self.reasimWater(self.reasimWater()+remainderWater);self.reasimSteam(self.reasimSteam()+remainderSteam);}self.changed();}
  self.heat(Math.max(20,self.heat()-self.passiveCooling(members-1)));if(reasim)boilWater(world,self);
 }
 private static void boilWater(ServerLevel world,RbmkColumnState self){if(self.heat()<100)return;double consumption=RbmkDials.getBoilerHeatConsumption(world);double availableHeat=(self.heat()-100)/consumption,availableWater=self.reasimWater(),availableSpace=16000-self.reasimSteam();int amount=(int)Math.floor(sourceMin(availableHeat,availableWater,availableSpace)*net.minecraft.util.Mth.clamp(RbmkDials.getReaSimBoilerSpeed(world),0,1));if(amount<=0)return;self.reasimWater(self.reasimWater()-amount);self.reasimSteam(self.reasimSteam()+amount);self.heat(self.heat()-amount*consumption);}
 private static double sourceMin(double... values){double minimum=Double.MAX_VALUE;for(double value:values)if(value<minimum)minimum=value;return minimum;}
 public void clear(){Arrays.fill(neighbors,null);}
}
