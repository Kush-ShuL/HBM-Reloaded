package com.hbm.client.lambda;
import com.hbm.entity.rocket.EntityRocketLambda;
import com.hbm.machine.lambda.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LambdaClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(LambdaContent.ENTITY.get(),LambdaRenderer::new);event.registerEntityRenderer(LambdaContent.ROCKET.get(),LambdaRocketRenderer::new);LambdaEntity.CLIENT_TICK=LambdaEffects::tick;LambdaEntity.CLIENT_REMOVE=LambdaEffects::removed;EntityRocketLambda.CLIENT_TICK=LambdaEffects::rocketTick;}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(LambdaContent.MENU.get(),LambdaScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final BlockEntityWithoutLevelRenderer renderer=new LambdaItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},LambdaContent.ITEM.get());}
    private LambdaClient(){}
}
