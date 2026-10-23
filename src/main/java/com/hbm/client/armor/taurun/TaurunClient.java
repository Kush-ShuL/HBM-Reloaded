package com.hbm.client.armor.taurun;

import com.hbm.armor.taurun.TaurunContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TaurunClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final TaurunItemRenderer renderer=new TaurunItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},TaurunContent.HELMET.get(),TaurunContent.PLATE.get(),TaurunContent.LEGS.get(),TaurunContent.BOOTS.get());}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(String id:new String[]{"taurun_helmet","taurun_plate","taurun_legs","taurun_boots"}){var key=ModelResourceLocation.inventory(TaurunContent.id(id));var model=event.getModels().get(key);if(model!=null)event.getModels().put(key,new BakedModelWrapper<BakedModel>(model){@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}});}}
    private TaurunClient(){}
}
