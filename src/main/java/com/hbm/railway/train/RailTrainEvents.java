package com.hbm.railway.train;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
/** Sole actual source world-END physics hook, no per-entity duplicate LTU integration. */
@EventBusSubscriber(modid="hbm")
public final class RailTrainEvents {
    @SubscribeEvent public static void afterWorld(LevelTickEvent.Post event){if(event.getLevel() instanceof ServerLevel level)RailTrainMotion.updateMotion(level);}
    private RailTrainEvents(){}
}
