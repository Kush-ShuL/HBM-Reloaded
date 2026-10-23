package com.hbm.world.structure.templates.funnel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
/** Source caches are server-lifetime; modern datapack reload is an actual recipe-set change. */
@EventBusSubscriber(modid="hbm")
public final class FunnelEvents {
    @SubscribeEvent public static void stopped(ServerStoppedEvent event){FunnelBlockEntity.clearRecipeCaches();}
    @SubscribeEvent public static void recipes(OnDatapackSyncEvent event){if(event.getPlayer()==null)FunnelBlockEntity.clearRecipeCaches();}
    private FunnelEvents(){}
}
