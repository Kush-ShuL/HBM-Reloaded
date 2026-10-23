package com.hbm.pollution;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

/** Dimension-local persistence; channel names and cell coordinates match the original. */
public final class PollutionWorldData extends SavedData {
    public static final Factory<PollutionWorldData> FACTORY = new Factory<>(PollutionWorldData::new, PollutionWorldData::load);
    final Map<PollutionData.Cell, PollutionData> cells = new HashMap<>();

    public static PollutionWorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        PollutionWorldData result = new PollutionWorldData();
        ListTag entries = tag.getList("entries", Tag.TAG_COMPOUND);
        for (int index = 0; index < entries.size(); index++) {
            CompoundTag entry = entries.getCompound(index);
            PollutionData value = new PollutionData();
            for (PollutionType type : PollutionType.values()) value.set(type, entry.getFloat(type.name().toLowerCase(Locale.ROOT)));
            result.cells.put(new PollutionData.Cell(entry.getInt("chunkX"), entry.getInt("chunkZ")), value);
        }
        return result;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();
        cells.forEach((cell, value) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("chunkX", cell.x());
            entry.putInt("chunkZ", cell.z());
            for (PollutionType type : PollutionType.values()) entry.putFloat(type.name().toLowerCase(Locale.ROOT), value.get(type));
            entries.add(entry);
        });
        tag.put("entries", entries);
        return tag;
    }

    void advance() {
        if (cells.isEmpty()) return;
        Map<PollutionData.Cell, PollutionData> next = PollutionData.advance(cells);
        cells.clear();
        cells.putAll(next);
        setDirty();
    }
}
