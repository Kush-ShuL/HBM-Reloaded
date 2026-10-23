package com.hbm.client.combinationfurnace;
import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
/** Only the real fire sheet is additive/source-forward, with original alpha0 cutoff and no depth write. */
final class CombinationFurnaceRenderTypes extends RenderType {
    private static final TransparencyStateShard ADD=new TransparencyStateShard("hbm_combination_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    static final RenderType FIRE=WorldEffectMeshes.register(create("hbm_combination_fire",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->CombinationFurnaceClient.FIRE)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/particle/rbmk_fire.png"),false,false)).setTransparencyState(ADD).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"RenderFurnaceCombination: source unlit SRC_ALPHA/ONE, alpha>=0, no depth writes");
    static final RenderType LINES=create("hbm_combination_preview",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,256,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(OptionalDouble.of(2D))).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));
    private CombinationFurnaceRenderTypes(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
