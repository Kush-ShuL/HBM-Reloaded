package com.hbm.block.ore;

import com.hbm.registry.HbmOres;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class OreItemModels {
    private OreItemModels() { }
    @SubscribeEvent public static void sprites(RegisterSpriteSourceTypesEvent event){AlexandriteSprites.TYPE=event.register(ResourceLocation.fromNamespaceAndPath("hbm","alexandrite"),AlexandriteSprites.CODEC);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(AlexandriteSprites::tick);
        NeoForge.EVENT_BUS.addListener(AlexandriteSprites::frame);
        event.enqueueWork(() -> {
            ItemProperties.register(HbmOres.DRILLBIT.get(), ResourceLocation.fromNamespaceAndPath("hbm", "legacy_metadata"), (stack, level, entity, seed) -> HbmOres.DRILLBIT.get().ordinal(stack));
            HbmOres.allItems().forEach(holder -> {
            if (holder.get() instanceof LegacyOreItem item && item.spec().multi())
                ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath("hbm", "legacy_metadata"), (stack, level, entity, seed) -> item.ordinal(stack));
            });
        });
    }
}
