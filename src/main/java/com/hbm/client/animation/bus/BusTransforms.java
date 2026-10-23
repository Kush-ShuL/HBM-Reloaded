package com.hbm.client.animation.bus;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4d;
import org.joml.Matrix4f;

/** Original translate, ordered Euler rotations, negative origin offset, then scale. */
public final class BusTransforms {
    private BusTransforms() { }

    public static Matrix4f matrix(double[] transform) {
        Matrix4d matrix = new Matrix4d().translate(transform[0], transform[1], transform[2]);
        for (int order = 0; order < 3; order++) {
            int axis = (int) transform[12 + order];
            matrix.rotate(Math.toRadians(transform[3 + axis]), axis == 0 ? 1 : 0, axis == 1 ? 1 : 0, axis == 2 ? 1 : 0);
        }
        matrix.translate(-transform[9], -transform[10], -transform[11]);
        matrix.scale(transform[6], transform[7], transform[8]);
        return new Matrix4f(matrix);
    }

    public static void apply(PoseStack poses, double[] transform) {
        poses.mulPose(matrix(transform));
    }
}
