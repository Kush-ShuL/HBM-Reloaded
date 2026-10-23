package com.hbm.client.oilliquefactor;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderType;
/** Original GL_TEXTURE_2D/LIGHTING disabled,opaque liquid and15% glass with source separate alpha and no depth writes. */
final class LiquefactorRenderTypes extends RenderType {
 private LiquefactorRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 private static final TransparencyStateShard GLASS_BLEND=new TransparencyStateShard("hbm_liquefactor_glass_blend",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
 static final RenderType FLUID=create("hbm_liquefactor_unlit_fluid",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
 static final RenderType GLASS=create("hbm_liquefactor_unlit_glass",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,true,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(GLASS_BLEND).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
 /** Preserve the shared HFR exact face/winding emitter while intentionally omitting disabled GL texture/normal/light attributes. */
 static VertexConsumer colorOnly(VertexConsumer out){return new VertexConsumer(){public VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){out.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float u,float v){return this;}public VertexConsumer setUv1(int u,int v){return this;}public VertexConsumer setUv2(int u,int v){return this;}public VertexConsumer setNormal(float x,float y,float z){return this;}};}
}
