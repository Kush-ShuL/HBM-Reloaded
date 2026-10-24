package com.hbm.block.ore;

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

    }
}
