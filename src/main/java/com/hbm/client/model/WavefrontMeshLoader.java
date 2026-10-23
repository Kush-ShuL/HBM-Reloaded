package com.hbm.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hbm.client.model.WavefrontMesh.Corner;
import com.hbm.client.model.WavefrontMesh.Face;
import com.hbm.client.model.WavefrontMesh.Normal;
import com.hbm.client.model.WavefrontMesh.NonFaceElement;
import com.hbm.client.model.WavefrontMesh.Directive;
import com.hbm.client.model.WavefrontMesh.Position;
import com.hbm.client.model.WavefrontMesh.TexCoord;
import com.hbm.client.model.WavefrontMesh.Triangle;

/** OBJ reader shared by machine, weapon, armor, entity and animated-part renderers. */
public final class WavefrontMeshLoader {
    private WavefrontMeshLoader() { }

    public static WavefrontMesh load(Reader source, String sourceName) throws IOException {
        List<Position> positions = new ArrayList<>();
        List<TexCoord> textures = new ArrayList<>();
        List<Normal> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();
        List<String> libraries = new ArrayList<>();
        List<NonFaceElement> nonFaces = new ArrayList<>();
        List<Directive> otherDirectives = new ArrayList<>();
        String object = "", legacyPart = "Default", material = "", smoothing = "off";
        List<String> groups = List.of();
        BufferedReader reader = source instanceof BufferedReader buffered ? buffered : new BufferedReader(source);
        String physical;
        StringBuilder continued = new StringBuilder();
        int line = 0;
        while ((physical = reader.readLine()) != null) {
            line++;
            String text = physical.split("#", 2)[0].strip();
            if (text.endsWith("\\")) {
                continued.append(text, 0, text.length() - 1).append(' ');
                continue;
            }
            if (!continued.isEmpty()) {
                text = continued + text;
                continued.setLength(0);
            }
            if (text.isBlank()) continue;
            String[] values = text.split("\\s+");
            try {
                switch (values[0]) {
                    case "v" -> {
                        require(values.length >= 4, "v requires x y z");
                        positions.add(new Position(number(values[1]), number(values[2]), number(values[3]), values.length > 4 ? number(values[4]) : 1));
                    }
                    case "vt" -> {
                        require(values.length >= 2, "vt requires u");
                        textures.add(new TexCoord(number(values[1]), values.length > 2 ? number(values[2]) : 0, values.length > 3 ? number(values[3]) : 0));
                    }
                    case "vn" -> {
                        require(values.length == 4, "vn requires x y z");
                        normals.add(new Normal(number(values[1]), number(values[2]), number(values[3])));
                    }
                    case "o" -> {
                        object = remainder(text);
                        legacyPart = object;
                    }
                    case "g" -> {
                        groups = List.copyOf(Arrays.asList(values).subList(1, values.length));
                        legacyPart = remainder(text);
                    }
                    case "usemtl" -> material = remainder(text);
                    case "mtllib" -> libraries.addAll(Arrays.asList(values).subList(1, values.length));
                    case "s" -> smoothing = remainder(text);
                    case "f" -> {
                        require(values.length >= 4, "f requires at least three corners");
                        List<Corner> corners = new ArrayList<>();
                        for (int index = 1; index < values.length; index++) {
                            String[] references = values[index].split("/", -1);
                            require(references.length <= 3, "invalid face reference " + values[index]);
                            int vertex = index(references[0], positions.size());
                            int uv = references.length > 1 && !references[1].isEmpty() ? index(references[1], textures.size()) : -1;
                            int normal = references.length > 2 && !references[2].isEmpty() ? index(references[2], normals.size()) : -1;
                            corners.add(new Corner(vertex, uv, normal));
                        }
                        // Both X5846 HFR and Forge derive a missing/flat normal from the first three corners.
                        Face face = new Face(corners, object, groups, legacyPart, material, smoothing, normal(positions, corners), line);
                        int faceIndex = faces.size();
                        faces.add(face);
                        triangulate(face, faceIndex, positions, triangles);
                    }
                    case "p", "l" -> {
                        List<Corner> corners = new ArrayList<>();
                        for (int i = 1; i < values.length; i++) {
                            String[] references = values[i].split("/", -1);
                            int vertex = index(references[0], positions.size());
                            int uv = references.length > 1 && !references[1].isEmpty() ? index(references[1], textures.size()) : -1;
                            corners.add(new Corner(vertex, uv, -1));
                        }
                        nonFaces.add(new NonFaceElement(values[0], corners, object, groups, material, line));
                    }
                    default -> otherDirectives.add(new Directive(values[0], text, line));
                }
            } catch (IllegalArgumentException exception) {
                throw new IOException(sourceName + ":" + line + ": " + exception.getMessage(), exception);
            }
        }
        if (!continued.isEmpty()) throw new IOException(sourceName + ": unterminated line continuation");
        return new WavefrontMesh(positions, textures, normals, faces, triangles, libraries, nonFaces, otherDirectives);
    }

