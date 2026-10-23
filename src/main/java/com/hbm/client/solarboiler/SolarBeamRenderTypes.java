package com.hbm.client.solarboiler;
import com.mojang.blaze3d.platform.GlStateManager;import com.mojang.blaze3d.systems.RenderSystem;import com.mojang.blaze3d.vertex.*;import net.minecraft.client.renderer.*;
/** Original heliostat SRC_ALPHA/ONE,no-cull,LEQUAL,color-only depth mask, untextured smooth vertex alpha. */
final class SolarBeamRenderTypes extends RenderType {
 private SolarBeamRenderTypes(){super("hbm_solar_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,true,()->{},()->{});}
 private static final TransparencyStateShard ADDITIVE=new TransparencyStateShard("hbm_solar_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
 static final RenderType BEAM=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_solar_heliostat_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,262144,false,true,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(ADDITIVE).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"Solar/HeliostatBeam");
}
