package com.hbm.client.assemblyfactory;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Original unlit, uncullled alpha-tapered sparks; exact770/771/1/0 separate blending. */
final class AssemblyFactoryRenderTypes extends RenderType {
    private AssemblyFactoryRenderTypes(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final TransparencyStateShard SOURCE_BLEND=new TransparencyStateShard("hbm_assembly_factory_alpha",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    static final RenderType SPARKS=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_assembly_factory_sparks",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/machines/assembly_factory_sparks.png"),false,false)).setTransparencyState(SOURCE_BLEND).setCullState(NO_CULL).createCompositeState(false)),"AssemblyFactory/SourceSparks");
}
