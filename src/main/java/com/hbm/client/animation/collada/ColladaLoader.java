package com.hbm.client.animation.collada;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.hbm.client.model.WavefrontMesh;
import com.hbm.client.model.WavefrontMesh.Corner;
import com.hbm.client.model.WavefrontMesh.Face;
import com.hbm.client.model.WavefrontMesh.Normal;
import com.hbm.client.model.WavefrontMesh.Position;
import com.hbm.client.model.WavefrontMesh.TexCoord;
import com.hbm.client.model.WavefrontMesh.Triangle;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/** X5846 Collada subset: original node selection, rigid meshes and uniformly sampled transforms. */
public final class ColladaLoader {
    private ColladaLoader() { }

    public static AnimatedModel load(ResourceLocation file) { return load(file, false); }
    public static AnimatedModel load(ResourceLocation file, boolean flipV) {
        try { return load(Minecraft.getInstance().getResourceManager(), file, flipV); }
        catch (IOException exception) {
            LoggerFactory.getLogger(ColladaLoader.class).error("FAILED TO LOAD MODEL: {}", file, exception);
            return null;
        }
    }
    public static AnimatedModel load(ResourceManager resources, ResourceLocation file, boolean flipV) throws IOException {
        try (InputStream input = resources.open(file)) { return load(input, flipV); }
    }
    public static AnimatedModel load(InputStream input, boolean flipV) throws IOException {
        return parse(document(input), flipV);
    }
    public static Animation loadAnim(int length, ResourceLocation file) {
        try { return loadAnim(Minecraft.getInstance().getResourceManager(), length, file); }
        catch (IOException exception) {
            LoggerFactory.getLogger(ColladaLoader.class).error("FAILED TO LOAD ANIMATION: {}", file, exception);
            return null;
        }
    }
    public static Animation loadAnim(ResourceManager resources, int length, ResourceLocation file) throws IOException {
        try (InputStream input = resources.open(file)) { return loadAnim(length, input); }
    }
    public static Animation loadAnim(int length, InputStream input) throws IOException {
        Element section = firstDescendant(document(input), "library_animations");
        Animation animation = new Animation();
        animation.length = length;
        for (Element node : children(section)) {
            if (!node.getNodeName().equals("animation")) continue;
            List<Element> components = children(node);
            if (components.isEmpty()) continue;
            Transform[] transforms = null;
            for (Element component : components) {
                if (component.getAttribute("id").endsWith("transform")) {
                    float[] data = floats(output(component).getElementsByTagName("float_array").item(0).getTextContent());
                    transforms = new Transform[data.length / 16];
                    for (int i = 0; i < transforms.length; i++) {
                        float[] matrix = new float[16];
                        System.arraycopy(data, i * 16, matrix, 0, 16);
                        transforms[i] = new Transform(matrix);
                    }
                } else if (component.getAttribute("id").endsWith("hide_viewport")) {
                    int[] hidden = integers(output(component).getElementsByTagName("float_array").item(0).getTextContent());
                    for (int i = 0; i < hidden.length; i++) transforms[i].hidden = hidden[i] > 0;
                }
            }
            animation.objectTransforms.put(node.getAttribute("name"), transforms);
            animation.numKeyFrames = transforms.length;
        }
        return animation;
    }

