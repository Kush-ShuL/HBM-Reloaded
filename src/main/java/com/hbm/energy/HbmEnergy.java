package com.hbm.energy;

import com.hbm.HbmReloaded;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Native long HE capability and server lifecycle for the rewritten nodespace. */
public final class HbmEnergy {
    public static final BlockCapability<EnergyConnector, Direction> HE =
            BlockCapability.createSided(HbmReloaded.id("he"), EnergyConnector.class);
    private static final Map<ServerLevel, PowerNodespace> WORLDS = new IdentityHashMap<>();
    private static boolean registered;

    private HbmEnergy() {}

    public static void register(IEventBus modBus) {
        if (registered) return;
        registered = true;
        modBus.addListener(HbmEnergy::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(HbmEnergy::tick);
        NeoForge.EVENT_BUS.addListener(HbmEnergy::unload);
        NeoForge.EVENT_BUS.addListener(HbmEnergy::serverStopped);
    }

    public static PowerNodespace nodespace(ServerLevel level) {
        return WORLDS.computeIfAbsent(level, ignored -> new PowerNodespace(level));
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet().forEach(entry -> {
            if (!entry.getKey().location().getNamespace().equals(HbmReloaded.MOD_ID)) return;
            event.registerBlockEntity(HE, entry.getValue(), (machine, side) -> {
                if (machine.isRemoved()) return null;
                EnergyConnector connector;
                if (machine instanceof EnergyCapabilityProvider provider) {
                    connector = provider.energyConnector(side);
                } else {
                    connector = machine instanceof EnergyConnector participant ? participant : null;
                }
                if (connector == null || !connector.isLoaded()) return null;
                if (connector instanceof SidedEnergyConnector sided) return sided.canConnect(side) ? connector : null;
                return side != null ? connector : null;
            });
        });
    }

    private static void tick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            PowerNodespace space = WORLDS.get(level);
            if (space != null) space.tick();
        }
    }

    private static void unload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            PowerNodespace space = WORLDS.remove(level);
            if (space != null) space.clear();
        }
    }

    private static void serverStopped(ServerStoppedEvent event) {
        WORLDS.values().forEach(PowerNodespace::clear);
        WORLDS.clear();
    }
    /** Invalidate all existing graphs for this server without creating a world or a node. */
    public static void clearServer(net.minecraft.server.MinecraftServer server){
        WORLDS.entrySet().removeIf(entry->{
            if(entry.getKey().getServer()!=server)return false;
            entry.getValue().clear();
            return true;
        });
    }
}
