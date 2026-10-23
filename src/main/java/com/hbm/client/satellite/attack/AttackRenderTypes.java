package com.hbm.client.satellite.attack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** Original untextured, unlit, cull-on beam/orb blend and depth masks. Geometry stays in original mesh/math kernels. */
final class AttackRenderTypes extends RenderType {
 private AttackRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,512,false,false,()->{},()->{});}
 private static final TransparencyStateShard ADD=new TransparencyStateShard("hbm_orbital_source_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
 private static final TransparencyStateShard ALPHA=new TransparencyStateShard("hbm_orbital_source_alpha",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
 static final RenderType BEAM=make("hbm_orbital_beam",ADD,false),ORB=make("hbm_death_orb",ALPHA,true),GLOW=make("hbm_death_orb_glow",ADD,true);
 private static RenderType make(String name,TransparencyStateShard blend,boolean depth){return com.hbm.client.render.WorldEffectMeshes.register(create(name,DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,1024,false,true,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(blend).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(depth?COLOR_DEPTH_WRITE:COLOR_WRITE).createCompositeState(false)),"DeathRay+Precision/Beam+Orb+Glow");}
 static VertexConsumer colorOnly(VertexConsumer out){return new VertexConsumer(){public VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){out.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float u,float v){return this;}public VertexConsumer setUv1(int u,int v){return this;}public VertexConsumer setUv2(int u,int v){return this;}public VertexConsumer setNormal(float x,float y,float z){return this;}};}
}
