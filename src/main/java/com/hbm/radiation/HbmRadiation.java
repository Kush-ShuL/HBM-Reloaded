package com.hbm.radiation;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Server-only owner of the selected chunk-radiation backend and shared effect lifecycle. */
public final class HbmRadiation {
    private static final Map<ServerLevel, RadiationWorld> WORLDS = new IdentityHashMap<>();
    private static final List<RadiationEffects> EFFECTS = new ArrayList<>();
    private static RadiationSettings settings = RadiationSettings.DEFAULT;
    private static int tickCounter, diffusionCycle;
    private static RadiationAlgorithm algorithm = RadiationAlgorithm.SIMPLE;
    private static boolean algorithmLocked;
    private static boolean registered;

    private HbmRadiation() {}

    public static void register(IEventBus modBus) {
        if (registered) return;
        registered = true;
        addEffects(new RadiationTerrainEffects());
        NeoForge.EVENT_BUS.addListener(HbmRadiation::levelLoaded);
        NeoForge.EVENT_BUS.addListener(HbmRadiation::levelUnloaded);
        NeoForge.EVENT_BUS.addListener(HbmRadiation::chunkLoaded);
        NeoForge.EVENT_BUS.addListener(HbmRadiation::chunkUnloaded);
        NeoForge.EVENT_BUS.addListener(HbmRadiation::tick);
        NeoForge.EVENT_BUS.addListener(HbmRadiation::serverStopped);
    }

    /** Call on the server thread, or before a server starts. */
    public static void configure(RadiationSettings updated) {
        settings = Objects.requireNonNull(updated);
        WORLDS.values().forEach(world -> world.configure(updated));
        if (!updated.enabled() || !updated.worldEffects().enabled()) {
            for (var effect : EFFECTS) if (effect instanceof RadiationTerrainEffects terrain) terrain.close();
        }
    }

    public static RadiationSettings settings() { return settings; }

    /** Selection is frozen when the first dimension opens, including across config reloads. */
    static void configureAlgorithm(RadiationAlgorithm selected) {
        Objects.requireNonNull(selected);
        if (algorithmLocked && algorithm != selected) {
            com.hbm.HbmReloaded.LOGGER.warn("辐射算法修改需重启世界；本次会话继续使用{}。", algorithm);
            return;
        }
        algorithm = selected;
    }

    public static RadiationAlgorithm algorithm() { return algorithm; }

    public static void addEffects(RadiationEffects effects) { EFFECTS.add(Objects.requireNonNull(effects)); }
    public static void removeEffects(RadiationEffects effects) {
        if (EFFECTS.remove(effects) && effects instanceof RadiationTerrainEffects terrain) terrain.close();
    }

    public static float getRadiation(ServerLevel level, int x, int y, int z) {
        if (!settings.enabled()) return 0F;
        return world(level).getRadiation(x, y, z);
    }

    /** External writes never synchronously load a chunk; PRISM retains the actual Y section. */
    public static void setRadiation(ServerLevel level, int x, int y, int z, float radiation) {
        if (settings.enabled()) world(level).setRadiation(x, y, z, radiation);
    }

    public static void incrementRad(ServerLevel level, int x, int y, int z, float radiation) {
        setRadiation(level, x, y, z, getRadiation(level, x, y, z) + radiation);
    }

    public static void decrementRad(ServerLevel level, int x, int y, int z, float radiation) {
        setRadiation(level, x, y, z, Math.max(getRadiation(level, x, y, z) - radiation, 0F));
    }

    /** Clears both active cells and this dimension's suspended saved values. */
    public static void clearSystem(ServerLevel level) { world(level).clear(); }

    private static RadiationWorld world(ServerLevel level) {
        algorithmLocked = true;
        return WORLDS.computeIfAbsent(level, key -> algorithm == RadiationAlgorithm.PRISM
                ? new PrismRadiationWorld(key) : new SimpleRadiationWorld(key, settings));
    }

    private static void levelLoaded(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) world(level);
    }

    private static void levelUnloaded(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            var removed = WORLDS.remove(level);
            if (removed != null) removed.close();
            for (var effect : EFFECTS) if (effect instanceof RadiationTerrainEffects terrain) terrain.unload(level);
        }
    }

    private static void chunkLoaded(ChunkEvent.Load event) {
        // The event can precede FULL promotion: no level/chunk lookups occur here.
        if (event.getLevel() instanceof ServerLevel level) world(level).loadChunk(event.getChunk().getPos());
    }

    private static void chunkUnloaded(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            RadiationWorld world = WORLDS.get(level);
            if (world != null) world.unloadChunk(event.getChunk().getPos());
        }
    }

    private static void tick(ServerTickEvent.Post event) {
        if (!settings.enabled()) return;
        tickCounter++;
        boolean diffuse = tickCounter >= settings.updateIntervalTicks();
        if (diffuse) { tickCounter = 0; diffusionCycle++; }
        List<RadiationEffects> effects = List.copyOf(EFFECTS);
        for (var entry : WORLDS.entrySet()) {
            ServerLevel level = entry.getKey();
            if (level.getServer() != event.getServer()) continue;
            if (diffuse) entry.getValue().update(effects, settings, diffusionCycle);
            if (settings.worldEffects().enabled()) {
                entry.getValue().worldEffects(effects, settings);
            }
        }
    }

    private static void serverStopped(ServerStoppedEvent event) {
        for (var effect : EFFECTS) if (effect instanceof RadiationTerrainEffects terrain) terrain.close();
        WORLDS.values().forEach(RadiationWorld::close);
        WORLDS.clear();
        tickCounter = diffusionCycle = 0;
        algorithmLocked = false;
        algorithm = RadiationAlgorithm.SIMPLE;
    }
}
