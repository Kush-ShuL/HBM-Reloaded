package com.hbm.client.bomb.nuclear.fallout;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;

/** Source loop order/no quad sorting, alpha770/771/1/0, no cull, ordinary scene depth and lightmap. */
final class FalloutRainRenderType extends RenderType {
    private FalloutRainRenderType(){super("unused",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final TransparencyStateShard ALPHA=new TransparencyStateShard("hbm_source_fallout_alpha",()->{
        RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
    },()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    static final RenderType RAIN=WorldEffectMeshes.register(create("hbm_source_fallout_rain",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,131072,false,false,
        CompositeState.builder().setShaderState(new ShaderStateShard(FalloutRainClient::shader))
        .setTextureState(new TextureStateShard(FalloutRainRenderer.TEXTURE,false,false)).setLightmapState(LIGHTMAP)
        .setTransparencyState(ALPHA).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"RenderFallout/EntityFalloutRain");
}
