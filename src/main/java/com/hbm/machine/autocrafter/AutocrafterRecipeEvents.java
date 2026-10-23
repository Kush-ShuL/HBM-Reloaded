package com.hbm.machine.autocrafter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
/** Modern datapack recipe changes are the only additional rebuild point; no per-tick full-recipe rescan. */
@EventBusSubscriber(modid="hbm")
public final class AutocrafterRecipeEvents {
    private static long revision;
    public static long revision(){return revision;}
    @SubscribeEvent public static void recipes(OnDatapackSyncEvent event){if(event.getPlayer()==null)revision++;}
    @SubscribeEvent public static void stopped(ServerStoppedEvent event){revision++;}
    private AutocrafterRecipeEvents(){}
}
