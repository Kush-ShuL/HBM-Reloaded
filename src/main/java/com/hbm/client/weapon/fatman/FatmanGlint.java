package com.hbm.client.weapon.fatman;
import com.hbm.client.model.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Original ItemRenderFatMan.renderBalefire: actual MiniNuke plus all3 unlit depthEQUAL source-color/ONE passes. */
final class FatmanGlint {
 private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(32768));
 static void render(PoseStack pose,int light,int overlay,float partial){var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/weapons/fatman.obj"));LegacyObjRenderer.renderOnly(model,pose,BUFFERS.getBuffer(RenderType.entityCutout(FatmanModels.fatman_balefire_tex)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,"MiniNuke");BUFFERS.endBatch();var player=Minecraft.getInstance().player;float age=(player==null?0:player.tickCount)+partial;for(int layer=0;layer<3;layer++){float movement=age*(.001F+layer*.003F)*-6,angle=(float)Math.toRadians(30-layer*60),cos=(float)Math.cos(angle),sin=(float)Math.sin(angle);var out=BUFFERS.getBuffer(GlintType.GLINT);LegacyObjRenderer.renderOnly(model,pose,uv(out,cos,sin,movement),LightTexture.FULL_BRIGHT,overlay,-1,LegacyObjRenderer.Convention.HFR,"MiniNuke");BUFFERS.endBatch(GlintType.GLINT);}}
 /** Keep exactly the shared emitter's authored winding/hand reflection and identical depth geometry. */
 private static VertexConsumer uv(VertexConsumer out,float cos,float sin,float movement){return new VertexConsumer(){
  public VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}
  public VertexConsumer setColor(int r,int g,int b,int a){out.setColor(0F,.8F*.76F,.15F*.76F,1F);return this;}
  public VertexConsumer setUv(float u,float v){out.setUv(2*(cos*u-sin*(v+movement)),2*(sin*u+cos*(v+movement)));return this;}
  public VertexConsumer setUv1(int u,int v){return this;}public VertexConsumer setUv2(int u,int v){return this;}public VertexConsumer setNormal(float x,float y,float z){return this;}
 };}
 private static final class GlintType extends RenderType {
  static final RenderType GLINT=create("hbm_fatman_bf_glint",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,32768,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/misc/glintbf.png"),false,false)).setTransparencyState(new TransparencyStateShard("hbm_fatman_bf_source",()->{RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();})).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
  private GlintType(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 }
 private FatmanGlint(){}
}
