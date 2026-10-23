package com.hbm.client.chemicalplant;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;

/** Source fluid disables lighting/depth writes, enables ordinary separate alpha blending. */
final class ChemicalPlantRenderTypes extends RenderType {
    private ChemicalPlantRenderTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private record Tint(int red,int green,int blue,int count){}
    private static final Map<Tint,RenderType> TYPES=new HashMap<>();
    static RenderType fluid(int red,int green,int blue,int count){return TYPES.computeIfAbsent(new Tint(red,green,blue,count),tint->create("hbm_chemical_plant_fluid_"+tint,DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new TextureStateShard(ChemicalPlantRenderer.FLUID,false,false))
            // Original per-draw float RGB average and alpha .5 must not be
            // quantized to a vertex color's eight-bit alpha/RGB beforehand.
            .setTransparencyState(new TransparencyStateShard("hbm_chemical_fluid_tint",()->{RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShaderColor(tint.red/255F/tint.count,tint.green/255F/tint.count,tint.blue/255F/tint.count,.5F);},()->{RenderSystem.disableBlend();RenderSystem.setShaderColor(1,1,1,1);}))
            .setWriteMaskState(COLOR_WRITE).createCompositeState(false)));}
}
