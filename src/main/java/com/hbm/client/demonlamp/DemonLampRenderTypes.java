package com.hbm.client.demonlamp;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;

/** Source untextured, unlit SRC_ALPHA/ONE ring quads; LEQUAL with depth writes disabled. */
final class DemonLampRenderTypes extends RenderType {
    private DemonLampRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType GLOW=create("hbm_demon_lamp_glow",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder()
        .setShaderState(POSITION_COLOR_SHADER).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL)
        .setTransparencyState(new TransparencyStateShard("hbm_demon_lamp_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();})).createCompositeState(false));
}
