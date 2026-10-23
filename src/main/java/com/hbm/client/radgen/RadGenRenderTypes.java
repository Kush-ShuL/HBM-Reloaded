package com.hbm.client.radgen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** Exact untextured stages; all geometry/normals/UV preparation remain in the unique LegacyObjRenderer. */
final class RadGenRenderTypes extends RenderType {
    private RadGenRenderTypes() { super("unused", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, () -> {}, () -> {}); }
    private static final ResourceLocation WHITE = ResourceLocation.parse("hbm:textures/models/machines/radgen_untextured.png");
    private static final TransparencyStateShard BLEND = new TransparencyStateShard("hbm_radgen_source_alpha", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }, () -> { RenderSystem.disableBlend(); RenderSystem.defaultBlendFunc(); });
    static final RenderType LIGHT = com.hbm.client.render.WorldEffectMeshes.register(create("hbm_radgen_unlit_indicator", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 512, false, false,
        CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"RadGen/SourceIndicatorOnly");
    static final RenderType ITEM_LIGHT = create("hbm_radgen_lit_item_indicator", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 512, false, false,
        CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER).setTextureState(new TextureStateShard(WHITE, false, false)).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    static final RenderType GLASS = create("hbm_radgen_lit_untextured_glass", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1024, false, true,
        CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(WHITE, false, false)).setTransparencyState(BLEND).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    static VertexConsumer colorOnly(VertexConsumer target) {
        return new VertexConsumer() {
            @Override public VertexConsumer addVertex(float x, float y, float z) { target.addVertex(x, y, z); return this; }
            @Override public VertexConsumer setColor(int r, int g, int b, int a) { target.setColor(r, g, b, a); return this; }
            @Override public VertexConsumer setUv(float u, float v) { return this; }
            @Override public VertexConsumer setUv1(int u, int v) { return this; }
            @Override public VertexConsumer setUv2(int u, int v) { return this; }
            @Override public VertexConsumer setNormal(float x, float y, float z) { return this; }
        };
    }
}
