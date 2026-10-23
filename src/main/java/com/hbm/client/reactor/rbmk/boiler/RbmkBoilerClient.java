package com.hbm.client.reactor.rbmk.boiler;
import com.hbm.machine.reactor.rbmk.boiler.*;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkBoilerClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RbmkBoilerContent.MENU.get(),RbmkBoilerScreen::new);}
    public static void steam(RbmkBoilerEffects packet){var game=Minecraft.getInstance();if(game.level!=null)game.particleEngine.add(new RbmkSteamParticle(game.level,packet.x(),packet.y(),packet.z()));}
    private RbmkBoilerClient(){}
}