    private static Element document(InputStream input) throws IOException {
        try { return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input).getDocumentElement(); }
        catch (ParserConfigurationException | SAXException exception) { throw new IOException("Cannot parse original Collada document", exception); }
    }

    private static AnimatedModel parse(Element root, boolean flipV) {
        Element scene = children(firstDescendant(root, "library_visual_scenes")).get(0);
        AnimatedModel structure = new AnimatedModel() {
            @Override
            protected void renderWithIndex(float interpolation, int first, int next, float diffN, IAnimatedModelCallback callback) {
                for (AnimatedModel child : children) child.renderWithIndex(interpolation, first, next, diffN, callback);
            }
            @Override
            public void render() { for (AnimatedModel child : children) child.render(); }
        };
        for (Element node : children(scene)) {
            if (node.getElementsByTagName("instance_geometry").getLength() > 0) structure.children.add(parseStructure(node));
        }
        Map<String, WavefrontMesh> geometry = parseGeometry(firstDescendant(root, "library_geometries"), flipV);
        attach(structure, geometry, new AnimationController());
        return structure;
    }

    private static AnimatedModel parseStructure(Element root) {
        AnimatedModel model = new AnimatedModel();
        model.name = root.getAttribute("name");
        for (Element element : children(root)) {
            if (element.getAttribute("sid").equals("transform")) {
                float[] matrix = floats(element.getTextContent());
                model.transform = new float[] {matrix[0], matrix[4], matrix[8], matrix[12], matrix[1], matrix[5], matrix[9], matrix[13], matrix[2], matrix[6], matrix[10], matrix[14], matrix[3], matrix[7], matrix[11], matrix[15]};
                model.hasTransform = true;
            } else if (element.getTagName().equals("instance_geometry")) model.geo_name = element.getAttribute("url").substring(1);
            else if (element.getElementsByTagName("instance_geometry").getLength() > 0) {
                AnimatedModel child = parseStructure(element);
                child.parent = model;
                model.children.add(child);
            }
        }
        return model;
    }

    private static Map<String, WavefrontMesh> parseGeometry(Element root, boolean flipV) {
        Map<String, WavefrontMesh> result = new HashMap<>();
        for (Element geometry : namedChildren(root, "geometry")) {
            Element mesh = namedChildren(geometry, "mesh").get(0);
            float[] coordinates = {}, normalValues = {}, uvValues = {};
            List<Integer> indices = new ArrayList<>();
            for (Element section : children(mesh)) {
                String id = section.getAttribute("id");
                if (id.endsWith("mesh-positions")) coordinates = floats(firstDescendant(section, "float_array").getTextContent());
                else if (id.endsWith("mesh-normals")) normalValues = floats(firstDescendant(section, "float_array").getTextContent());
                else if (id.endsWith("mesh-map-0")) uvValues = floats(firstDescendant(section, "float_array").getTextContent());
                else if (section.getNodeName().equals("triangles")) for (int index : integers(firstDescendant(section, "p").getTextContent())) indices.add(index);
            }
            if (coordinates.length == 0) continue;
            List<Position> positions = new ArrayList<>();
            List<Normal> normals = new ArrayList<>();
            List<TexCoord> uvs = new ArrayList<>();
            List<Face> faces = new ArrayList<>();
            List<Triangle> triangles = new ArrayList<>();
            for (int i = 0; i < coordinates.length; i += 3) positions.add(new Position(coordinates[i], coordinates[i + 1], coordinates[i + 2], 1));
            for (int i = 0; i < normalValues.length; i += 3) normals.add(new Normal(normalValues[i], normalValues[i + 1], normalValues[i + 2]));
            for (int i = 0; i < uvValues.length; i += 2) uvs.add(new TexCoord(uvValues[i], flipV ? 1 - uvValues[i + 1] : uvValues[i + 1], 0));
            for (int i = 0; i < indices.size(); i += 9) {
                List<Corner> corners = new ArrayList<>();
                for (int offset = 0; offset < 9; offset += 3) corners.add(new Corner(indices.get(i + offset), indices.get(i + offset + 2), indices.get(i + offset + 1)));
                int faceIndex = faces.size();
                faces.add(new Face(corners, geometry.getAttribute("id"), List.of(), geometry.getAttribute("id"), "", "", normals.get(corners.get(0).normal()), -1));
                triangles.add(new Triangle(faceIndex, 0, 1, 2));
            }
            result.put(geometry.getAttribute("id"), new WavefrontMesh(positions, uvs, normals, faces, triangles, List.of(), List.of(), List.of()));
        }
        return result;
    }

    private static void attach(AnimatedModel model, Map<String, WavefrontMesh> geometry, AnimationController controller) {
        model.geometry = geometry.get(model.geo_name);
        model.hasGeometry = !model.geo_name.isEmpty() && model.geometry != null;
        model.controller = controller;
        for (AnimatedModel child : model.children) attach(child, geometry, controller);
    }

    private static Element output(Element root) {
        Element sampler = firstDescendant(root, "sampler");
        for (Element input : children(sampler)) {
            if (!input.getAttribute("semantic").equals("OUTPUT")) continue;
            String id = input.getAttribute("source").substring(1);
            for (Element child : children(root)) if (child.getAttribute("id").equals(id)) return child;
        }
        throw new IllegalArgumentException("Missing original animation OUTPUT source: " + root.getAttribute("id"));
    }
    private static Element firstDescendant(Element root, String name) { return (Element) root.getElementsByTagName(name).item(0); }
    private static List<Element> namedChildren(Element root, String name) {
        return children(root).stream().filter(element -> element.getNodeName().equals(name)).toList();
    }
    private static List<Element> children(Element root) {
        List<Element> result = new ArrayList<>();
        if (root != null) for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            Node child = root.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) result.add((Element) child);
        }
        return result;
    }
    private static float[] floats(String text) {
        if (text.isEmpty()) return new float[0];
        String[] tokens = text.split(" ");
        float[] result = new float[tokens.length];
        for (int i = 0; i < tokens.length; i++) result[i] = Float.parseFloat(tokens[i]);
        return result;
    }
    private static int[] integers(String text) {
        String[] tokens = text.split(" ");
        int[] result = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) result[i] = Integer.parseInt(tokens[i]);
        return result;
    }
}
