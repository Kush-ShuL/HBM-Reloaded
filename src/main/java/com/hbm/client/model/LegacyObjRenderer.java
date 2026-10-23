package com.hbm.client.model;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.joml.Vector3f;
import java.util.function.Predicate;

import com.hbm.client.model.WavefrontMesh.Corner;
import com.hbm.client.model.WavefrontMesh.Face;
import com.hbm.client.model.WavefrontMesh.Normal;
import com.hbm.client.model.WavefrontMesh.Position;
import com.hbm.client.model.WavefrontMesh.TexCoord;
import com.hbm.client.model.WavefrontMesh.Triangle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/** Emits the original mesh after the caller's original model/animation PoseStack transforms. */
public final class LegacyObjRenderer {
    private LegacyObjRenderer() { }

    private static final ThreadLocal<Integer> REFLECTIONS = ThreadLocal.withInitial(() -> 0);

    /** Mirror a complete authored model, including its later animation matrices. */
    public static Reflection mirrorX(PoseStack poses, boolean mirror) { return new Reflection(poses, mirror); }

    public static final class Reflection implements AutoCloseable {
        private final PoseStack poses;
        private final int previous;
        private final boolean mirror;
        private Reflection(PoseStack poses, boolean mirror) {
            this.poses = poses;
            this.mirror = mirror;
            previous = REFLECTIONS.get();
            if (mirror) {
                poses.pushPose();
                poses.scale(-1, 1, 1);
                REFLECTIONS.set(previous + 1);
            }
        }
        @Override public void close() {
            if (!mirror) return;
            poses.popPose();
            if (previous == 0) REFLECTIONS.remove(); else REFLECTIONS.set(previous);
        }
    }

    private static final Map<WavefrontMesh, EnumMap<Convention, Prepared>> PREPARED = new IdentityHashMap<>();
    public static synchronized void clear() { PREPARED.clear(); }
    /** A reload prepares private immutable model data without changing the currently displayed resource generation. */
    static final class ReloadCache {
        private final Map<WavefrontMesh, EnumMap<Convention, Prepared>> meshes = new IdentityHashMap<>();
    }
    static ReloadCache prepareReload(Iterable<WavefrontMesh> meshes) {
        ReloadCache cache = new ReloadCache();
        for (WavefrontMesh mesh : meshes) {
            EnumMap<Convention, Prepared> conventions = new EnumMap<>(Convention.class);
            for (Convention convention : Convention.values()) conventions.put(convention, new Prepared(mesh, convention));
            cache.meshes.put(mesh, conventions);
        }
        return cache;
    }
    static synchronized void installReload(ReloadCache cache) {
        PREPARED.clear();
        PREPARED.putAll(cache.meshes);
    }
    private static synchronized Prepared prepare(WavefrontMesh mesh, Convention convention) {
        return PREPARED.computeIfAbsent(mesh, ignored -> new EnumMap<>(Convention.class))
                .computeIfAbsent(convention, ignored -> new Prepared(mesh, convention));
    }

    /** Only immutable source data is cached; pose/light/colour still come from each draw. */
    private static final class Prepared {
        final int[] starts, normalIds;
        final float[] uv;
        final Normal[] normals;
        final ThreadLocal<Normals> draws;
        Prepared(WavefrontMesh mesh, Convention convention) {
            starts = new int[mesh.faces().size() + 1];
            for (int f = 0; f < mesh.faces().size(); f++) starts[f + 1] = starts[f] + mesh.faces().get(f).corners().size();
            normalIds = new int[starts[starts.length - 1]];
            uv = new float[normalIds.length * 2];
            var ids = new LinkedHashMap<Normal, Integer>();
            var values = new ArrayList<Normal>();
            for (int f = 0; f < mesh.faces().size(); f++) {
                var face = mesh.faces().get(f);
                float[] average = averageUv(mesh, face, convention);
                for (int c = 0; c < face.corners().size(); c++) {
                    var corner = face.corners().get(c);
                    int index = starts[f] + c;
                    Normal normal = convention.smooth && corner.normal() >= 0 ? mesh.normals().get(corner.normal()) : face.normal();
                    Integer id = ids.get(normal);
                    if (id == null) { id = values.size(); ids.put(normal, id); values.add(normal); }
                    normalIds[index] = id;
                    if (corner.texture() >= 0) {
                        var texture = mesh.textureCoordinates().get(corner.texture());
                        float u = texture.u(), v = convention.flipV ? 1 - texture.v() : texture.v();
                        u += u > average[0] ? -convention.inset : convention.inset;
                        v += v > average[1] ? -convention.inset : convention.inset;
                        uv[index * 2] = u; uv[index * 2 + 1] = v;
                    }
                }
            }
            normals = values.toArray(Normal[]::new);
            draws = ThreadLocal.withInitial(() -> new Normals(normals.length));
        }
    }

