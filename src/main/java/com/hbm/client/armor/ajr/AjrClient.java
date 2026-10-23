package com.hbm.client.armor.ajr;
import com.hbm.armor.ajr.AjrContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
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
public final class AjrClient {
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final AjrItemRenderer renderer=new AjrItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},AjrContent.HELMET.get(),AjrContent.PLATE.get(),AjrContent.LEGS.get(),AjrContent.BOOTS.get(),AjrContent.O_HELMET.get(),AjrContent.O_PLATE.get(),AjrContent.O_LEGS.get(),AjrContent.O_BOOTS.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:java.util.List.of(AjrContent.HELMET,AjrContent.PLATE,AjrContent.LEGS,AjrContent.BOOTS,AjrContent.O_HELMET,AjrContent.O_PLATE,AjrContent.O_LEGS,AjrContent.O_BOOTS))T51ArmorRenderer.register(item.get(),AjrArmorRenderer::render);});}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(String family:new String[]{"ajr","ajro"})for(String part:new String[]{"helmet","plate","legs","boots"}){var key=ModelResourceLocation.inventory(AjrContent.id(family+"_"+part));var model=event.getModels().get(key);if(model!=null)event.getModels().put(key,new BakedModelWrapper<>(model){@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}});}}
 private AjrClient(){}
}
