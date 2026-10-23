package com.hbm.client.refueler;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;

/** Source lit untextured SRC_ALPHA,ONE, culling and ordinary depth test/write. */
final class RefuelerRenderTypes extends RenderType {
    static ShaderInstance shader;
    private static final TransparencyStateShard ADD=new TransparencyStateShard("refueler_source_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    static final RenderType FLUID=WorldEffectMeshes.register(create("hbm_refueler_source_fluid",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->shader)).setTransparencyState(ADD).setLightmapState(LIGHTMAP).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"Refueler252/lit additive clipped Fluid");
    private RefuelerRenderTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
