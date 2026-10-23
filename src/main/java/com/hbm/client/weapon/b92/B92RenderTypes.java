package com.hbm.client.weapon.b92;
import com.mojang.blaze3d.platform.GlStateManager;import com.mojang.blaze3d.systems.RenderSystem;import com.mojang.blaze3d.vertex.*;import net.minecraft.client.renderer.RenderType;
final class B92RenderTypes extends RenderType {
 private B92RenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,true,()->{},()->{});}
 private static final TransparencyStateShard ADDITIVE=new TransparencyStateShard("hbm_b92_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
 static final RenderType BEAM=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_b92_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,16384,false,true,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(ADDITIVE).setCullState(NO_CULL).createCompositeState(false)),"B92/EntityBeam");
}
