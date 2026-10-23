package com.hbm.client.electrolyser;

import com.hbm.machine.electrolyser.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ElectrolyserClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ElectrolyserContent.FLUID_MENU.get(),ElectrolyserScreen::new);event.register(ElectrolyserContent.METAL_MENU.get(),ElectrolyserScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ElectrolyserContent.ENTITY.get(),ElectrolyserRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ElectrolyserItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ElectrolyserItemRenderer();return renderer;}},ElectrolyserContent.ITEM.get());}
}
