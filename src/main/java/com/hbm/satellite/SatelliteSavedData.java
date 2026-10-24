package com.hbm.satellite;
import java.util.*;import net.minecraft.core.HolderLookup;import net.minecraft.nbt.*;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.level.saveddata.SavedData;
/** Original satellites.dat is per dimension. Unmigrated source classes remain opaque, reserved and lossless. */
public final class SatelliteSavedData extends SavedData {
 public static final Factory<SatelliteSavedData> FACTORY=new Factory<>(SatelliteSavedData::new,SatelliteSavedData::load);
 public final HashMap<Integer,SatelliteBase> sats=new HashMap<>();private final HashMap<Integer,Unbound> unbound=new HashMap<>();public record Unbound(int sourceId,CompoundTag sourceData){}
 public SatelliteSavedData(){setDirty();}public boolean isFreqTaken(int freq){return sats.containsKey(freq)||unbound.containsKey(freq);}public SatelliteBase getSatFromFreq(int freq){return sats.get(freq);}public Set<Integer> frequencies(){var keys=new HashSet<>(sats.keySet());keys.addAll(unbound.keySet());return Collections.unmodifiableSet(keys);}public boolean remove(int freq){boolean removed=sats.remove(freq)!=null|unbound.remove(freq)!=null;if(removed)setDirty();return removed;}
 public static SatelliteSavedData getData(ServerLevel world){return world.getDataStorage().computeIfAbsent(FACTORY,"satellites");}
 public static SatelliteSavedData load(CompoundTag nbt,HolderLookup.Provider lookup){var data=new SatelliteSavedData();for(int i=0;i<nbt.getInt("satCount");i++){int id=nbt.getInt("sat_id_"+i),freq=nbt.getInt("sat_freq_"+i);var state=nbt.getCompound("sat_data_"+i);var sat=SatelliteRegistry.createFromId(id);if(sat==null)data.unbound.put(freq,new Unbound(id,state.copy()));else{sat.readFromNBT(state,lookup);data.sats.put(freq,sat);}}return data;}
 @Override public CompoundTag save(CompoundTag nbt,HolderLookup.Provider lookup){nbt.putInt("satCount",sats.size()+unbound.size());int i=0;for(var entry:sats.entrySet()){var state=new CompoundTag();entry.getValue().writeToNBT(state,lookup);nbt.putInt("sat_id_"+i,entry.getValue().getID());nbt.putInt("sat_freq_"+i,entry.getKey());nbt.put("sat_data_"+i,state);i++;}for(var entry:unbound.entrySet()){nbt.putInt("sat_id_"+i,entry.getValue().sourceId());nbt.putInt("sat_freq_"+i,entry.getKey());nbt.put("sat_data_"+i,entry.getValue().sourceData().copy());i++;}return nbt;}
 public static void update(ServerLevel world){
  // Source world START order: detector expiry, satellite work/dirty state, then ray expiry.
  com.hbm.bomb.nuclear.SatelliteBursts.updateSystem(world);
  var data=getData(world);boolean dirty=false;
  for(var satellite:data.sats.values()){satellite.onUpdateTick(world);if(satellite.isDirty){satellite.isDirty=false;dirty=true;}}
  if(dirty)data.setDirty();
  if(world.getGameTime()%20==10)SatelliteRayEvents.updateSystem(world);
 }
}
