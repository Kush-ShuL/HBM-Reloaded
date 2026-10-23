package com.hbm.client.entity.nuclearfog;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** Source unlit texture*color, standard SRC_ALPHA blending, scene-depth test and no depth writes. */
final class NuclearFogRenderType extends RenderType {
    static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/particle/fog.png");
    private static final TransparencyStateShard SOURCE_ALPHA=new TransparencyStateShard("hbm_nuclear_fog_source_alpha",()->{
        RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
    },()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
    static final RenderType FOG=WorldEffectMeshes.register(create("hbm_source_nuclear_fog",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder()
        .setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(TEXTURE,false,false))
        .setTransparencyState(SOURCE_ALPHA).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"EntityFogFX/FogRenderer");
    private NuclearFogRenderType(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
