package com.hbm.client.animation.collada;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;

/** X5846 transform decomposition/interpolation, with its original arithmetic and matrix order. */
public final class Transform {
    public record Vector(double x, double y, double z) { }
    public record Rotation(float x, float y, float z, float w) {
        Rotation normalized() {
            float length = (float) Math.sqrt(x * x + y * y + z * z + w * w);
            if (length == 0) throw new IllegalStateException("Zero-length animation quaternion");
            float inverse = 1.0F / length;
            return new Rotation(x * inverse, y * inverse, z * inverse, w * inverse);
        }
    }
    public final Vector scale;
    public final Vector translation;
    public final Rotation rotation;
    public boolean hidden;

    public Transform(float[] matrix) {
        float x = (float) Math.sqrt((double) matrix[0] * matrix[0] + (double) matrix[1] * matrix[1] + (double) matrix[2] * matrix[2]);
        float y = (float) Math.sqrt((double) matrix[4] * matrix[4] + (double) matrix[5] * matrix[5] + (double) matrix[6] * matrix[6]);
        float z = (float) Math.sqrt((double) matrix[8] * matrix[8] + (double) matrix[9] * matrix[9] + (double) matrix[10] * matrix[10]);
        scale = new Vector(x, y, z);
        matrix[0] /= x; matrix[1] /= x; matrix[2] /= x;
        matrix[4] /= y; matrix[5] /= y; matrix[6] /= y;
        matrix[8] /= z; matrix[9] /= z; matrix[10] /= z;
        rotation = fromLegacyMatrix(matrix);
        translation = new Vector(matrix[3], matrix[7], matrix[11]);
    }

    /** Column-major buffer field interpretation matches the former LWJGL2 Matrix4f.load. */
    private static Rotation fromLegacyMatrix(float[] m) {
        float trace = m[0] + m[5] + m[10];
        float x, y, z, w, factor;
        if (trace >= 0) {
            factor = (float) Math.sqrt(trace + 1.0);
            w = factor * 0.5F;
            factor = 0.5F / factor;
            x = (m[9] - m[6]) * factor;
            y = (m[2] - m[8]) * factor;
            z = (m[4] - m[1]) * factor;
        } else if (Math.max(Math.max(m[0], m[5]), m[10]) == m[0]) {
            factor = (float) Math.sqrt(m[0] - (m[5] + m[10]) + 1.0);
            x = factor * 0.5F;
            factor = 0.5F / factor;
            y = (m[1] + m[4]) * factor;
            z = (m[8] + m[2]) * factor;
            w = (m[9] - m[6]) * factor;
        } else if (Math.max(Math.max(m[0], m[5]), m[10]) == m[5]) {
            factor = (float) Math.sqrt(m[5] - (m[10] + m[0]) + 1.0);
            y = factor * 0.5F;
            factor = 0.5F / factor;
            z = (m[6] + m[9]) * factor;
            x = (m[1] + m[4]) * factor;
            w = (m[2] - m[8]) * factor;
        } else {
            factor = (float) Math.sqrt(m[10] - (m[0] + m[5]) + 1.0);
            z = factor * 0.5F;
            factor = 0.5F / factor;
            x = (m[8] + m[2]) * factor;
            y = (m[6] + m[9]) * factor;
            w = (m[4] - m[1]) * factor;
        }
        return new Rotation(x, y, z, w);
    }

    public static Rotation slerp(Rotation first, Rotation second, float t) {
        double dot = first.x * second.x + first.y * second.y + first.z * second.z + first.w * second.w;
        if (dot < 0) {
            second = new Rotation(-second.x, -second.y, -second.z, -second.w);
            dot = -dot;
        }
        if (dot > 0.9999999) {
            // Intentionally the original X5846 expression, not a replacement nlerp formula.
            return new Rotation(first.x + t * second.x, first.y + t * second.y, first.z + t * second.z, first.w + t * second.w).normalized();
        }
        double angle = Math.acos(dot), theta = angle * t;
        double sine = Math.sin(theta), denominator = Math.sin(angle);
        float firstScale = (float) (Math.cos(theta) - dot * sine / denominator);
        float secondScale = (float) (sine / denominator);
        return new Rotation(firstScale * first.x + secondScale * second.x, firstScale * first.y + secondScale * second.y,
                firstScale * first.z + secondScale * second.z, firstScale * first.w + secondScale * second.w);
    }

    public Matrix4f interpolatedMatrix(Transform other, float t) {
        double tx = translation.x + (other.translation.x - translation.x) * t;
        double ty = translation.y + (other.translation.y - translation.y) * t;
        double tz = translation.z + (other.translation.z - translation.z) * t;
        double sx = scale.x + (other.scale.x - scale.x) * t;
        double sy = scale.y + (other.scale.y - scale.y) * t;
        double sz = scale.z + (other.scale.z - scale.z) * t;
        Rotation q = slerp(rotation, other.rotation, t);
        float xx = q.x * q.x, xy = q.x * q.y, xz = q.x * q.z, xw = q.x * q.w;
        float yy = q.y * q.y, yz = q.y * q.z, yw = q.y * q.w;
        float zz = q.z * q.z, zw = q.z * q.w;
        float[] m = {
                1 - 2 * (yy + zz), 2 * (xy + zw), 2 * (xz - yw), 0,
                2 * (xy - zw), 1 - 2 * (xx + zz), 2 * (yz + xw), 0,
                2 * (xz + yw), 2 * (yz - xw), 1 - 2 * (xx + yy), 0,
                0, 0, 0, 1};
        // X5846 scales rows in the column-major buffer, then writes translation.
        for (int column = 0; column < 4; column++) {
            int i = column * 4;
            m[i] = (float) (m[i] * sx);
            m[i + 1] = (float) (m[i + 1] * sy);
            m[i + 2] = (float) (m[i + 2] * sz);
        }
        m[12] = (float) tx; m[13] = (float) ty; m[14] = (float) tz;
        return new Matrix4f().set(m);
    }

    public void interpolateAndApply(Transform other, float t, PoseStack poses) {
        poses.mulPose(interpolatedMatrix(other, t));
    }
}
