package com.hbm.client.network.debug;

import com.hbm.network.debug.NetworkPortDebug;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class NetworkPortDebugClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->NetworkPortDebug.CLIENT=packet->{
        var mc=Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new NetworkPortDebugParticle(mc.level,packet));
    });}
    private NetworkPortDebugClient(){}
}
