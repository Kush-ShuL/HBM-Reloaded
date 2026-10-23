package com.hbm.saveddata;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/** Original dimension-local impactData; no invented firestorm values or triggers. */
public final class TomSaveData extends SavedData {
    public static final String key="impactData";
    private static final Factory<TomSaveData> FACTORY=new Factory<>(TomSaveData::new,TomSaveData::load);
    private static final Map<Level,TomSaveData> CLIENT_DATA=new WeakHashMap<>();
    private static TomSaveData lastCachedUnsafe;
    public float dust,fire;
    public boolean impact;
    public static TomSaveData forWorld(Level world){
        var result=world instanceof ServerLevel server?server.getDataStorage().computeIfAbsent(FACTORY,key):CLIENT_DATA.computeIfAbsent(world,ignored->new TomSaveData());
        lastCachedUnsafe=result;return result;
    }
    public static TomSaveData getLastCachedOrNull(){return lastCachedUnsafe;}
    public static void resetLastCached(){lastCachedUnsafe=null;}
    public TomSaveData(){}
    public TomSaveData(String tagName){}
    private static TomSaveData load(CompoundTag tag,HolderLookup.Provider lookup){var result=new TomSaveData();result.readFromNBT(tag);return result;}
    public void readFromNBT(CompoundTag tag){dust=tag.getFloat("dust");fire=tag.getFloat("fire");impact=tag.getBoolean("impact");}
    public void writeToNBT(CompoundTag tag){tag.putFloat("dust",dust);tag.putFloat("fire",fire);tag.putBoolean("impact",impact);}
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){writeToNBT(tag);return tag;}
}
