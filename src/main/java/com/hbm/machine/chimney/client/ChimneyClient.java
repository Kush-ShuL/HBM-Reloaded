package com.hbm.machine.chimney.client;
import com.hbm.machine.chimney.ChimneyContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChimneyClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ChimneyContent.ENTITY.get(),ChimneyRenderer::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ChimneyItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new ChimneyItemRenderer(false)):renderer;}},ChimneyContent.BRICK_ITEM.get());event.registerItem(new IClientItemExtensions(){private ChimneyItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new ChimneyItemRenderer(true)):renderer;}},ChimneyContent.INDUSTRIAL_ITEM.get());}
}
