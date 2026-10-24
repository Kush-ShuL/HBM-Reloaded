package com.hbm.client.glyphidhive;
import com.hbm.world.glyphidhive.GlyphidHiveMaterialContent;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GlyphidHiveMaterialClient {
    private static ModelResourceLocation cube(int family,int icon){return ModelResourceLocation.standalone(ResourceLocation.parse("hbm:block/glyphid_base_82_"+family+"_"+icon));}
    @SubscribeEvent public static void additional(ModelEvent.RegisterAdditional event){for(int family=0;family<3;family++)for(int icon=0;icon<2;icon++)event.register(cube(family,icon));}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){
        var models=new BakedModel[3][2];for(int family=0;family<3;family++)for(int icon=0;icon<2;icon++)models[family][icon]=java.util.Objects.requireNonNull(event.getModels().get(cube(family,icon)),"Missing original glyphid base cube");
        event.getModels().replaceAll((id,model)->{if(!id.id().equals(GlyphidHiveMaterialContent.BASE.getId())||!id.getVariant().startsWith("legacy_meta="))return model;int meta=Integer.parseInt(id.getVariant().substring(12));return new GlyphidHiveMaterialModel(model,models,meta);});
    }
    private GlyphidHiveMaterialClient(){}
}
