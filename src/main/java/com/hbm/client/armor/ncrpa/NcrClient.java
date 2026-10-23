package com.hbm.client.armor.ncrpa;
import com.hbm.armor.ncrpa.*;
import com.hbm.client.armor.rpa.RpaMeleeRenderer;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.weapon.panzerschreck.PanzerschreckClient;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class NcrClient {
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final NcrItemRenderer renderer=new NcrItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},NcrContent.HELMET.get(),NcrContent.PLATE.get(),NcrContent.LEGS.get(),NcrContent.BOOTS.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
  for(var item:java.util.List.of(NcrContent.HELMET,NcrContent.PLATE,NcrContent.LEGS,NcrContent.BOOTS))T51ArmorRenderer.register(item.get(),NcrArmorRenderer::render);
  RpaMeleeRenderer.register(NcrMelee.INSTANCE,NcrMeleeArms::render);
  for(var configs:new com.hbm.weapon.sedna.BulletConfig[][]{NcrRocketBullets.PLAIN,NcrRocketBullets.STEER})for(var config:configs)config.setRenderer(PanzerschreckClient::rocket);
 });}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(String name:new String[]{"ncrpa_helmet","ncrpa_plate","ncrpa_legs","ncrpa_boots"}){var key=ModelResourceLocation.inventory(NcrContent.id(name));var model=event.getModels().get(key);if(model!=null)event.getModels().put(key,new BakedModelWrapper<>(model){@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}});}}
 private NcrClient(){}
}
