package com.hbm.radiation;

import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * Integration points for source radiation particles and terrain changes.
 * Consumers must use already-loaded chunks and must not request chunk tickets.
 */
public interface RadiationEffects {
    /** Called in source contribution order; the source chunk is already loaded. */
    default void onDiffusionWrite(ServerLevel level, ChunkPos source, ChunkPos target,
                                  float radiation, RadiationSettings settings) {}

    /** Called each server tick, matching ChunkRadiationManager's world-effects hook. */
    default void onWorldEffects(ServerLevel level, Map<Long, Float> activeRadiation,
                               RadiationSettings settings) {}
}
