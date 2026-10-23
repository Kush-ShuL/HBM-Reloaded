package com.hbm.client.reactor.iter.utilities;
import com.hbm.machine.reactor.iter.utilities.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FusionUtilityClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FusionUtilityContent.BOILER_ENTITY.get(),ctx->new FusionUtilityRenderer<>(ctx,false));event.registerBlockEntityRenderer(FusionUtilityContent.CREATIVE_ENTITY.get(),ctx->new FusionUtilityRenderer<>(ctx,true));CreativeKlystronEntity.CLIENT_TICK=CreativeKlystronSound::tick;CreativeKlystronEntity.CLIENT_REMOVED=CreativeKlystronSound::removed;}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){for(boolean creative:new boolean[]{false,true})event.registerItem(new IClientItemExtensions(){private FusionUtilityItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new FusionUtilityItemRenderer(creative)):renderer;}},(creative?FusionUtilityContent.CREATIVE_ITEM:FusionUtilityContent.BOILER_ITEM).get());}
 private FusionUtilityClient(){}
}
