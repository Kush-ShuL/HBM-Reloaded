package com.hbm.client.model;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
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

    private static final Map<WavefrontMesh, Geometry> PREPARED = new IdentityHashMap<>();
    public static synchronized void clear() { PREPARED.clear(); }
    /** A reload prepares private immutable model data without changing the currently displayed resource generation. */
    static final class ReloadCache {
        private final Map<WavefrontMesh, Geometry> meshes = new IdentityHashMap<>();
    }
    static ReloadCache prepareReload(Iterable<WavefrontMesh> meshes) {
        ReloadCache cache = new ReloadCache();
        for (WavefrontMesh mesh : meshes) {
            Geometry geometry = new Geometry(mesh);
            for (Convention convention : Convention.values()) geometry.conventions.put(convention, new Prepared(geometry, convention));
            cache.meshes.put(mesh, geometry);
        }
        return cache;
    }
    static synchronized void installReload(ReloadCache cache) {
        PREPARED.clear();
        PREPARED.putAll(cache.meshes);
    }
    private static synchronized Prepared prepare(WavefrontMesh mesh, Convention convention) {
        Geometry geometry = PREPARED.computeIfAbsent(mesh, Geometry::new);
        return geometry.conventions.computeIfAbsent(convention, ignored -> new Prepared(geometry, convention));
    }

    /** Face order and part membership are independent of the draw's pose and UV convention. */
    private static final class Geometry {
        final WavefrontMesh mesh;
        final int[] starts, triangleStarts, allFaces, positionIds;
        final Position[] positions;
        final Map<String, int[]> parts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        final Map<int[], Stream> streams = new IdentityHashMap<>();
        final ArrayList<Selection> selections = new ArrayList<>();
        final EnumMap<Convention, Prepared> conventions = new EnumMap<>(Convention.class);

        Geometry(WavefrontMesh mesh) {
            this.mesh = mesh;
            int count = mesh.faces().size();
            starts = new int[count + 1];
            triangleStarts = new int[count + 1];
            allFaces = new int[count];
            Map<String, ArrayList<Integer>> grouped = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            int triangle = 0;
            for (int f = 0; f < count; f++) {
                Face face = mesh.faces().get(f);
                allFaces[f] = f;
                starts[f + 1] = starts[f] + face.corners().size();
                grouped.computeIfAbsent(face.legacyPart(), ignored -> new ArrayList<>()).add(f);
                while (triangle < mesh.triangles().size() && mesh.triangles().get(triangle).face() == f) triangle++;
                triangleStarts[f + 1] = triangle;
            }
            positions = mesh.positions().toArray(Position[]::new);
            positionIds = new int[starts[count]];
            for (int f = 0; f < count; f++) {
                var corners = mesh.faces().get(f).corners();
                for (int c = 0; c < corners.size(); c++) positionIds[starts[f] + c] = corners.get(c).position();
            }
            grouped.forEach((part, faces) -> parts.put(part, faces.stream().mapToInt(Integer::intValue).toArray()));
            stream(allFaces);
            parts.values().forEach(this::stream);
        }

        synchronized Stream stream(int[] faces) {
            Stream stream = streams.get(faces);
            if (stream == null) { stream = new Stream(this, faces); streams.put(faces, stream); }
            return stream;
        }

        synchronized int[] select(String[] names, boolean exclude) {
            if (!exclude && names.length == 1) {
                int[] found = parts.get(names[0]);
                return found == null ? NO_FACES : found;
            }
            for (Selection selection : selections) {
                if (selection.exclude == exclude && Arrays.equals(selection.names, names)) return selection.faces;
            }
            int[] faces = Arrays.stream(allFaces)
                    .filter(f -> containsPart(mesh.faces().get(f), names) != exclude).toArray();
            selections.add(new Selection(names.clone(), exclude, faces));
            return faces;
        }
    }

    private static final int[] NO_FACES = new int[0];
    private record Selection(String[] names, boolean exclude, int[] faces) { }

    /** The original quad order and mirrored winding, prepared once per part selection. */
    private static final class Stream {
        final int[] corners, reflected, positions;
        Stream(Geometry geometry, int[] faces) {
            var forward = new ArrayList<Integer>();
            var reverse = new ArrayList<Integer>();
            var used = new java.util.BitSet(geometry.positions.length);
            for (int face : faces) {
                int start = geometry.starts[face];
                if (geometry.starts[face + 1] - start == 4) {
                    for (int i = 0; i < 4; i++) {
                        forward.add(start + i);
                        reverse.add(start + (i == 0 ? 0 : 4 - i));
                    }
                } else {
                    for (int i = geometry.triangleStarts[face]; i < geometry.triangleStarts[face + 1]; i++) {
                        Triangle triangle = geometry.mesh.triangles().get(i);
                        forward.add(start + triangle.first()); forward.add(start + triangle.second());
                        forward.add(start + triangle.third()); forward.add(start + triangle.third());
                        reverse.add(start + triangle.first()); reverse.add(start + triangle.third());
                        reverse.add(start + triangle.second()); reverse.add(start + triangle.second());
                    }
                }
            }
            corners = forward.stream().mapToInt(Integer::intValue).toArray();
            reflected = reverse.stream().mapToInt(Integer::intValue).toArray();
            for (int corner : corners) used.set(geometry.positionIds[corner]);
            positions = used.stream().toArray();
        }
    }

    /** Only immutable source data is cached; pose/light/colour still come from each draw. */
    private static final class Prepared {
        final Geometry geometry;
        final int[] starts, normalIds;
        final float[] uv;
        final Normal[] normals;
        final ThreadLocal<Normals> draws;
        final Map<Stream, int[]> streamNormals = new IdentityHashMap<>();
        final ThreadLocal<Frames> frames;
        Prepared(Geometry geometry, Convention convention) {
            this.geometry = geometry;
            WavefrontMesh mesh = geometry.mesh;
            starts = geometry.starts;
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
            frames = ThreadLocal.withInitial(() -> new Frames(geometry.positions.length, normals.length));
            synchronized (geometry) { geometry.streams.values().forEach(this::normalSelection); }
        }
        synchronized int[] normalSelection(Stream stream) {
            int[] selection = streamNormals.get(stream);
            if (selection == null) {
                var used = new java.util.BitSet(normals.length);
                for (int corner : stream.corners) used.set(normalIds[corner]);
                selection = used.stream().toArray();
                streamNormals.put(stream, selection);
            }
            return selection;
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

    private static final class Frame {
        final float[] positions, normals;
        final Vector3f temporary = new Vector3f();
        Frame(int positions, int normals) { this.positions = new float[positions * 3]; this.normals = new float[normals * 3]; }
    }
    /** Nested consumer callbacks borrow a separate frame, leaving their caller's eager transforms intact. */
    private static final class Frames {
        final int positions, normals;
        final ArrayList<Frame> frames = new ArrayList<>();
        int depth;
        Frames(int positions, int normals) { this.positions = positions; this.normals = normals; }
        Frame begin() {
            if (depth == frames.size()) frames.add(new Frame(positions, normals));
            return frames.get(depth++);
        }
        void end() { depth--; }
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
        PoseStack.Pose pose = poses.last();
        for (int faceIndex = 0; faceIndex < mesh.faces().size(); faceIndex++) {
            Face face = mesh.faces().get(faceIndex);
            if (!selected.test(face)) continue;
            renderFace(prepared, faceIndex, pose, consumer, packedLight, packedOverlay, argb, normals, draw, reflected);
        }
    }

    private static void renderFaces(Prepared prepared, int[] faces, PoseStack poses, VertexConsumer consumer,
                                    int light, int overlay, int argb) {
        Geometry geometry = prepared.geometry;
        Stream stream = geometry.stream(faces);
        int[] normalSelection = prepared.normalSelection(stream);
        int[] corners = (REFLECTIONS.get() & 1) != 0 ? stream.reflected : stream.corners;
        PoseStack.Pose pose = poses.last();
        Frames frames = prepared.frames.get();
        Frame frame = frames.begin();
        try {
            for (int id : stream.positions) {
                Position position = geometry.positions[id];
                pose.pose().transformPosition(position.x(), position.y(), position.z(), frame.temporary);
                int p = id * 3;
                frame.positions[p] = frame.temporary.x; frame.positions[p + 1] = frame.temporary.y; frame.positions[p + 2] = frame.temporary.z;
            }
            for (int id : normalSelection) {
                Normal normal = prepared.normals[id];
                pose.transformNormal(normal.x(), normal.y(), normal.z(), frame.temporary);
                int n = id * 3;
                frame.normals[n] = frame.temporary.x; frame.normals[n + 1] = frame.temporary.y; frame.normals[n + 2] = frame.temporary.z;
            }
            for (int corner : corners) {
                int p = geometry.positionIds[corner] * 3, n = prepared.normalIds[corner] * 3;
                consumer.addVertex(frame.positions[p], frame.positions[p + 1], frame.positions[p + 2], argb,
                        prepared.uv[corner * 2], prepared.uv[corner * 2 + 1], overlay, light,
                        frame.normals[n], frame.normals[n + 1], frame.normals[n + 2]);
            }
        } finally { frames.end(); }
    }

    private static void renderFace(Prepared prepared, int faceIndex, PoseStack.Pose pose, VertexConsumer consumer,
                                   int light, int overlay, int argb, Normals normals, long draw, boolean reflected) {
        Geometry geometry = prepared.geometry;
        Face face = geometry.mesh.faces().get(faceIndex);
        if (face.corners().size() == 4) {
            // Keep the original 0--2 diagonal while restoring front-face winding.
            for (int index = 0; index < 4; index++) {
                int corner = reflected && index != 0 ? 4 - index : index;
                emit(prepared.starts[faceIndex] + corner, pose, consumer, light, overlay, argb, prepared, normals, draw);
            }
        } else {
            for (int index = geometry.triangleStarts[faceIndex]; index < geometry.triangleStarts[faceIndex + 1]; index++) {
                emitTriangle(geometry.mesh.triangles().get(index), pose, consumer, light, overlay, argb, prepared, normals, draw, true, reflected);
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
        PoseStack.Pose pose = poses.last();
        for (Triangle triangle : mesh.triangles()) {
            Face face = mesh.faces().get(triangle.face());
            if (selected.test(face)) emitTriangle(triangle, pose, consumer, packedLight, packedOverlay, argb, prepared, normals, draw, false, reflected);
        }
    }

    public static void renderAll(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                                 int light, int overlay, int argb, Convention convention) {
        Prepared prepared = prepare(mesh, convention);
        renderFaces(prepared, prepared.geometry.allFaces, poses, consumer, light, overlay, argb);
    }

    public static void renderOnly(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                                  int light, int overlay, int argb, Convention convention, String... parts) {
        Prepared prepared = prepare(mesh, convention);
        renderFaces(prepared, prepared.geometry.select(parts, false), poses, consumer, light, overlay, argb);
    }

    public static void renderAllExcept(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer,
                                       int light, int overlay, int argb, Convention convention, String... parts) {
        Prepared prepared = prepare(mesh, convention);
        renderFaces(prepared, prepared.geometry.select(parts, true), poses, consumer, light, overlay, argb);
    }

    private static boolean containsPart(Face face, String[] parts) {
        for (String part : parts) if (face.legacyPart().equalsIgnoreCase(part)) return true;
        return false;
    }

    private static void emitTriangle(Triangle triangle, PoseStack.Pose pose,
                                     VertexConsumer consumer, int light, int overlay, int argb,
                                     Prepared prepared, Normals normals, long draw, boolean quadBuffer, boolean reflected) {
        int start = prepared.starts[triangle.face()];
        int second = reflected ? triangle.third() : triangle.second();
        int third = reflected ? triangle.second() : triangle.third();
        emit(start + triangle.first(), pose, consumer, light, overlay, argb, prepared, normals, draw);
        emit(start + second, pose, consumer, light, overlay, argb, prepared, normals, draw);
        emit(start + third, pose, consumer, light, overlay, argb, prepared, normals, draw);
        if (quadBuffer) emit(start + third, pose, consumer, light, overlay, argb, prepared, normals, draw);
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

    private static void emit(int vertex, PoseStack.Pose pose,
                             VertexConsumer consumer, int light, int overlay, int argb,
                             Prepared prepared, Normals normals, long draw) {
        int n = normals.transformed(prepared, vertex, pose, draw);
        float nx = normals.values[n], ny = normals.values[n + 1], nz = normals.values[n + 2];
        Position position = prepared.geometry.positions[prepared.geometry.positionIds[vertex]];
        Vector3f transformed = pose.pose().transformPosition(position.x(), position.y(), position.z(), normals.temporary);
        consumer.addVertex(transformed.x, transformed.y, transformed.z, argb,
                prepared.uv[vertex * 2], prepared.uv[vertex * 2 + 1], overlay, light, nx, ny, nz);
    }
}
