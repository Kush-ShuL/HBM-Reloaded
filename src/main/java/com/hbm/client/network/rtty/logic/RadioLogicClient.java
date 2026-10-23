package com.hbm.client.network.rtty.logic;
import com.hbm.client.network.rtty.RadioTorchBakedModel;
import com.hbm.network.rtty.logic.*;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadioLogicClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RadioLogicBlock.OPEN_SCREEN=logic->Minecraft.getInstance().setScreen(new RadioLogicScreen(logic)));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().getNamespace().equals("hbm")&&id.id().getPath().equals("radio_torch_logic")&&id.getVariant().startsWith("legacy_meta=")?new RadioTorchBakedModel(model,"rtty_logic",Integer.parseInt(id.getVariant().substring(12)),event.getTextureGetter(),entity->entity instanceof RadioLogicEntity logic&&logic.lastState>0):model);}
}
