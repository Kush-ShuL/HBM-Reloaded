package com.hbm.client.model.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/** Explicit replacement for the old model's implicit OpenGL/Tessellator context. */
public record LegacyModelRenderContext(PoseStack poses, VertexConsumer vertices,
                                        int light, int overlay, int argb) {
    private static final ThreadLocal<LegacyModelRenderContext> CURRENT = new ThreadLocal<>();

    public static LegacyModelRenderContext currentOrNull() { return CURRENT.get(); }

    public static LegacyModelRenderContext current() {
        LegacyModelRenderContext context = CURRENT.get();
        if (context == null) throw new IllegalStateException("Render a legacy Java model inside ModelBase.renderToBuffer or LegacyModelRenderContext.push");
        return context;
    }

    public static Scope push(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb) {
        LegacyModelRenderContext previous = CURRENT.get();
        CURRENT.set(new LegacyModelRenderContext(poses, vertices, light, overlay, argb));
        return new Scope(previous);
    }

    public static final class Scope implements AutoCloseable {
        private final LegacyModelRenderContext previous;
        private boolean closed;

        private Scope(LegacyModelRenderContext previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (closed) return;
            if (previous == null) CURRENT.remove();
            else CURRENT.set(previous);
            closed = true;
        }
    }
}
