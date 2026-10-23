package com.hbm.client.reactor.iter.torus;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
/** Source SmallBlockPronter alpha.75, real culled6faces, no depthwrites. */
final class TorusPreviewType extends RenderType {
    private TorusPreviewType(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType PREVIEW=create("hbm_torus_structure_preview",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,32768,false,true,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader)).setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS,false,false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setCullState(CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
}