    /** Each draw has its own generation; nested draws cannot reuse another pose's normal. */
    private static final class Normals {
        final long[] generations;
        final float[] values;
        final Vector3f temporary = new Vector3f();
        long generation;
        Normals(int count) { generations = new long[count]; values = new float[count * 3]; }
        long begin() { if (++generation == 0) { Arrays.fill(generations, 0); generation = 1; } return generation; }
        int transformed(Prepared prepared, int corner, PoseStack.Pose pose, long draw) {
            int id = prepared.normalIds[corner], index = id * 3;
            if (generations[id] != draw) {
                var normal = prepared.normals[id];
                // This is exactly VertexConsumer.setNormal(pose,...), including trusted-normal handling.
                pose.transformNormal(normal.x(), normal.y(), normal.z(), temporary);
                values[index] = temporary.x; values[index + 1] = temporary.y; values[index + 2] = temporary.z;
                generations[id] = draw;
            }
            return index;
        }
    }


    public enum Convention {
        /** Forge WavefrontObject/Face: V inverted, 0.0005 UV inset, flat face normals. */
        FORGE(true, 0.0005F, false),
        /** X5846 HFRWavefrontObject/S_Face: V inverted, zero UV inset, authored normals. */
        HFR(true, 0, true),
        /** X5846 HFRWavefrontObject.noSmooth(): V inverted, zero inset, flat normals. */
        HFR_FLAT(true, 0, false),
        /** Raw OBJ coordinates; useful for consumers which explicitly implement their own UV transform. */
        RAW(false, 0, true);

        final boolean flipV;
        final float inset;
        final boolean smooth;
        Convention(boolean flipV, float inset, boolean smooth) {
            this.flipV = flipV;
            this.inset = inset;
            this.smooth = smooth;
        }
    }

    /**
     * Suitable for vanilla entity RenderTypes, which use QUADS. A source triangle is
     * emitted as (a,b,c,c), preserving the triangle exactly in a quad vertex buffer.
     * Original quads are submitted unchanged, preserving the old diagonal and UVs.
     */
    public static void render(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                              int packedLight, int packedOverlay, int argb,
                              Convention convention, Predicate<Face> selected) {
        Prepared prepared = prepare(mesh, convention);
        Normals normals = prepared.draws.get(); long draw = normals.begin();
        boolean reflected = (REFLECTIONS.get() & 1) != 0;
        int triangleCursor = 0;
        for (int faceIndex = 0; faceIndex < mesh.faces().size(); faceIndex++) {
            Face face = mesh.faces().get(faceIndex);
            int firstTriangle = triangleCursor;
            while (triangleCursor < mesh.triangles().size() && mesh.triangles().get(triangleCursor).face() == faceIndex) triangleCursor++;
            if (!selected.test(face)) continue;
            if (face.corners().size() == 4) {
                // Keep the original 0--2 diagonal while restoring front-face winding.
                for (int index = 0; index < 4; index++) {
                    int corner = reflected && index != 0 ? 4 - index : index;
                    emit(mesh, face, corner, prepared.starts[faceIndex] + corner, poses.last(), consumer, packedLight, packedOverlay, argb, prepared, normals, draw);
                }
            } else {
                for (int index = firstTriangle; index < triangleCursor; index++) {
                    Triangle triangle = mesh.triangles().get(index);
                    emitTriangle(mesh, face, triangle, poses.last(), consumer, packedLight, packedOverlay, argb, prepared, normals, draw, true, reflected);
                }
            }
        }
    }

