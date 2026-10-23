package com.hbm.client.entity.glyphid;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
/** Only the source infected overlay has blending with alpha testing completely disabled. */
final class GlyphidInfestationRenderType extends RenderType {
    static ShaderInstance shader;
    static final RenderType TYPE=create("hbm_glyphid_infestation",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->shader)).setTextureState(new TextureStateShard(GlyphidRenderer.INFESTED,false,false)).setCullState(NO_CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    private GlyphidInfestationRenderType(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
