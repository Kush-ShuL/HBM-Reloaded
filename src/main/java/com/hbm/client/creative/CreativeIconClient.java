package com.hbm.client.creative;

import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmCreativeTabs;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CreativeIconClient {
    private CreativeIconClient() {}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(()->ItemProperties.register(HbmCreativeTabs.UI_ICON.get(),ResourceLocation.fromNamespaceAndPath("hbm","legacy_metadata"),(stack,level,entity,seed)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0))); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) { event.registerItem(new IClientItemExtensions() {
        private CreativeIconRenderer renderer;
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if(renderer==null) renderer=new CreativeIconRenderer(); return renderer; }
    },HbmCreativeTabs.UI_ICON.get()); }
}
