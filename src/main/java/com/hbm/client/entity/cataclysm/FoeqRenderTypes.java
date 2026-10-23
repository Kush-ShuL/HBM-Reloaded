package com.hbm.client.entity.cataclysm;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** Source untextured, lit OBJ fire: additive and still depth-writing, as RenderFOEQ actually leaves depthMask enabled. */
final class FoeqRenderTypes extends RenderType {
    private static final TransparencyStateShard ADDITIVE=new TransparencyStateShard("hbm_foeq_source_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
    static final RenderType FIRE=WorldEffectMeshes.register(create("hbm_foeq_source_fire",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(ResourceLocation.withDefaultNamespace("textures/misc/white.png"),false,false)).setTransparencyState(ADDITIVE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"RenderFOEQ original untextured additive/lit/depth-writing OBJ fire");
    private FoeqRenderTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
