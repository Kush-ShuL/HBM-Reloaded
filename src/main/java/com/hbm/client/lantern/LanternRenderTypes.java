package com.hbm.client.lantern;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

/** Exact original world Light pass: untextured, unlit, opaque, no cull, ordinary depth test/write. */
final class LanternRenderTypes extends RenderType {
    private LanternRenderTypes() { super("unused", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, () -> { }, () -> { }); }
    static final RenderType LIGHT = create("hbm_lantern90_light", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS,
            256, false, false, CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL)
                    .setTransparencyState(NO_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));
    static VertexConsumer colorOnly(VertexConsumer output) {
        return new VertexConsumer() {
            @Override public VertexConsumer addVertex(float x, float y, float z) { output.addVertex(x, y, z); return this; }
            @Override public VertexConsumer setColor(int r, int g, int b, int a) { output.setColor(r, g, b, a); return this; }
            @Override public VertexConsumer setUv(float u, float v) { return this; }
            @Override public VertexConsumer setUv1(int u, int v) { return this; }
            @Override public VertexConsumer setUv2(int u, int v) { return this; }
            @Override public VertexConsumer setNormal(float x, float y, float z) { return this; }
        };
    }
}
