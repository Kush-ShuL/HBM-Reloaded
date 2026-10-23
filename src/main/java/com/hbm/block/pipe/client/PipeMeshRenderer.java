package com.hbm.block.pipe.client;

import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;

/** ObjUtil.renderPartWithIcon's flat normals, authored UVs and exact face-brightness formula. */
final class PipeMeshRenderer {
    private PipeMeshRenderer() {}
    static void render(WavefrontMesh mesh, PoseStack poses, VertexConsumer consumer, int light, int overlay, int rgb, boolean shadow, String... parts) {
        int triangle = 0;
        for (int faceIndex = 0; faceIndex < mesh.faces().size(); faceIndex++) {
            var face = mesh.faces().get(faceIndex); int firstTriangle = triangle;
            while (triangle < mesh.triangles().size() && mesh.triangles().get(triangle).face() == faceIndex) triangle++;
            if (Arrays.stream(parts).noneMatch(part -> part.equalsIgnoreCase(face.legacyPart()))) continue;
            var normal = face.normal();
            float brightness = shadow ? Math.max(0.45F, normal.y() * 0.3F + 0.7F - Math.abs(normal.x()) * 0.1F + Math.abs(normal.z()) * 0.1F) : 1F;
            // Legacy Tessellator.setColorRGBA clamps before packing; east-west top slopes exceed brightness 1.
            int red = Math.clamp((int) ((rgb >>> 16 & 255) * brightness), 0, 255);
            int green = Math.clamp((int) ((rgb >>> 8 & 255) * brightness), 0, 255);
            int blue = Math.clamp((int) ((rgb & 255) * brightness), 0, 255);
            int color = 0xFF000000 | red << 16 | green << 8 | blue;
            if (face.corners().size() == 4) {
                for (var corner : face.corners()) emit(mesh, corner, normal, poses.last(), consumer, light, overlay, color);
            } else for (int index = firstTriangle; index < triangle; index++) {
                var tri = mesh.triangles().get(index);
                emit(mesh, face.corners().get(tri.first()), normal, poses.last(), consumer, light, overlay, color);
                emit(mesh, face.corners().get(tri.second()), normal, poses.last(), consumer, light, overlay, color);
                emit(mesh, face.corners().get(tri.third()), normal, poses.last(), consumer, light, overlay, color);
                emit(mesh, face.corners().get(tri.third()), normal, poses.last(), consumer, light, overlay, color);
            }
        }
    }
    private static void emit(WavefrontMesh mesh, WavefrontMesh.Corner corner, WavefrontMesh.Normal normal, PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, int color) {
        var point = mesh.positions().get(corner.position());
        var uv = corner.texture() < 0 ? new WavefrontMesh.TexCoord(0, 1, 0) : mesh.textureCoordinates().get(corner.texture());
        consumer.addVertex(pose, point.x(), point.y(), point.z()).setColor(color).setUv(uv.u(), 1 - uv.v()).setLight(light).setOverlay(overlay).setNormal(pose, normal.x(), normal.y(), normal.z());
    }
}
