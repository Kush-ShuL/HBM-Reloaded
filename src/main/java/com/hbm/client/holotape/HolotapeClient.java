package com.hbm.client.holotape;
import com.hbm.LegacyStartup;import com.hbm.item.holotape.*;import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.item.ItemProperties;import net.minecraft.resources.ResourceLocation;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HolotapeClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{HolotapeImageItem.OPEN=hand->Minecraft.getInstance().setScreen(new HolotapeScreen(hand));ItemProperties.register(HolotapeContent.POLAROID.get(),ResourceLocation.parse("hbm:polaroid_fate"),(stack,level,entity,seed)->LegacyStartup.polaroidId/18F);});}
 private HolotapeClient(){}
}
