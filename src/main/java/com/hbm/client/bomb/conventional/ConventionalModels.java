package com.hbm.client.bomb.conventional;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConventionalModels {
    private ConventionalModels(){}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){
        var sprite=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/det_cord")));
        event.getModels().replaceAll((id,model)->{
            if(id.id().getNamespace().equals("hbm")){
                String texture=switch(id.id().getPath()){case "red_barrel"->"barrel_red";case "pink_barrel"->"barrel_pink";case "lox_barrel"->"barrel_lox";case "yellow_barrel"->"barrel_yellow";case "vitrified_barrel"->"barrel_vitrified";default->null;};
                if(texture!=null)return new com.hbm.client.barrel.BarrelBakedModel(model,id.getVariant().equals("inventory"),event.getTextureGetter(),ResourceLocation.fromNamespaceAndPath("hbm","block/"+texture));
            }
            if(id.id().equals(ResourceLocation.parse("hbm:det_cord")))return new DetCordModel(model,sprite);
            if(id.id().getNamespace().equals("hbm")&&(id.id().getPath().equals("charge_dynamite")||id.id().getPath().equals("charge_miner")||id.id().getPath().equals("charge_c4")||id.id().getPath().equals("charge_semtex")))return new TimedChargeModel(model,event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+id.id().getPath()))),id.id().getPath().equals("charge_c4")||id.id().getPath().equals("charge_semtex"),id.getVariant().equals("inventory"));
            return model;
        });
    }
}
