package com.hbm.client.network.rtty.counter;
import com.hbm.client.network.rtty.RadioTorchBakedModel;
import com.hbm.network.rtty.counter.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadioCounterClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RadioCounterContent.MENU.get(),RadioCounterScreen::new);}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var sprite=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/rtty_counter")));event.getModels().replaceAll((id,model)->id.id().getNamespace().equals("hbm")&&id.id().getPath().equals("radio_torch_counter")&&id.getVariant().startsWith("legacy_meta=")?new RadioTorchBakedModel(model,"rtty_counter",Integer.parseInt(id.getVariant().substring(12)),ignored->sprite,entity->false):model);}
}
