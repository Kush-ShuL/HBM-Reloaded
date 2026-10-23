package com.hbm.client.disease;
import com.hbm.disease.DiseaseVisuals;import net.minecraft.ChatFormatting;import net.minecraft.client.Minecraft;import net.minecraft.network.chat.Component;import net.minecraft.world.entity.LivingEntity;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DiseaseClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->DiseaseVisuals.CLIENT=DiseaseClient::receive);}
 private static void receive(DiseaseVisuals.Effect effect){if(effect.mode()==DiseaseVisuals.ASBESTOS_WARNING||effect.mode()==DiseaseVisuals.COAL_WARNING){com.hbm.item.settings.client.LegacyInfoSystem.push(Component.translatable(effect.mode()==DiseaseVisuals.ASBESTOS_WARNING?"info.asbestos":"info.coaldust").withStyle(ChatFormatting.RED),3000,12);return;}var level=Minecraft.getInstance().level;if(level!=null&&level.getEntity(effect.entity()) instanceof LivingEntity entity)com.hbm.client.medicine.MedicineClient.sourceVomit(entity,effect.mode(),effect.count());}
 private DiseaseClient(){}
}
