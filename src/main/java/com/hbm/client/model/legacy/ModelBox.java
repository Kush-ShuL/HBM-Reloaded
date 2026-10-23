package com.hbm.client.model.legacy;

/** Original six-face box topology and texture layout; does not use modern CubeListBuilder. */
public final class ModelBox {
    private final PositionTextureVertex[] vertexPositions;
    private final TexturedQuad[] quadList;
    public final float posX1, posY1, posZ1, posX2, posY2, posZ2;
    public String boxName;

    public ModelBox(ModelRenderer renderer, int textureX, int textureY,
                    float x, float y, float z, int width, int height, int depth, float inflation) {
        posX1 = x; posY1 = y; posZ1 = z;
        posX2 = x + width; posY2 = y + height; posZ2 = z + depth;
        float x2 = x + width, y2 = y + height, z2 = z + depth;
        x -= inflation; y -= inflation; z -= inflation;
        x2 += inflation; y2 += inflation; z2 += inflation;
        if (renderer.mirror) {
            float swap = x2;
            x2 = x;
            x = swap;
        }
        PositionTextureVertex v0 = new PositionTextureVertex(x, y, z, 0, 0);
        PositionTextureVertex v1 = new PositionTextureVertex(x2, y, z, 0, 8);
        PositionTextureVertex v2 = new PositionTextureVertex(x2, y2, z, 8, 8);
        PositionTextureVertex v3 = new PositionTextureVertex(x, y2, z, 8, 0);
        PositionTextureVertex v4 = new PositionTextureVertex(x, y, z2, 0, 0);
        PositionTextureVertex v5 = new PositionTextureVertex(x2, y, z2, 0, 8);
        PositionTextureVertex v6 = new PositionTextureVertex(x2, y2, z2, 8, 8);
        PositionTextureVertex v7 = new PositionTextureVertex(x, y2, z2, 8, 0);
        vertexPositions = new PositionTextureVertex[] {v0, v1, v2, v3, v4, v5, v6, v7};
        float tw = renderer.textureWidth, th = renderer.textureHeight;
        quadList = new TexturedQuad[] {
                new TexturedQuad(new PositionTextureVertex[] {v5, v1, v2, v6}, textureX + depth + width, textureY + depth, textureX + depth + width + depth, textureY + depth + height, tw, th),
                new TexturedQuad(new PositionTextureVertex[] {v0, v4, v7, v3}, textureX, textureY + depth, textureX + depth, textureY + depth + height, tw, th),
                new TexturedQuad(new PositionTextureVertex[] {v5, v4, v0, v1}, textureX + depth, textureY, textureX + depth + width, textureY + depth, tw, th),
                new TexturedQuad(new PositionTextureVertex[] {v2, v3, v7, v6}, textureX + depth + width, textureY + depth, textureX + depth + width + width, textureY, tw, th),
                new TexturedQuad(new PositionTextureVertex[] {v1, v0, v3, v2}, textureX + depth, textureY + depth, textureX + depth + width, textureY + depth + height, tw, th),
                new TexturedQuad(new PositionTextureVertex[] {v4, v5, v6, v7}, textureX + depth + width + depth, textureY + depth, textureX + depth + width + depth + width, textureY + depth + height, tw, th)
        };
        if (renderer.mirror) for (TexturedQuad quad : quadList) quad.flipFace();
    }

    public void render(LegacyModelRenderContext context, float compiledScale) {
        for (TexturedQuad quad : quadList) quad.draw(context, compiledScale);
    }

    public ModelBox setBoxName(String name) {
        boxName = name;
        return this;
    }

    public PositionTextureVertex[] vertices() { return vertexPositions.clone(); }
    public TexturedQuad[] quads() { return quadList.clone(); }
}
