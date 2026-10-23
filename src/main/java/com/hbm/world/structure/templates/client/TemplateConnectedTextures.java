package com.hbm.world.structure.templates.client;
import com.hbm.machine.refinery.deco.DecoSteelModel;
import java.util.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Same-block source IBlockCT connectivity; no invented cross-material glass/beryllium links. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TemplateConnectedTextures {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var cache=new HashMap<String,BakedModel>();event.getModels().replaceAll((id,model)->{
        String name=id.id().getPath();if(!id.id().getNamespace().equals("hbm")||!Set.of("reinforced_glass","deco_beryllium").contains(name)||id.getVariant().equals("inventory"))return model;
        return cache.computeIfAbsent(name,ignored->DecoSteelModel.wrap(model,event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+name))),event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+name+"_ct"))),(state,other)->state.getBlock()==other.getBlock()));
    });}
    private TemplateConnectedTextures(){}
}
