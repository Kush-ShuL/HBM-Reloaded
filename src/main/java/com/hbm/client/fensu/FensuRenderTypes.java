package com.hbm.client.fensu;

import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Independent original unlit fullbright/model additive passes; no shared smoke/cloud renderer changes. */
public final class FensuRenderTypes extends RenderType {
    private FensuRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final Map<ResourceLocation,RenderType> UNLIT=new HashMap<>(),PLASMA=new HashMap<>();
    public static RenderType unlit(ResourceLocation texture){return UNLIT.computeIfAbsent(texture,id->com.hbm.client.render.WorldEffectMeshes.register(create("hbm_fensu_unlit",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->FensuClient.unlit)).setTextureState(new TextureStateShard(id,false,false)).setCullState(NO_CULL).createCompositeState(false)),"FEnSU/SourceLightsOnly"));}
    public static RenderType plasma(ResourceLocation texture){return PLASMA.computeIfAbsent(texture,id->com.hbm.client.render.WorldEffectMeshes.register(create("hbm_fensu_plasma",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->FensuClient.plasma)).setTextureState(new TextureStateShard(id,false,false)).setTransparencyState(LIGHTNING_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"FEnSU/Plasma"));}
    static final RenderType ARC=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_fensu_arc",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"FEnSU/Arc+Trails");
    public static VertexConsumer unlitVertices(VertexConsumer output,float uShift,float vShift){return new VertexConsumer(){public VertexConsumer addVertex(float x,float y,float z){output.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){output.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float u,float v){output.setUv(u+uShift,v+vShift);return this;}public VertexConsumer setUv1(int u,int v){return this;}public VertexConsumer setUv2(int u,int v){return this;}public VertexConsumer setNormal(float x,float y,float z){return this;}};}
}
