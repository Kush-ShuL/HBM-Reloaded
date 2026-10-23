package com.hbm.radiation;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Dimension-local persistent values, including suspended unloaded chunks. */
public final class RadiationSavedData extends SavedData {
    public static final String DATA_NAME = "hbm_simple_radiation";
    private static final Factory<RadiationSavedData> FACTORY =
            new Factory<>(RadiationSavedData::new, RadiationSavedData::load);
    private RadiationAlgorithm algorithm = RadiationAlgorithm.SIMPLE;
    private String invalidAlgorithm;
    private final Map<Long, Float> radiation = new HashMap<>();
    // Mutually exclusive representation of the SAME field; never a parallel independent dose store.
    private final Map<Long, Map<Integer, PrismRadiationCell>> prism = new HashMap<>();
    // Work survives radiation decay and chunk/world unload. It is not a second dose field.
    private final LinkedHashMap<Long, TerrainWork> terrain = new LinkedHashMap<>();

    static final class TerrainWork {
        final long chunk;
        float peakRadiation;
        int column;

        TerrainWork(long chunk, float peakRadiation, int column) {
            this.chunk = chunk;
            this.peakRadiation = peakRadiation;
            this.column = column;
        }
    }

    public static RadiationSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static RadiationSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        var result = new RadiationSavedData();
        if (tag.contains("Algorithm", Tag.TAG_STRING)) {
            try { result.algorithm = RadiationAlgorithm.valueOf(tag.getString("Algorithm")); }
            catch (IllegalArgumentException bad) { result.invalidAlgorithm = tag.getString("Algorithm"); }
        }
        ListTag entries = tag.getList("Chunks", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            long chunk = entry.getLong("Chunk");
            if (result.algorithm == RadiationAlgorithm.SIMPLE) {
                float value = entry.getFloat("Radiation");
                if (Float.isFinite(value) && value > 0F) result.radiation.put(chunk, value);
            } else {
                var sections = new HashMap<Integer, PrismRadiationCell>();
                var cells = entry.getList("Sections", Tag.TAG_COMPOUND);
                for (int j = 0; j < cells.size(); j++) {
                    var cell = cells.getCompound(j);
                    sections.put(cell.getInt("SectionY"), PrismRadiationCell.load(cell));
                }
                result.prism.put(chunk, sections);
            }
        }
        ListTag work = tag.getList("TerrainWork", Tag.TAG_COMPOUND);
        for (int i = 0; i < work.size(); i++) {
            CompoundTag entry = work.getCompound(i);
            float peak = entry.getFloat("PeakRadiation");
            int column = entry.getInt("Column");
            if (Float.isFinite(peak) && peak >= 0F && column >= 0 && column < 256) {
                long chunk = entry.getLong("Chunk");
                result.terrain.put(chunk, new TerrainWork(chunk, peak, column));
            }
        }
        return result;
    }

    /** Reuse the one saved field; no dimensional guess, automatic conversion, or silent dose loss. */
    void selectAlgorithm(RadiationAlgorithm requested, String dimension) {
        if (invalidAlgorithm != null) throw new IllegalStateException("辐射存储中的算法标记无效：" + invalidAlgorithm + "。未修改任何辐射数据。");
        if (algorithm == requested) return;
        boolean nonzero = radiation.values().stream().anyMatch(value -> value > 0F);
        if (!nonzero) for (var column : prism.values()) {
            if (column.values().stream().anyMatch(cell -> cell.radiation > 0F)) { nonzero = true; break; }
        }
        if (nonzero) throw new IllegalStateException("维度" + dimension + "的非零辐射场采用" + algorithm
                + "，不能直接切换到" + requested + "。请将chunk.algorithm改回" + algorithm
                + "后重启；只有原辐射场清空后才能切换。已保存的剂量和地形处理进度均未删除。");
        radiation.clear();
        prism.clear();
        algorithm = requested;
        setDirty();
    }

    PrismRadiationCell prismSection(long chunk, int sectionY) {
        var cells = prism.get(chunk);
        return cells == null ? null : cells.get(sectionY);
    }

    void storePrismChunk(long chunk, int minSection, PrismRadiationCell[] cells) {
        if (algorithm != RadiationAlgorithm.PRISM) throw new IllegalStateException("PRISM field is not selected");
        var sections = prism.computeIfAbsent(chunk, unused -> new HashMap<>());
        for (int i = 0; i < cells.length; i++) if (cells[i] != null) sections.put(minSection + i, cells[i]);
        setDirty();
    }

    /** Capture eligibility now, not when a potentially delayed terrain job is executed. */
    void enqueueTerrain(long chunk, float value, RadiationSettings.WorldEffects config) {
        if (!config.enabled() || config.sampledChunks() == 0 || config.attemptsPerChunk() == 0
                || !Float.isFinite(value) || value < config.minimumRadiation()) return;
        TerrainWork work = terrain.get(chunk);
        if (work == null) {
            terrain.put(chunk, new TerrainWork(chunk, value, 0));
            setDirty();
        } else if (value > work.peakRadiation) {
            work.peakRadiation = value;
            setDirty();
        }
    }

    /** Seed old saves once on world load/configuration, without requesting any chunks. */
    void seedTerrain(RadiationSettings.WorldEffects config) {
        for (var entry : radiation.entrySet()) enqueueTerrain(entry.getKey(), entry.getValue(), config);
    }

    List<TerrainWork> terrainWindow(int limit) {
        var window = new ArrayList<TerrainWork>(Math.min(limit, terrain.size()));
        for (TerrainWork work : terrain.values()) {
            if (window.size() == limit) break;
            window.add(work);
        }
        return window;
    }

    boolean hasTerrainWork() { return !terrain.isEmpty(); }
    int terrainSize() { return terrain.size(); }

    void advanceTerrain(TerrainWork work) {
        work.column++;
        if (work.column == 256) terrain.remove(work.chunk);
        setDirty();
    }

    void deferTerrain(TerrainWork work) {
        // Cold work goes behind the other jobs, retaining its column and captured dose.
        if (terrain.remove(work.chunk) != null) {
            terrain.put(work.chunk, work);
            setDirty();
        }
    }

    public float getRadiation(long chunk) {
        if (algorithm != RadiationAlgorithm.SIMPLE) throw new IllegalStateException("SIMPLE field is not selected");
        return radiation.getOrDefault(chunk, 0F);
    }

    public void setRadiation(long chunk, float value) {
        if (algorithm != RadiationAlgorithm.SIMPLE) throw new IllegalStateException("SIMPLE field is not selected");
        Float previous = radiation.get(chunk);
        if (value <= 0F || !Float.isFinite(value)) {
            if (previous != null) { radiation.remove(chunk); setDirty(); }
        } else if (previous == null || Float.compare(previous, value) != 0) {
            radiation.put(chunk, value);
            setDirty();
        }
    }

    public void clear() {
        if (!radiation.isEmpty() || !prism.isEmpty() || !terrain.isEmpty()) {
            radiation.clear();
            prism.clear();
            terrain.clear();
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        if (invalidAlgorithm != null) throw new IllegalStateException("拒绝覆盖算法标记无效的辐射数据。");
        var entries = new ListTag();
        if (algorithm == RadiationAlgorithm.SIMPLE) {
            for (var value : radiation.entrySet()) {
                var entry = new CompoundTag();
                entry.putLong("Chunk", value.getKey());
                entry.putFloat("Radiation", value.getValue());
                entries.add(entry);
            }
        } else {
            for (var column : prism.entrySet()) {
                var entry = new CompoundTag();
                entry.putLong("Chunk", column.getKey());
                var sections = new ListTag();
                for (var cell : column.getValue().entrySet()) sections.add(cell.getValue().save(cell.getKey()));
                entry.put("Sections", sections);
                entries.add(entry);
            }
        }
        var workEntries = new ListTag();
        for (TerrainWork work : terrain.values()) {
            var entry = new CompoundTag();
            entry.putLong("Chunk", work.chunk);
            entry.putFloat("PeakRadiation", work.peakRadiation);
            entry.putInt("Column", work.column);
            workEntries.add(entry);
        }
        tag.putInt("Version", 3);
        tag.putString("Algorithm", algorithm.name());
        tag.put("Chunks", entries);
        tag.put("TerrainWork", workEntries);
        return tag;
    }
}
