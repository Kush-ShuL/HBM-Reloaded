package com.hbm.client.generator.combustion;

import com.hbm.machine.generator.combustion.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CombustionClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{CombustionBlockEntity.CLIENT_TICK=CombustionSound::tick;CombustionBlockEntity.CLIENT_REMOVED=CombustionSound::removed;ItemProperties.register(CombustionContent.PISTONS.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,world,owner,seed)->PistonSetItem.metadata(stack));});}
    @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(CombustionContent.ENTITY.get(),CombustionRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CombustionContent.MENU.get(),CombustionScreen::new);}
    @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private CombustionItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new CombustionItemRenderer()):renderer;}},CombustionContent.ITEM.get());}
    private CombustionClient(){}
}
