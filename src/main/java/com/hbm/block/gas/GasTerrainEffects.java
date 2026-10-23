package com.hbm.block.gas;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** Exact unresolved terrain operations, ready for their actual block implementations. */
public final class GasTerrainEffects {
    public interface Backend {
        default boolean replaceGrassWithWasteEarth(ServerLevel level,BlockPos pos){return false;}
        default boolean depositFallout(ServerLevel level,BlockPos pos){return false;}
    }
    private static final List<Backend> BACKENDS=new CopyOnWriteArrayList<>();
    private GasTerrainEffects(){}
    public static void addBackend(Backend backend){BACKENDS.add(Objects.requireNonNull(backend));}
    public static void removeBackend(Backend backend){BACKENDS.remove(backend);}
    public static void wasteEarth(ServerLevel level,BlockPos pos){for(var backend:BACKENDS)if(backend.replaceGrassWithWasteEarth(level,pos))return;}
    public static void fallout(ServerLevel level,BlockPos pos){for(var backend:BACKENDS)if(backend.depositFallout(level,pos))return;}
}
