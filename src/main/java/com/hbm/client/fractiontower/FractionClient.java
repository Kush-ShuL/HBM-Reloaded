package com.hbm.client.fractiontower;
import com.hbm.machine.fractiontower.FractionContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FractionClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FractionContent.ENTITY.get(),ctx->new FractionRenderer<>(false));event.registerBlockEntityRenderer(FractionContent.SPACER_ENTITY.get(),ctx->new FractionRenderer<>(true));}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){for(boolean spacer:new boolean[]{false,true})event.registerItem(new IClientItemExtensions(){private FractionItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new FractionItemRenderer(spacer)):renderer;}},spacer?FractionContent.SPACER_ITEM.get():FractionContent.ITEM.get());}
}
