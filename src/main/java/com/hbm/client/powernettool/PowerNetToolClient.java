package com.hbm.client.powernettool;
import com.hbm.item.powernettool.PowerNetToolNotice;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PowerNetToolClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(()->PowerNetToolNotice.CLIENT=packet->{var mc=Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new PowerNetTextParticle(mc.level,packet.x(),packet.y(),packet.z(),packet.text()));}); }
    private PowerNetToolClient() { }
}