    /** Caller must supply a buffer whose VertexFormat.Mode is TRIANGLES. */
    public static void renderTriangles(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                                       int packedLight, int packedOverlay, int argb,
                                       Convention convention, Predicate<Face> selected) {
        Prepared prepared = prepare(mesh, convention);
        Normals normals = prepared.draws.get(); long draw = normals.begin();
        boolean reflected = (REFLECTIONS.get() & 1) != 0;
        for (Triangle triangle : mesh.triangles()) {
            Face face = mesh.faces().get(triangle.face());
            if (selected.test(face)) emitTriangle(mesh, face, triangle, poses.last(), consumer, packedLight, packedOverlay, argb, prepared, normals, draw, false, reflected);
        }
    }

    public static void renderAll(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                                 int light, int overlay, int argb, Convention convention) {
        render(mesh, poses, consumer, light, overlay, argb, convention, face -> true);
    }

    public static void renderOnly(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                                  int light, int overlay, int argb, Convention convention, String... parts) {
        render(mesh, poses, consumer, light, overlay, argb, convention,
                face -> containsPart(face, parts));
    }

    public static void renderAllExcept(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                                       int light, int overlay, int argb, Convention convention, String... parts) {
        render(mesh, poses, consumer, light, overlay, argb, convention,
                face -> !containsPart(face, parts));
    }

    private static boolean containsPart(Face face, String[] parts) {
        for (String part : parts) if (face.legacyPart().equalsIgnoreCase(part)) return true;
        return false;
    }

    private static void emitTriangle(WavefrontMesh mesh, Face face, Triangle triangle, PoseStack.Pose pose,
                                     VertexConsumer consumer, int light, int overlay, int argb,
                                     Prepared prepared, Normals normals, long draw, boolean quadBuffer, boolean reflected) {
        int start = prepared.starts[triangle.face()];
        int second = reflected ? triangle.third() : triangle.second();
        int third = reflected ? triangle.second() : triangle.third();
        emit(mesh, face, triangle.first(), start + triangle.first(), pose, consumer, light, overlay, argb, prepared, normals, draw);
        emit(mesh, face, second, start + second, pose, consumer, light, overlay, argb, prepared, normals, draw);
        emit(mesh, face, third, start + third, pose, consumer, light, overlay, argb, prepared, normals, draw);
        if (quadBuffer) emit(mesh, face, third, start + third, pose, consumer, light, overlay, argb, prepared, normals, draw);
    }

    private static float[] averageUv(WavefrontMesh mesh, Face face, Convention convention) {
        float u = 0, v = 0;
        int count = 0;
        for (Corner corner : face.corners()) {
            if (corner.texture() < 0) continue;
            TexCoord texture = mesh.textureCoordinates().get(corner.texture());
            u += texture.u();
            v += convention.flipV ? 1 - texture.v() : texture.v();
            count++;
        }
        return count == 0 ? new float[] {0, 0} : new float[] {u / count, v / count};
    }

    private static void emit(WavefrontMesh mesh, Face face, int cornerIndex, int vertex, PoseStack.Pose pose,
                             VertexConsumer consumer, int light, int overlay, int argb,
                             Prepared prepared, Normals normals, long draw) {
        Position position = mesh.positions().get(face.corners().get(cornerIndex).position());
        int n = normals.transformed(prepared, vertex, pose, draw);
        float nx = normals.values[n], ny = normals.values[n + 1], nz = normals.values[n + 2];
        consumer.addVertex(pose, position.x(), position.y(), position.z())
                .setColor(argb).setUv(prepared.uv[vertex * 2], prepared.uv[vertex * 2 + 1]).setOverlay(overlay).setLight(light)
                .setNormal(nx, ny, nz);
    }
}
