package com.hbm.client.reactor.rbmk.console;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderType;
/** Original untextured quad and two-physical-pixel GL_LINES passes. */
final class RbmkConsoleRenderTypes extends RenderType {
    private RbmkConsoleRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType PANELS=create("hbm_rbmk_console_panels",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(CULL).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    static final RenderType GRAPH=create("hbm_rbmk_console_graph",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.DEBUG_LINES,4096,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setLineState(new LineStateShard(OptionalDouble.of(2))).setCullState(NO_CULL).setDepthTestState(NO_DEPTH_TEST).createCompositeState(false));
    private static final Map<RenderType,RenderType> FONTS=new IdentityHashMap<>();
    static RenderType font(RenderType original){return FONTS.computeIfAbsent(original,NoDepthWrite::new);}
    /** Delegate the actual Minecraft/font-mod shader+texture+depth-test, preserving only source depthMask(false). */
    private static final class NoDepthWrite extends RenderType {
        private NoDepthWrite(RenderType source){super("hbm_rbmk_console_font_"+FONTS.size(),source.format(),source.mode(),source.bufferSize(),false,true,()->{source.setupRenderState();RenderSystem.depthMask(false);},()->{source.clearRenderState();RenderSystem.depthMask(true);});}
    }
}
