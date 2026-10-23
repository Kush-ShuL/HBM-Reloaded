package com.hbm.client.model;

import java.util.List;
import java.util.function.Predicate;

/** Immutable OBJ data in its authored coordinate system. No atlas remapping or centering. */
public record WavefrontMesh(
        List<Position> positions,
        List<TexCoord> textureCoordinates,
        List<Normal> normals,
        List<Face> faces,
        List<Triangle> triangles,
        List<String> materialLibraries,
        List<NonFaceElement> nonFaceElements,
        List<Directive> otherDirectives) {
    public WavefrontMesh {
        positions = List.copyOf(positions);
        textureCoordinates = List.copyOf(textureCoordinates);
        normals = List.copyOf(normals);
        faces = List.copyOf(faces);
        triangles = List.copyOf(triangles);
        materialLibraries = List.copyOf(materialLibraries);
        nonFaceElements = List.copyOf(nonFaceElements);
        otherDirectives = List.copyOf(otherDirectives);
    }

    public record Position(float x, float y, float z, float w) { }
    /** Raw vt values, including the optional third coordinate. V is not changed by loading. */
    public record TexCoord(float u, float v, float w) { }
    public record Normal(float x, float y, float z) { }
    /** Zero-based resolved OBJ indices; -1 means the optional vt/vn was absent. */
    public record Corner(int position, int texture, int normal) { }
    public record Face(List<Corner> corners, String object, List<String> groups,
                       String legacyPart, String material, String smoothingGroup,
                       Normal normal, int sourceLine) {
        public Face {
            corners = List.copyOf(corners);
            groups = List.copyOf(groups);
        }
    }
    /** Corner indices refer to the original polygon, preserving its UV and normal seams. */
    public record Triangle(int face, int first, int second, int third) { }
    /** X5846's loaders ignore line/point drawing; retain their authored data for explicit consumers. */
    public record NonFaceElement(String keyword, List<Corner> corners, String object,
                                 List<String> groups, String material, int sourceLine) {
        public NonFaceElement {
            corners = List.copyOf(corners);
            groups = List.copyOf(groups);
        }
    }
    /** Freeform/exporter directives remain available rather than being silently deleted. */
    public record Directive(String keyword, String source, int sourceLine) { }

    /** Forge/HFR renderOnly/renderPart used the most recently declared g or o name. */
    public static Predicate<Face> legacyPart(String name) {
        return face -> face.legacyPart().equalsIgnoreCase(name);
    }

    public static Predicate<Face> group(String name) {
        return face -> face.groups().stream().anyMatch(group -> group.equals(name));
    }
}
