package com.hbm.client.entity.ghost;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Source blend separate770/771/1/0, alpha GREATER0, lit/depth-writing ghost. No fullbright/Eyes or alpha.1 substitution. */
public final class GhostRenderTypes extends RenderType {
    static ShaderInstance shader;
    private static final TransparencyStateShard SOURCE_ALPHA=new TransparencyStateShard("ghost_source_alpha",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    private static final RenderType BODY=WorldEffectMeshes.register(create("hbm_ghost",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->shader)).setTextureState(new TextureStateShard(GhostRenderer.TEXTURE,false,false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setCullState(NO_CULL).setTransparencyState(SOURCE_ALPHA).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(true)),"Ghost225/source translucent biped, alpha>0");
    public static RenderType body(ResourceLocation ignored){return BODY;}
    private GhostRenderTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
