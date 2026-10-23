package com.hbm.client.oilextraction;
import com.mojang.blaze3d.vertex.*;import net.minecraft.client.renderer.RenderType;
/** Original untextured/unlit no-cull moving rods and cable strips, opaque gray source RGBA. */
final class OilDrillRenderTypes extends RenderType {
 private OilDrillRenderTypes(){super("hbm_pumpjack_rods",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,()->{},()->{});}
 static final RenderType RODS=create("hbm_pumpjack_rods",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).createCompositeState(false));
}
