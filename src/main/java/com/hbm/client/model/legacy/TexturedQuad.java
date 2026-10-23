package com.hbm.client.model.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/** Independent adapter for the exact 1.7.10 TexturedQuad vertex/UV/normal algorithm. */
public final class TexturedQuad {
    public PositionTextureVertex[] vertexPositions;
    public final int nVertices;
    private boolean invertNormal;

    public TexturedQuad(PositionTextureVertex[] vertices) {
        vertexPositions = vertices;
        nVertices = vertices.length;
    }

    public TexturedQuad(PositionTextureVertex[] vertices, int u1, int v1, int u2, int v2,
                        float textureWidth, float textureHeight) {
        this(vertices);
        // Official 1.7.10 uses 0.0F / textureSize here, unlike Forge OBJ's 0.0005F inset.
        float insetU = 0.0F / textureWidth;
        float insetV = 0.0F / textureHeight;
        vertices[0] = vertices[0].setTexturePosition(u2 / textureWidth - insetU, v1 / textureHeight + insetV);
        vertices[1] = vertices[1].setTexturePosition(u1 / textureWidth + insetU, v1 / textureHeight + insetV);
        vertices[2] = vertices[2].setTexturePosition(u1 / textureWidth + insetU, v2 / textureHeight - insetV);
        vertices[3] = vertices[3].setTexturePosition(u2 / textureWidth - insetU, v2 / textureHeight - insetV);
    }

    public void flipFace() {
        PositionTextureVertex[] reversed = new PositionTextureVertex[vertexPositions.length];
        for (int index = 0; index < reversed.length; index++) reversed[index] = vertexPositions[reversed.length - index - 1];
        vertexPositions = reversed;
    }

    public void draw(LegacyModelRenderContext context, float compiledScale) {
        PositionTextureVertex a = vertexPositions[0], b = vertexPositions[1], c = vertexPositions[2];
        // TexturedQuad computes (v1-v2) cross (v1-v0), then Vec3.normalize.
        double ax = (double) b.x() - c.x(), ay = (double) b.y() - c.y(), az = (double) b.z() - c.z();
        double bx = (double) b.x() - a.x(), by = (double) b.y() - a.y(), bz = (double) b.z() - a.z();
        double nx = ay * bz - az * by, ny = az * bx - ax * bz, nz = ax * by - ay * bx;
        double length = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length < 1.0E-4) nx = ny = nz = 0;
        else { nx /= length; ny /= length; nz /= length; }
        if (invertNormal) { nx = -nx; ny = -ny; nz = -nz; }
        PoseStack.Pose pose = context.poses().last();
        VertexConsumer consumer = context.vertices();
        for (PositionTextureVertex vertex : vertexPositions) {
            consumer.addVertex(pose, vertex.x() * compiledScale, vertex.y() * compiledScale, vertex.z() * compiledScale)
                    .setColor(context.argb()).setUv(vertex.texturePositionX(), vertex.texturePositionY())
                    .setOverlay(context.overlay()).setLight(context.light())
                    .setNormal(pose, (float) nx, (float) ny, (float) nz);
        }
    }
}
