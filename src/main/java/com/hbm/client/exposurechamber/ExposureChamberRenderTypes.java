package com.hbm.client.exposurechamber;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** Original unlit/fullbright core writes depth without introducing additive blending. */
final class ExposureChamberRenderTypes extends RenderType {
 static final RenderType CORE=create("hbm_exposure_chamber_core",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new TextureStateShard(ExposureChamberRenderer.TEXTURE,false,false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
 private ExposureChamberRenderTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
