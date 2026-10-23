package com.hbm.radiation;

import java.util.List;
import net.minecraft.world.level.ChunkPos;

/** Only one backend exists per dimension and owns its authoritative saved field. */
interface RadiationWorld {
    void configure(RadiationSettings settings);
    void loadChunk(ChunkPos chunk);
    void unloadChunk(ChunkPos chunk);
    float getRadiation(int x, int y, int z);
    void setRadiation(int x, int y, int z, float radiation);
    void update(List<RadiationEffects> effects, RadiationSettings settings, int cycle);
    default void worldEffects(List<RadiationEffects> effects, RadiationSettings settings) {}
    default void close() {}
    void clear();
}
