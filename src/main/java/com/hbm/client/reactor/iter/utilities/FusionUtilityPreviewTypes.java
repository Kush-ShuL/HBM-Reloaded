package com.hbm.client.reactor.iter.utilities;
import com.mojang.blaze3d.vertex.*;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderType;
/** Source opaque2px GL_LINES, LEQUAL and depthMask(false), with no texture/lighting. */
final class FusionUtilityPreviewTypes extends RenderType {
    static final RenderType LINES=create("hbm_fusion_utility_preview",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(OptionalDouble.of(2D))).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));
    private FusionUtilityPreviewTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,()->{},()->{});}
}
