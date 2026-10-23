package com.hbm.client.starcomponents;
import com.hbm.item.starcomponents.StarComponentsContent;import net.minecraft.client.renderer.item.ItemProperties;import net.minecraft.resources.ResourceLocation;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
/** Original ItemEnumMulti name-derived textures and exact metadata modulo route. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class StarComponentsClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(StarComponentsContent.ITEM.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->StarComponentsContent.ITEM.get().ordinal(stack)));}
 private StarComponentsClient(){}
}
