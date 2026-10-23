package com.hbm.fusion.plasma;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
@EventBusSubscriber(modid="hbm")
public final class PlasmaNetworks {
    private static final Map<ServerLevel,PlasmaNodespace> WORLDS=new IdentityHashMap<>();
    public static PlasmaNodespace nodespace(ServerLevel level){return WORLDS.computeIfAbsent(level,ignored->new PlasmaNodespace());}
    @SubscribeEvent public static void tick(LevelTickEvent.Post event){if(event.getLevel() instanceof ServerLevel level){var space=WORLDS.get(level);if(space!=null)space.tick();}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level){var space=WORLDS.remove(level);if(space!=null)space.clear();}}
    @SubscribeEvent public static void stop(ServerStoppedEvent event){WORLDS.values().forEach(PlasmaNodespace::clear);WORLDS.clear();}
    private PlasmaNetworks(){}
}
