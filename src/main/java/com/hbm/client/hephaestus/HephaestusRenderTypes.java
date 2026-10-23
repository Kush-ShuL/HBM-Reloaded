package com.hbm.client.hephaestus;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Original opaque/cull/disabled-lighting Core pass, with normal world depth and fog. */
final class HephaestusRenderTypes extends RenderType {
    private HephaestusRenderTypes(){super("unused",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final Map<ResourceLocation,RenderType> TYPES=new HashMap<>();
    static RenderType unlit(ResourceLocation texture){return TYPES.computeIfAbsent(texture,id->create("hbm_hephaestus_core",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(id,false,false)).setLightmapState(LIGHTMAP).setCullState(CULL).createCompositeState(false)));}
    static VertexConsumer unlitVertices(VertexConsumer out){return new VertexConsumer(){
        public VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}
        public VertexConsumer setColor(int r,int g,int b,int a){out.setColor(r,g,b,a);return this;}
        public VertexConsumer setUv(float u,float v){out.setUv(u,v);return this;}
        public VertexConsumer setUv1(int u,int v){return this;}
        public VertexConsumer setUv2(int u,int v){out.setUv2(u,v);return this;}
        public VertexConsumer setNormal(float x,float y,float z){return this;}
    };}
}
