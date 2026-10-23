package com.hbm.client.armor.bismuth;

import com.hbm.armor.bismuth.BismuthContent;
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
public final class BismuthClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final BismuthItemRenderer renderer=new BismuthItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},BismuthContent.HELMET.get(),BismuthContent.PLATE.get(),BismuthContent.LEGS.get(),BismuthContent.BOOTS.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:java.util.List.of(BismuthContent.HELMET,BismuthContent.PLATE,BismuthContent.LEGS,BismuthContent.BOOTS))T51ArmorRenderer.register(item.get(),BismuthArmorRenderer::render);});}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(String part:new String[]{"helmet","plate","legs","boots"}){var key=ModelResourceLocation.inventory(BismuthContent.id("bismuth_"+part));var original=event.getModels().get(key);if(original!=null)event.getModels().put(key,new BakedModelWrapper<>(original){@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}});}}
    private BismuthClient(){}
}
