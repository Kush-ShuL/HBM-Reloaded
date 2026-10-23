package com.hbm.client.coker;
import com.mojang.blaze3d.vertex.*;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderType;
final class CokerPreviewTypes extends RenderType {
    static final RenderType LINES=create("hbm_coker_preview",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(OptionalDouble.of(2D))).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));
    private CokerPreviewTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,()->{},()->{});}
}
