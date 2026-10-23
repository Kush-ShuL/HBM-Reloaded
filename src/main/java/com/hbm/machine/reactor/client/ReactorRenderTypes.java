package com.hbm.machine.reactor.client;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
final class ReactorRenderTypes extends RenderType {
    private ReactorRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType SPARK_OUTER=spark(5),SPARK_INNER=spark(2);
    private static RenderType spark(int width){return create("hbm_reactor_spark_"+width,DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,1536,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(java.util.OptionalDouble.of(width))).setCullState(NO_CULL).createCompositeState(false));}
    static final RenderType CHERENKOV=create("hbm_research_cherenkov",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,true,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
}
