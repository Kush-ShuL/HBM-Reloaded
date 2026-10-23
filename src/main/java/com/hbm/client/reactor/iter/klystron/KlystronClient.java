package com.hbm.client.reactor.iter.klystron;
import com.hbm.machine.reactor.iter.klystron.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class KlystronClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{KlystronEntity.CLIENT_TICK=KlystronSound::tick;KlystronEntity.CLIENT_REMOVED=KlystronSound::removed;});}
    @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(KlystronContent.ENTITY.get(),KlystronRenderer::new);}@SubscribeEvent public static void screen(RegisterMenuScreensEvent event){event.register(KlystronContent.MENU.get(),KlystronScreen::new);}
    @SubscribeEvent public static void item(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private KlystronItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new KlystronItemRenderer()):renderer;}},KlystronContent.ITEM.get());}
    private KlystronClient(){}
}
