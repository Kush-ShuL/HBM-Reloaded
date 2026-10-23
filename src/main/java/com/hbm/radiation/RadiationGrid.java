package com.hbm.radiation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Dependency-free rewrite of ChunkRadiationHandlerSimple's float grid.
 * The original per-write decay and previously-absent-target overwrite branches
 * are intentional here; this is not a conventional summed convolution.
 */
public final class RadiationGrid {
    private final Map<Long, Float> radiation = new HashMap<>();
    private RadiationSettings settings;
    // Cached only while HashMap topology/order is unchanged. These are the read-only
    // view's entry wrappers: existing-key value writes remain live, setValue cannot bypass us.
    private Map.Entry<?, ?>[] worldEffectEntries;

    /** The sole terrain sampler may reuse this array; other public effects still receive a read-only Map. */
    Map.Entry<?, ?>[] worldEffectEntries() {
        if (worldEffectEntries == null) worldEffectEntries = values().entrySet().toArray(Map.Entry[]::new);
        return worldEffectEntries;
    }

    /** All raw writes, including source's intentionally unclamped absent-target diffusion branch. */
    private void putRaw(long chunk, float value) {
        int previousSize = radiation.size();
        radiation.put(chunk, value);
        if (radiation.size() != previousSize) worldEffectEntries = null;
    }

    public RadiationGrid(RadiationSettings settings) {
        this.settings = Objects.requireNonNull(settings);
    }

    public void configure(RadiationSettings settings) {
        this.settings = Objects.requireNonNull(settings);
    }

    public RadiationSettings settings() { return settings; }
    public boolean containsChunk(long chunk) { return radiation.containsKey(chunk); }
    public Map<Long, Float> values() { return Collections.unmodifiableMap(radiation); }
    public float getChunkRadiation(long chunk) { return clamp(radiation.getOrDefault(chunk, 0F)); }

    /** Includes zero loaded chunks, since snapshot membership affects decay. */
    public void loadChunk(long chunk, float value) { putRaw(chunk, clamp(value)); }

    /** Correct ChunkPos key removal, rather than the legacy Chunk-object key. */
    public void unloadChunk(long chunk) { if (radiation.remove(chunk) != null) worldEffectEntries = null; }

    public void setChunkRadiation(long chunk, float value) { putRaw(chunk, clamp(value)); }
    public void incrementChunkRadiation(long chunk, float amount) { setChunkRadiation(chunk, getChunkRadiation(chunk) + amount); }
    public void decrementChunkRadiation(long chunk, float amount) { setChunkRadiation(chunk, Math.max(getChunkRadiation(chunk) - amount, 0F)); }
    public void clear() { radiation.clear(); worldEffectEntries = null; }

    public void diffuse(DiffusionListener listener) {
        Map<Long, Float> previous = new HashMap<>(radiation);
        clear();
        var parameters = settings.diffusion();
        for (var source : previous.entrySet()) {
            float value = source.getValue();
            if (value == 0F) continue;
            int sourceX = chunkX(source.getKey());
            int sourceZ = chunkZ(source.getKey());
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                    long target = key(sourceX + offsetX, sourceZ + offsetZ);
                    float contribution = value * parameters.weight(offsetX, offsetZ);
                    float next;
                    if (previous.containsKey(target)) {
                        float combined = radiation.getOrDefault(target, 0F) + contribution;
                        next = clamp(combined * parameters.decayMultiplier() - parameters.decaySubtraction());
                    } else {
                        // Source implementation uses put, even if another source wrote here.
                        next = contribution;
                    }
                    putRaw(target, next);
                    listener.onWrite(source.getKey(), target, next);
                }
            }
        }
    }

    public void diffuse() { diffuse((source, target, value) -> {}); }

    public float clamp(float value) {
        if (Float.isNaN(value)) return 0F;
        return Math.min(Math.max(value, 0F), settings.maximumRadiation());
    }

    /** Same packed coordinate layout as modern ChunkPos.asLong(). */
    public static long key(int chunkX, int chunkZ) { return chunkX & 0xFFFFFFFFL | (chunkZ & 0xFFFFFFFFL) << 32; }
    public static int chunkX(long key) { return (int) key; }
    public static int chunkZ(long key) { return (int) (key >>> 32); }

    @FunctionalInterface
    public interface DiffusionListener {
        void onWrite(long sourceChunk, long targetChunk, float radiation);
    }
}
