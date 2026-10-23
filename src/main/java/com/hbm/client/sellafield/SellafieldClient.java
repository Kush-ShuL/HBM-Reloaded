package com.hbm.client.sellafield;
import java.util.Objects;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SellafieldClient {
    private static ModelResourceLocation model(int level,int variant){return ModelResourceLocation.standalone(ResourceLocation.parse("hbm:block/natural_sellafield_"+level+"_"+variant));}
    @SubscribeEvent public static void additional(ModelEvent.RegisterAdditional event){for(int l=0;l<6;l++)for(int v=0;v<4;v++)event.register(model(l,v));}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){BakedModel[][] models=new BakedModel[6][4];for(int l=0;l<6;l++)for(int v=0;v<4;v++)models[l][v]=Objects.requireNonNull(event.getModels().get(model(l,v)),"Missing source Sellafield model");event.getModels().replaceAll((id,original)->{if(!id.id().equals(ResourceLocation.parse("hbm:sellafield")))return original;if(id.getVariant().equals("inventory"))return new SellafieldItemModel(original,models);int metadata=Integer.parseInt(id.getVariant().substring("legacy_meta=".length()));return new ActiveSellafieldModel(original,models[metadata%6]);});}
}
