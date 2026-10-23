package com.hbm.client.strandcaster;
import com.hbm.machine.strandcaster.StrandCasterContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class StrandCasterClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(StrandCasterContent.ENTITY.get(),StrandCasterRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(StrandCasterContent.MENU.get(),StrandCasterScreen::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private StrandCasterItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new StrandCasterItemRenderer()):renderer;}},StrandCasterContent.ITEM.get());}
}
