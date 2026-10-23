package com.hbm.client.reactor.rbmk.crane;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** Genuine source no-texture/no-light lamp geometry, preserving original no-cull pass. */
final class RbmkCraneRenderTypes extends RenderType {
    private RbmkCraneRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType LAMPS=create("hbm_rbmk_crane_lamps",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
}