    private static String remainder(String text) {
        int split = text.indexOf(' ');
        if (split < 0) split = text.indexOf('\t');
        return split < 0 ? "" : text.substring(split + 1).strip();
    }

    private static float number(String value) {
        float number = Float.parseFloat(value);
        require(Float.isFinite(number), "non-finite coordinate " + value);
        return number;
    }

    private static int index(String value, int size) {
        int reference = Integer.parseInt(value);
        int index = reference > 0 ? reference - 1 : size + reference;
        require(reference != 0 && index >= 0 && index < size, "OBJ index out of range: " + reference + " (size " + size + ")");
        return index;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    private static Normal normal(List<Position> vertices, List<Corner> corners) {
        Position a = vertices.get(corners.get(0).position());
        Position b = vertices.get(corners.get(1).position());
        Position c = vertices.get(corners.get(2).position());
        double x = (double) (b.y() - a.y()) * (c.z() - a.z()) - (double) (b.z() - a.z()) * (c.y() - a.y());
        double y = (double) (b.z() - a.z()) * (c.x() - a.x()) - (double) (b.x() - a.x()) * (c.z() - a.z());
        double z = (double) (b.x() - a.x()) * (c.y() - a.y()) - (double) (b.y() - a.y()) * (c.x() - a.x());
        double length = Math.sqrt(x * x + y * y + z * z);
        // Vec3.normalize in 1.7.10 returns the zero vector below 1e-4.
        return length < 1.0E-4 ? new Normal(0, 0, 0) : new Normal((float) (x / length), (float) (y / length), (float) (z / length));
    }

    private static void triangulate(Face face, int faceIndex, List<Position> positions, List<Triangle> output) {
        int size = face.corners().size();
        if (size == 3) {
            output.add(new Triangle(faceIndex, 0, 1, 2));
            return;
        }
        // Newell normal is used only to choose a stable projection for triangulation;
        // it never replaces authored normals or the legacy face normal.
        double nx = 0, ny = 0, nz = 0;
        for (int i = 0; i < size; i++) {
            Position a = positions.get(face.corners().get(i).position());
            Position b = positions.get(face.corners().get((i + 1) % size).position());
            nx += (a.y() - b.y()) * (a.z() + b.z());
            ny += (a.z() - b.z()) * (a.x() + b.x());
            nz += (a.x() - b.x()) * (a.y() + b.y());
        }
        int axis = Math.abs(nx) >= Math.abs(ny) && Math.abs(nx) >= Math.abs(nz) ? 0 : Math.abs(ny) >= Math.abs(nz) ? 1 : 2;
        double[] x = new double[size], y = new double[size];
        double extent = 0;
        for (int i = 0; i < size; i++) {
            Position p = positions.get(face.corners().get(i).position());
            x[i] = axis == 0 ? p.y() : p.x();
            y[i] = axis == 2 ? p.y() : p.z();
            extent = Math.max(extent, Math.max(Math.abs(x[i]), Math.abs(y[i])));
        }
        double epsilon = Math.max(1.0E-16, extent * extent * 1.0E-12);
        double area = 0;
        for (int i = 0; i < size; i++) area += x[i] * y[(i + 1) % size] - x[(i + 1) % size] * y[i];
        double direction = area < 0 ? -1 : 1;
        boolean convex = true;
        for (int i = 0; i < size; i++) {
            if (direction * cross(x, y, i, (i + 1) % size, (i + 2) % size) < -epsilon) convex = false;
        }
        if (convex || Math.abs(area) <= epsilon) {
            // Preserve the original quad diagonal, including deliberately degenerate quads.
            for (int i = 1; i < size - 1; i++) output.add(new Triangle(faceIndex, 0, i, i + 1));
            return;
        }
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < size; i++) remaining.add(i);
        while (remaining.size() > 3) {
            boolean clipped = false;
            for (int i = 0; i < remaining.size(); i++) {
                int a = remaining.get((i + remaining.size() - 1) % remaining.size());
                int b = remaining.get(i), c = remaining.get((i + 1) % remaining.size());
                if (direction * cross(x, y, a, b, c) <= epsilon) continue;
                boolean contains = false;
                for (int point : remaining) {
                    if (point == a || point == b || point == c) continue;
                    if (direction * cross(x, y, a, b, point) >= -epsilon && direction * cross(x, y, b, c, point) >= -epsilon && direction * cross(x, y, c, a, point) >= -epsilon) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    output.add(new Triangle(faceIndex, a, b, c));
                    remaining.remove(i);
                    clipped = true;
                    break;
                }
            }
            require(clipped, "cannot triangulate self-intersecting/degenerate polygon at source line " + face.sourceLine());
        }
        output.add(new Triangle(faceIndex, remaining.get(0), remaining.get(1), remaining.get(2)));
    }

    private static double cross(double[] x, double[] y, int a, int b, int c) {
        return (x[b] - x[a]) * (y[c] - y[a]) - (y[b] - y[a]) * (x[c] - x[a]);
    }
}
