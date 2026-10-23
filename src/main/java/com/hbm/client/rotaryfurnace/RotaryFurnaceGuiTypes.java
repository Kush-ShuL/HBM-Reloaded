package com.hbm.client.rotaryfurnace;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Source molten GUI pass: SRC_ALPHA/ONE, white alpha .3 over tinted opaque first pass. */
final class RotaryFurnaceGuiTypes extends RenderType {
 private RotaryFurnaceGuiTypes(){super("hbm_rotary_gui",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 static final RenderType GLOW=create("hbm_rotary_gui_glow",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,true,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/gui/processing/gui_rotary_furnace.png"),false,false)).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setTransparencyState(new TransparencyStateShard("hbm_rotary_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();})).createCompositeState(false));
}
