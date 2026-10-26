package com.hbm.client.animation.hmf;

import java.util.Arrays;
import java.util.function.Predicate;

import com.hbm.client.animation.AnimationClock;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/** HMF is the original OBJ mesh with Forge UV inset plus time-driven V scrolling. */
public record HmfModel(WavefrontMesh mesh) {
    public static HmfModel load(ResourceManager resources, ResourceLocation location) {
        return new HmfModel(LegacyObjModels.INSTANCE.get(resources, location));
    }

    public static float scrollOffset(long frameMillis) {
        float originalFloatTime = frameMillis;
        return (float) (((double) originalFloatTime % HmfController.modoloMod) / HmfController.quotientMod);
    }

    public void renderAll(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb) {
        render(poses, vertices, light, overlay, argb, AnimationClock.get_ms(), face -> true);
    }

    /** Allows an emitter to retain millisecond precision until its periodic UV offset is computed. */
    public void renderAllWithScroll(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb, float offset) {
        LegacyObjRenderer.renderAll(mesh, poses, new ScrolledVertices(vertices, offset),
                light, overlay, argb, LegacyObjRenderer.Convention.FORGE);
    }

    public void renderOnly(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb, String... groups) {
        render(poses, vertices, light, overlay, argb, AnimationClock.get_ms(),
                face -> Arrays.stream(groups).anyMatch(group -> face.legacyPart().equalsIgnoreCase(group)));
    }

    public void renderAllExcept(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb, String... groups) {
        render(poses, vertices, light, overlay, argb, AnimationClock.get_ms(),
                face -> Arrays.stream(groups).noneMatch(group -> face.legacyPart().equalsIgnoreCase(group)));
    }

    public void render(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb,
                       long frameMillis, Predicate<WavefrontMesh.Face> selected) {
        LegacyObjRenderer.render(mesh, poses, new ScrolledVertices(vertices, scrollOffset(frameMillis)),
                light, overlay, argb, LegacyObjRenderer.Convention.FORGE, selected);
    }

    private record ScrolledVertices(VertexConsumer delegate, float offset) implements VertexConsumer {
        @Override public VertexConsumer addVertex(float x, float y, float z) { delegate.addVertex(x, y, z); return this; }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { delegate.setColor(r, g, b, a); return this; }
        @Override public VertexConsumer setUv(float u, float v) { delegate.setUv(u, v + offset); return this; }
        @Override public VertexConsumer setUv1(int u, int v) { delegate.setUv1(u, v); return this; }
        @Override public VertexConsumer setUv2(int u, int v) { delegate.setUv2(u, v); return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { delegate.setNormal(x, y, z); return this; }
    }
}
