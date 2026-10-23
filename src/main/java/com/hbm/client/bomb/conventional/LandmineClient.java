package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.LandmineContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LandmineClient {
    private LandmineClient(){}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(LandmineContent.ENTITY.get(),LandmineRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private LandmineItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new LandmineItemRenderer();return renderer;}},LandmineContent.AP_ITEM.get(),LandmineContent.HE_ITEM.get(),LandmineContent.SHRAP_ITEM.get(),LandmineContent.NAVAL_ITEM.get(),LandmineContent.FAT_ITEM.get());}
}
