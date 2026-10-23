package com.hbm.client.steelfurnace;
import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.*;
/** Exact source untextured/fullbright additive vent quads, independently from the normally lit opaque OBJ. */
final class SteelFurnaceRenderTypes extends RenderType {
    private static final TransparencyStateShard SOURCE_ADD=new TransparencyStateShard("hbm_steel_vent_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    static final RenderType VENT=WorldEffectMeshes.register(create("hbm_steel_furnace_vent",DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,VertexFormat.Mode.QUADS,512,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionColorLightmapShader)).setTransparencyState(SOURCE_ADD).setCullState(CULL).setLightmapState(LIGHTMAP).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"RenderFurnaceSteel: four source vent quads, SRC_ALPHA/ONE, fullbright, original depth writes");
    static final RenderType LINES=create("hbm_steel_furnace_preview",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(OptionalDouble.of(2D))).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));
    private SteelFurnaceRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
