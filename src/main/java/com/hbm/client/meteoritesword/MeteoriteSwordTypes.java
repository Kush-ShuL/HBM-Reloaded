package com.hbm.client.meteoritesword;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Source colored2pass glint: unlit, depthEQUAL, SRC_COLOR/ONE and source flat separate-alpha0/0. */
final class MeteoriteSwordTypes extends RenderType {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/misc/glint.png");
 static final RenderType FLAT=type(true),SOLID=type(false);
 private static RenderType type(boolean flat){return create("hbm_meteor_sword_glint_"+(flat?"flat":"3d"),DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,32768,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(TEXTURE,false,false)).setTransparencyState(new TransparencyStateShard("src_color_one",()->{RenderSystem.enableBlend();if(flat)RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR,GlStateManager.DestFactor.ONE,GlStateManager.SourceFactor.ZERO,GlStateManager.DestFactor.ZERO);else RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();})).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));}
 private MeteoriteSwordTypes(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
