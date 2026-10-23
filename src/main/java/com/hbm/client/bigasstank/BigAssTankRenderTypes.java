package com.hbm.client.bigasstank;

import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

final class BigAssTankRenderTypes extends RenderType {
    private BigAssTankRenderTypes(){super("unused",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final Map<ResourceLocation,RenderType> TYPES=new HashMap<>();
    static RenderType fluid(ResourceLocation texture){return TYPES.computeIfAbsent(texture,id->create("hbm_big_ass_tank_fluid_"+id,DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(id,false,false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setCullState(NO_CULL).createCompositeState(false)));}
}
