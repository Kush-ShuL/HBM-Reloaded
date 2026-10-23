package com.hbm.client.vacuumdistill;

import com.hbm.machine.vacuumdistill.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class VacuumDistillClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{VacuumDistillBlockEntity.CLIENT_TICK=VacuumDistillSound::tick;VacuumDistillBlockEntity.CLIENT_REMOVED=VacuumDistillSound::removed;});}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(VacuumDistillContent.MENU.get(),VacuumDistillScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(VacuumDistillContent.ENTITY.get(),VacuumDistillRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private VacuumDistillItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new VacuumDistillItemRenderer();return renderer;}},VacuumDistillContent.ITEM.get());}
}
