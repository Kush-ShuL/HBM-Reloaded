package com.hbm.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

/** Native COLOR_DEPTH_WRITE has an empty setup; source solid effects must actually enable writes. */
public final class SourceDepthWrite extends RenderType {
    public static final WriteMaskStateShard MASK = new WriteMaskStateShard(true, true) {
        private boolean previous;
        @Override public void setupRenderState() {
            previous = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            RenderSystem.depthMask(true);
            GL11.glDepthMask(true);
        }
        @Override public void clearRenderState() {
            RenderSystem.depthMask(previous);
            GL11.glDepthMask(previous);
        }
    };

    private SourceDepthWrite() {
        super("unused", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, () -> {}, () -> {});
    }
}
