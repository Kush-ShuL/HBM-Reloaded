package com.hbm.client.reactor.rbmk.console;
import com.hbm.machine.reactor.rbmk.console.*;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkConsoleClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkConsoleContent.ENTITY.get(),RbmkConsoleRenderer::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RbmkConsoleContent.OPEN=pos->{var game=Minecraft.getInstance();if(game.level!=null&&game.level.getBlockEntity(pos) instanceof RbmkConsoleEntity console)game.setScreen(new RbmkConsoleScreen(console));});}
    public static void update(RbmkConsoleNetwork.Update packet){var game=Minecraft.getInstance();if(game.level!=null&&packet.data()!=null&&game.level.getBlockEntity(packet.pos()) instanceof RbmkConsoleEntity console)console.sync(packet.data());}
    private RbmkConsoleClient(){}
}
