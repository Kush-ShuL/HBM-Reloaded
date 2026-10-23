package com.hbm.client.strandcaster;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Source molten GUI pass: SRC_ALPHA/ONE, white alpha .3 over tinted opaque first pass. */
final class StrandCasterGuiTypes extends RenderType {
 private StrandCasterGuiTypes(){super("hbm_strand_gui",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 static final RenderType GLOW=create("hbm_strand_gui_glow",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,true,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/gui/processing/gui_strand_caster.png"),false,false)).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setTransparencyState(new TransparencyStateShard("hbm_strand_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();})).createCompositeState(false));
}
