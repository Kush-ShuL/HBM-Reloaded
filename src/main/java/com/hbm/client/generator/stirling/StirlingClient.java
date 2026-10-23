package com.hbm.client.generator.stirling;
import com.hbm.machine.generator.stirling.*;import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class StirlingClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(StirlingContent.ENTITY.get(),StirlingRenderer::new);event.registerEntityRenderer(StirlingContent.COG.get(),StrayCogRenderer::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){var extension=new IClientItemExtensions(){private StirlingItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new StirlingItemRenderer()):renderer;}};for(var item:StirlingContent.ITEM.values())event.registerItem(extension,item.get());}
}
