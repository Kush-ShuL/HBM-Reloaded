package com.hbm.client.plasmaforge;

import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Source unlit additive passes with alpha>0, no depth writes, repeating original UVs. */
final class PlasmaForgeRenderTypes extends RenderType {
    private PlasmaForgeRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final Map<ResourceLocation,RenderType> UNLIT=new HashMap<>(),ADDITIVE=new HashMap<>(),GLOW=new HashMap<>();
    static RenderType unlit(ResourceLocation texture){return UNLIT.computeIfAbsent(texture,id->com.hbm.client.render.WorldEffectMeshes.register(create("hbm_forge_unlit",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->PlasmaForgeClient.unlit)).setTextureState(new TextureStateShard(id,false,false)).setCullState(CULL).createCompositeState(false)),"PlasmaForge/OpaqueSourcePlasma"));}
    static RenderType additive(ResourceLocation texture,boolean glow){return(glow?GLOW:ADDITIVE).computeIfAbsent(texture,id->com.hbm.client.render.WorldEffectMeshes.register(create(glow?"hbm_forge_glow":"hbm_forge_additive",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->glow?PlasmaForgeClient.glow:PlasmaForgeClient.additive)).setTextureState(new TextureStateShard(id,false,false)).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"PlasmaForge/Additive+Glow"));}
    static final RenderType JET=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_forge_jet",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"PlasmaForge/Jet");
    static VertexConsumer uv(VertexConsumer output,float uShift,float vShift){return new VertexConsumer(){public VertexConsumer addVertex(float x,float y,float z){output.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){output.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float u,float v){output.setUv(u+uShift,v+vShift);return this;}public VertexConsumer setUv1(int u,int v){return this;}public VertexConsumer setUv2(int u,int v){return this;}public VertexConsumer setNormal(float x,float y,float z){return this;}};}
}
