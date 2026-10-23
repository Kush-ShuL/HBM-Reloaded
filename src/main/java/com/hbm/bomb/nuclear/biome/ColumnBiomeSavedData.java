package com.hbm.bomb.nuclear.biome;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Dimension-local exact biome columns and their original3D samples survive chunk unload and restart. */
public final class ColumnBiomeSavedData extends SavedData {
    public static final String NAME="hbm_column_biomes";
    private static final Factory<ColumnBiomeSavedData> FACTORY=new Factory<>(ColumnBiomeSavedData::new,ColumnBiomeSavedData::load);
    final ConcurrentHashMap<Long,ColumnBiomeChunk> chunks=new ConcurrentHashMap<>();
    public static ColumnBiomeSavedData get(ServerLevel level){return level.getDataStorage().computeIfAbsent(FACTORY,NAME);}
    private static ColumnBiomeSavedData load(CompoundTag tag,HolderLookup.Provider registries){var result=new ColumnBiomeSavedData();var list=tag.getList("chunks",Tag.TAG_COMPOUND);for(int i=0;i<list.size();i++){var entry=list.getCompound(i);result.chunks.put(entry.getLong("chunk"),ColumnBiomeChunk.load(entry));}return result;}
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registries){tag.putInt("version",1);var list=new ListTag();chunks.forEach((pos,value)->{var entry=value.save();entry.putLong("chunk",pos);list.add(entry);});tag.put("chunks",list);return tag;}
}
