package com.hbm.client.door.transition;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

/** Source has no transition-seal item renderer registration; original flat block icon remains. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TransitionSealClient {
    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event){event.registerReloadListener(TransitionSealModel.INSTANCE);}
    private TransitionSealClient(){}
}
