package com.hbm.client.model.legacy;

/** Coordinates are authored model units; texture positions are normalized exactly once. */
public record PositionTextureVertex(float x, float y, float z, float texturePositionX, float texturePositionY) {
    public PositionTextureVertex setTexturePosition(float u, float v) {
        return new PositionTextureVertex(x, y, z, u, v);
    }
}
