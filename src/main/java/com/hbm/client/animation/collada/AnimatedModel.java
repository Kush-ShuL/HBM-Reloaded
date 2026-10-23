package com.hbm.client.animation.collada;

import java.util.ArrayList;
import java.util.List;

import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.hbm.client.model.legacy.LegacyModelRenderContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

/** Original Collada hierarchy, timing transitions and traversal with buffered mesh drawing. */
public class AnimatedModel {
    public AnimationController controller;
    public String name = "";
    public float[] transform;
    public boolean hasGeometry = true;
    public boolean hasTransform;
    public String geo_name = "";
    public AnimatedModel parent;
    public List<AnimatedModel> children = new ArrayList<>();
    public WavefrontMesh geometry;

    public void renderAnimated(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb, long time) {
        try (var ignored = LegacyModelRenderContext.push(poses, vertices, light, overlay, argb)) {
            renderAnimated(time);
        }
    }

    public void render(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb) {
        try (var ignored = LegacyModelRenderContext.push(poses, vertices, light, overlay, argb)) {
            render();
        }
    }

    public void renderAnimated(long sysTime) { renderAnimated(sysTime, null); }

    public void renderAnimated(long sysTime, IAnimatedModelCallback callback) {
        if (controller.activeAnim == AnimationWrapper.EMPTY) {
            render(callback);
            return;
        }
        AnimationWrapper activeAnim = controller.activeAnim;
        int numKeyFrames = activeAnim.anim.numKeyFrames;
        int diff = (int) (sysTime - activeAnim.startTime);
        diff *= activeAnim.speedScale;
        if (diff > activeAnim.anim.length) {
            int remainder = diff % activeAnim.anim.length;
            switch (activeAnim.endResult.type) {
                case END -> {
                    controller.activeAnim = AnimationWrapper.EMPTY;
                    render(callback);
                    return;
                }
                case REPEAT -> activeAnim.startTime = sysTime - remainder;
                case REPEAT_REVERSE -> {
                    activeAnim.startTime = sysTime - remainder;
                    activeAnim.reverse = !activeAnim.reverse;
                }
                case START_NEW -> {
                    activeAnim.cloneStats(activeAnim.endResult.next);
                    activeAnim.startTime = sysTime - remainder;
                }
                case STAY -> activeAnim.startTime = sysTime - activeAnim.anim.length;
            }
        }
        diff = (int) (sysTime - activeAnim.startTime);
        if (activeAnim.reverse) diff = activeAnim.anim.length - diff;
        diff *= activeAnim.speedScale;
        float remapped = clamp(((float) diff / activeAnim.anim.length) * (numKeyFrames - 1), 0, numKeyFrames - 1);
        float diffN = clamp((float) diff / activeAnim.anim.length, 0, 1);
        int first = (int) remapped;
        int next = first < numKeyFrames - 1 ? first + 1 : first;
        renderWithIndex((float) (remapped - Math.floor(remapped)), first, next, diffN, callback);
        controller.activeAnim.prevFrame = first;
    }

    protected void renderWithIndex(float interpolation, int first, int next, float diffN, IAnimatedModelCallback callback) {
        LegacyModelRenderContext context = LegacyModelRenderContext.current();
        PoseStack poses = context.poses();
        poses.pushPose();
        try {
            boolean hidden = false;
            if (hasTransform) {
                Transform[] frames = controller.activeAnim.anim.objectTransforms.get(name);
                if (frames != null) {
                    hidden = frames[first].hidden;
                    frames[first].interpolateAndApply(frames[next], interpolation, poses);
                } else poses.mulPose(new Matrix4f().set(transform));
            }
            if (callback != null) hidden |= callback.onRender(controller.activeAnim.prevFrame, first, geometry, diffN, name);
            if (hasGeometry && !hidden) draw(context);
            if (callback != null) callback.postRender(controller.activeAnim.prevFrame, first, geometry, diffN, name);
            // A hidden node's children still render in X5846.
            for (AnimatedModel child : children) child.renderWithIndex(interpolation, first, next, diffN, callback);
        } finally {
            poses.popPose();
        }
    }

    public void render() { render(null); }

    public void render(IAnimatedModelCallback callback) {
        LegacyModelRenderContext context = LegacyModelRenderContext.current();
        PoseStack poses = context.poses();
        poses.pushPose();
        try {
            if (hasTransform) poses.mulPose(new Matrix4f().set(transform));
            boolean hidden = callback != null && callback.onRender(-1, -1, geometry, -1, name);
            if (hasGeometry && !hidden) draw(context);
            if (callback != null) callback.postRender(-1, -1, geometry, -1, name);
            for (AnimatedModel child : children) child.render(callback);
        } finally {
            poses.popPose();
        }
    }

    private void draw(LegacyModelRenderContext context) {
        if (geometry == null) throw new IllegalStateException("Missing original Collada geometry: " + geo_name);
        LegacyObjRenderer.renderAll(geometry, context.poses(), context.vertices(), context.light(), context.overlay(), context.argb(), LegacyObjRenderer.Convention.RAW);
    }

    private static float clamp(float number, float minimum, float maximum) {
        return number < minimum ? minimum : number > maximum ? maximum : number;
    }

    /** Mesh replaces the removed OpenGL display-list handle; callback timing/order is unchanged. */
    public interface IAnimatedModelCallback {
        boolean onRender(int previousFrame, int currentFrame, WavefrontMesh model, float diffN, String modelName);
        default void postRender(int previousFrame, int currentFrame, WavefrontMesh model, float diffN, String modelName) { }
    }
}
