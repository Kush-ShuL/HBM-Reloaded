package com.hbm.client.reactor.iter.auxiliary;
import com.hbm.machine.reactor.iter.auxiliary.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FusionAuxiliaryClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FusionAuxiliaryContent.COLLECTOR_ENTITY.get(),ctx->new FusionAuxiliaryRenderer<>(ctx,true));event.registerBlockEntityRenderer(FusionAuxiliaryContent.COUPLER_ENTITY.get(),ctx->new FusionAuxiliaryRenderer<>(ctx,false));}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){for(boolean collector:new boolean[]{true,false})event.registerItem(new IClientItemExtensions(){private FusionAuxiliaryItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new FusionAuxiliaryItemRenderer(collector)):renderer;}},(collector?FusionAuxiliaryContent.COLLECTOR_ITEM:FusionAuxiliaryContent.COUPLER_ITEM).get());}
 private FusionAuxiliaryClient(){}
}
