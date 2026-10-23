package com.hbm.radiation;

import java.util.HashSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/** Owns the active grid and its dimension's authoritative SavedData. */
final class SimpleRadiationWorld implements RadiationWorld {
    private final ServerLevel level;
    private final RadiationSavedData saved;
    private final RadiationGrid active;
    private RadiationSettings settings;

    SimpleRadiationWorld(ServerLevel level, RadiationSettings settings) {
        this.level = level;
        this.settings = settings;
        saved = RadiationSavedData.get(level);
        saved.selectAlgorithm(RadiationAlgorithm.SIMPLE, level.dimension().location().toString());
        active = new RadiationGrid(settings);
        saved.seedTerrain(settings.worldEffects());
    }

    @Override public void configure(RadiationSettings settings) {
        this.settings = settings;
        active.configure(settings);
        saved.seedTerrain(settings.worldEffects());
        for (var entry : active.values().entrySet())
            saved.enqueueTerrain(entry.getKey(), active.clamp(entry.getValue()), settings.worldEffects());
    }
    RadiationGrid active() { return active; }

    @Override public void worldEffects(List<RadiationEffects> effects, RadiationSettings settings) {
        for (var effect : effects) {
            if (effect instanceof RadiationTerrainEffects terrain) terrain.onWorldEffects(level, active, settings);
            else effect.onWorldEffects(level, active.values(), settings);
        }
    }

    @Override public void loadChunk(ChunkPos chunk) {
        active.loadChunk(chunk.toLong(), saved.getRadiation(chunk.toLong()));
        saved.enqueueTerrain(chunk.toLong(), active.getChunkRadiation(chunk.toLong()), settings.worldEffects());
    }

    @Override public void unloadChunk(ChunkPos chunk) {
        long key = chunk.toLong();
        if (active.containsChunk(key)) saved.setRadiation(key, active.getChunkRadiation(key));
        active.unloadChunk(key);
    }

    @Override public float getRadiation(int x, int y, int z) {
        return active.getChunkRadiation(RadiationGrid.key(x >> 4, z >> 4));
    }

    @Override public void setRadiation(int x, int y, int z, float value) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (level.getChunkSource().getChunkNow(chunkX, chunkZ) == null) return;
        long key = RadiationGrid.key(chunkX, chunkZ);
        active.setChunkRadiation(key, value);
        saved.setRadiation(key, active.getChunkRadiation(key));
        saved.enqueueTerrain(key, active.getChunkRadiation(key), settings.worldEffects());
    }

    @Override public void clear() {
        active.clear();
        saved.clear();
    }

    @Override public void update(List<RadiationEffects> effects, RadiationSettings settings, int cycle) {
        var previousKeys = new HashSet<>(active.values().keySet());
        active.diffuse((source, target, radiation) -> {
            if (effects.isEmpty() || level.getChunkSource().getChunkNow(RadiationGrid.chunkX(source), RadiationGrid.chunkZ(source)) == null) return;
            for (var effect : effects) effect.onDiffusionWrite(level, new ChunkPos(source), new ChunkPos(target), radiation, settings);
        });
        // Removed active cells must clear their old saved value; unloaded cells that
        // were already suspended are unaffected until reloaded or reached by diffusion.
        for (long key : previousKeys) saved.setRadiation(key, active.getChunkRadiation(key));
        for (var entry : active.values().entrySet()) {
            float radiation = active.clamp(entry.getValue());
            saved.setRadiation(entry.getKey(), radiation);
            saved.enqueueTerrain(entry.getKey(), radiation, settings.worldEffects());
        }
    }
}
