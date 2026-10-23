package com.hbm.client.network.rtty.ror;
import com.hbm.client.network.rtty.RadioTorchBakedModel;
import com.hbm.network.rtty.ror.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadioRorClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RadioRorBlock.OPEN_SCREEN=radio->Minecraft.getInstance().setScreen(new RadioRorScreen(radio)));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{String path=id.id().getPath();if(!id.id().getNamespace().equals("hbm")||!(path.equals("radio_torch_reader")||path.equals("radio_torch_controller"))||!id.getVariant().startsWith("legacy_meta="))return model;var sprite=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/rtty_"+(path.equals("radio_torch_reader")?"reader":"controller"))));return new RadioTorchBakedModel(model,"rtty",Integer.parseInt(id.getVariant().substring(12)),ignored->sprite,entity->false);});}
}
