package com.hbm.client.bomb.conventional.grenade;

import com.hbm.bomb.conventional.grenade.GrenadeContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GrenadeClient {
    private GrenadeClient(){}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(GrenadeContent.UNIVERSAL_ENTITY.get(),UniversalGrenadeRenderer::new);event.registerEntityRenderer(GrenadeContent.GENERIC.get(),GenericGrenadeRenderer::new);event.registerEntityRenderer(GrenadeContent.LINGERING.get(),net.minecraft.client.renderer.entity.NoopRenderer::new);}
    @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private UniversalGrenadeItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new UniversalGrenadeItemRenderer();return renderer;}},GrenadeContent.UNIVERSAL.get());}
    @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:GrenadeContent.components())net.minecraft.client.renderer.item.ItemProperties.register(item,net.minecraft.resources.ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->((com.hbm.item.EnumMultiItem)item).ordinal(stack));});}
}
