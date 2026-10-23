package com.hbm.client.reactor.dfc.components;
import com.hbm.machine.reactor.dfc.components.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DfcComponentClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){for(var kind:DfcComponentKind.values())event.register(DfcComponentsContent.menu(kind),DfcComponentScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){for(var kind:DfcComponentKind.values())register(event,kind);}
 @SuppressWarnings({"unchecked","rawtypes"})private static void register(EntityRenderersEvent.RegisterRenderers event,DfcComponentKind kind){event.registerBlockEntityRenderer((net.minecraft.world.level.block.entity.BlockEntityType)DfcComponentsContent.entity(kind),context->new DfcComponentRenderer(context));}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){for(var kind:DfcComponentKind.values())event.registerItem(new IClientItemExtensions(){private DfcComponentItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new DfcComponentItemRenderer(kind)):renderer;}},DfcComponentsContent.item(kind));}
 @SubscribeEvent public static void buffers(RegisterRenderBuffersEvent event){event.registerRenderBuffer(DfcComponentBeams.SOLID);event.registerRenderBuffer(DfcComponentBeams.LINES);}
 private DfcComponentClient(){}
}
