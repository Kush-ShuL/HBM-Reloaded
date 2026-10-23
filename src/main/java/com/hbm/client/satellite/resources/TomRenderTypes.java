package com.hbm.client.satellite.resources;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Real TomPronter unlit body and source SRC_ALPHA/ONE flame; no second smoke/transparency compositor. */
final class TomRenderTypes extends RenderType {
    private static final TransparencyStateShard SOURCE_ADD=new TransparencyStateShard("hbm_tom_source_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
    static final RenderType BODY=make("body","tom_main",false),FLAME=make("flame","tom_flame",true);
    private static RenderType make(String name,String texture,boolean additive){return com.hbm.client.render.WorldEffectMeshes.register(create("hbm_tom_"+name,DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,262144,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/weapons/"+texture+".png"),false,false)).setTransparencyState(additive?SOURCE_ADD:NO_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"TOM/SourceBody+Flame");}
    private TomRenderTypes(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
