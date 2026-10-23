package com.hbm.machine.foundry.crucible.client;
import com.hbm.machine.foundry.crucible.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ThermalClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CrucibleContent.CRUCIBLE_MENU.get(),ThermalScreen::new);event.register(CrucibleContent.FIREBOX_MENU.get(),ThermalScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(CrucibleContent.CRUCIBLE_ENTITY.get(),context->new ThermalRenderer<CrucibleBlockEntity>(false));event.registerBlockEntityRenderer(CrucibleContent.FIREBOX_ENTITY.get(),context->new ThermalRenderer<FireboxBlockEntity>(true));}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ThermalItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new ThermalItemRenderer(false)):renderer;}},CrucibleContent.CRUCIBLE_ITEM.get());event.registerItem(new IClientItemExtensions(){private ThermalItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new ThermalItemRenderer(true)):renderer;}},CrucibleContent.FIREBOX_ITEM.get());}
}
