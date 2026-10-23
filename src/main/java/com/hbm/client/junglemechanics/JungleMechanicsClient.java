package com.hbm.client.junglemechanics;
import com.hbm.block.junglemechanics.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class JungleMechanicsClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:java.util.List.of(JungleMechanicsContent.TRAP_ITEM.get(),JungleMechanicsContent.GLYPH_ITEM.get()))ItemProperties.register(item,ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->JungleMetaItem.metadata(stack));});}
 private JungleMechanicsClient(){}
}
