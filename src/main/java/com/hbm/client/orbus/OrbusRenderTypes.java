package com.hbm.client.orbus;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** Original untextured, unlit opaque fluid sphere; beams reuse the shared source BeamPronter path. */
final class OrbusRenderTypes extends RenderType {
 static final RenderType SPHERE=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_orbus_sphere",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,65536,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"Orbus/OriginalFluidSphere");
 private OrbusRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
