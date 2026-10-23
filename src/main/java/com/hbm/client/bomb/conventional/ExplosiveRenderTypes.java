package com.hbm.client.bomb.conventional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.platform.GlStateManager;

/** Source TNT pulse uses untextured white geometry and SRC_ALPHA / DST_ALPHA blending. */
final class ExplosiveRenderTypes extends RenderType {
    private ExplosiveRenderTypes() { super("unused", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, () -> {}, () -> {}); }
    static final RenderType UNLIT = create("hbm_explosive_unlit", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, false, CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).createCompositeState(false));
    static final RenderType FLASH = com.hbm.client.render.WorldEffectMeshes.register(create("hbm_primed_flash", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true,
            CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).setTransparencyState(new TransparencyStateShard("hbm_tnt_blend", () -> {
                RenderSystem.enableBlend(); RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            }, () -> { RenderSystem.disableBlend(); RenderSystem.defaultBlendFunc(); })).createCompositeState(false)),"PrimedTNT/SourceWhitePulse");
}
