package com.hbm.client.reactor.dfc;
import com.mojang.blaze3d.vertex.*;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderType;
/** Source untextured sphere, depth-writing additive shells, non-depth-writing flare and two exact pixel line widths. */
final class DfcCoreRenderTypes extends RenderType {
    private DfcCoreRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType SOLID=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_dfc_core_solid",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false)),"DFC/Core");
    static final RenderType GLOW=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_dfc_core_glow",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setCullState(CULL).setTransparencyState(LIGHTNING_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false)),"DFC/Glow");
    static final RenderType FLARE=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_dfc_core_flare",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.TRIANGLES,32768,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setCullState(CULL).setTransparencyState(LIGHTNING_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"DFC/Flare");
    static final RenderType WIDE=line(5),NARROW=line(2);
    private static RenderType line(int pixels){return com.hbm.client.render.WorldEffectMeshes.register(create("hbm_dfc_core_spark_"+pixels,DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(OptionalDouble.of(pixels))).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false)),"DFC/Width5+2Sparks");}
}
