package com.hbm.client.constructionwand;

import com.hbm.item.constructionwand.ConstructionWandContent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConstructionWandClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemBlockRenderTypes.setRenderLayer(ConstructionWandContent.AIR_MARKER.get(),RenderType.cutout()));}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){var id=ResourceLocation.parse("hbm:wand_air");event.getModels().replaceAll((key,model)->key.id().equals(id)&&!key.getVariant().equals("inventory")?new WandAirModel(model):model);}
    private ConstructionWandClient(){}
}
