package com.hbm.config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
/** A disconnected server cannot leave its gameplay-mode snapshot in the next world. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class GeneralModesClient {
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){GeneralModes.clearRemote();}
    private GeneralModesClient(){}
}
