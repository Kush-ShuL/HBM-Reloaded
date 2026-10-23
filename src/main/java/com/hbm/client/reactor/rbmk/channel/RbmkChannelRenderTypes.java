package com.hbm.client.reactor.rbmk.channel;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** Original untextured/unlit alpha-one additive Cherenkov sheets; source leaves depth writes enabled. */
final class RbmkChannelRenderTypes extends RenderType {
    private RbmkChannelRenderTypes() { super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{}); }
    private static final TransparencyStateShard ADDITIVE = new TransparencyStateShard("hbm_rbmk_cherenkov",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
    static final RenderType CHERENKOV = create("hbm_rbmk_cherenkov",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).setTransparencyState(ADDITIVE).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
}
