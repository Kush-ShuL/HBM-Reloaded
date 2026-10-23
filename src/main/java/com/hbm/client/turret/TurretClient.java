package com.hbm.client.turret;
import com.hbm.machine.turret.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TurretClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.<TurretMenu,SentryScreen<TurretMenu>>register(TurretContent.MENU.get(),(menu,inventory,title)->new SentryScreen<TurretMenu>(menu,inventory,title));}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(TurretContent.SENTRY_ENTITY.get(),SentryRenderer::new);event.registerBlockEntityRenderer(TurretContent.DAMAGED_ENTITY.get(),SentryRenderer::new);}
    @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private SentryItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new SentryItemRenderer()):renderer;}},TurretContent.SENTRY_ITEM.get());}
    @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event){event.enqueueWork(()->TurretEffects.client=TurretClientEffects::receive);}
}
