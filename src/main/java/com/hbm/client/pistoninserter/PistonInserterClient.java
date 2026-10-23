package com.hbm.client.pistoninserter;

import com.hbm.machine.pistoninserter.PistonInserterContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;

/** Original automatic IItemRendererProvider path is actually bound, despite no explicit ItemRenderLibrary row. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PistonInserterClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(PistonInserterContent.ENTITY.get(),PistonInserterRenderer::new);}
    @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PistonInserterItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new PistonInserterItemRenderer()):renderer;}},PistonInserterContent.ITEM.get());}
    private PistonInserterClient(){}
}
