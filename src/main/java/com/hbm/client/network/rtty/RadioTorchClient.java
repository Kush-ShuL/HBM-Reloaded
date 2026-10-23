package com.hbm.client.network.rtty;
import com.hbm.network.rtty.RadioTorchBlock;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadioTorchClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RadioTorchBlock.OPEN_SCREEN=radio->Minecraft.getInstance().setScreen(new RadioTorchScreen(radio)));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{String path=id.id().getPath(),variant=id.getVariant();if(!id.id().getNamespace().equals("hbm")||!(path.equals("radio_torch_sender")||path.equals("radio_torch_receiver"))||!variant.startsWith("legacy_meta="))return model;return new RadioTorchBakedModel(model,path.equals("radio_torch_sender"),Integer.parseInt(variant.substring(12)),event.getTextureGetter());});}
}
