package com.hbm.client.arcfurnace;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
final class ArcFurnaceRenderTypes extends RenderType {
 private ArcFurnaceRenderTypes(){super("unused",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 private static final Map<ResourceLocation,RenderType> TYPES=new HashMap<>();
 static RenderType unlit(ResourceLocation texture){return TYPES.computeIfAbsent(texture,id->create("hbm_arc_furnace_unlit",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(id,false,false)).setLightmapState(LIGHTMAP).setCullState(NO_CULL).createCompositeState(false)));}
 /** Source disabled lighting: no directional normals or overlay enter the particle shader. */
 static VertexConsumer unlitVertices(VertexConsumer delegate){return new VertexConsumer(){public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float u,float v){delegate.setUv(u,v);return this;}public VertexConsumer setUv1(int u,int v){return this;}public VertexConsumer setUv2(int u,int v){delegate.setUv2(u,v);return this;}public VertexConsumer setNormal(float x,float y,float z){return this;}};}
}
