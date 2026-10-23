package com.hbm.client.marshmallow;

import com.hbm.item.marshmallow.ItemMarshmallow;
import com.hbm.item.marshmallow.MarshmallowContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class MarshmallowClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(MarshmallowContent.MARSHMALLOW.get(), ResourceLocation.parse("hbm:marshmallow_roasted"),
                (stack, level, owner, seed) -> ItemMarshmallow.metadata(stack) == 1 ? 1 : 0));
    }
    private MarshmallowClient() { }
}
