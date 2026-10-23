package com.hbm.block.pipe.client;

import com.hbm.HbmReloaded;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;

/** RenderBlocks-style bounds, per-face texture crop, quarter rotations and original face shades. */
final class PipeCubeRenderer {
    private PipeCubeRenderer() {}
    static TextureAtlasSprite sprite(String path) { return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(HbmReloaded.id("block/" + path)); }
    static void cube(PoseStack poses, VertexConsumer out, int light, int overlay, float x0, float y0, float z0, float x1, float y1, float z1,
                     java.util.function.Function<Direction, TextureAtlasSprite> sprites, int rgb, int[] rotations, boolean shade) {
        for (Direction face : Direction.values()) {
            float[][] points = switch (face) {
                case DOWN -> new float[][]{{x0,y0,z1},{x0,y0,z0},{x1,y0,z0},{x1,y0,z1}};
                case UP -> new float[][]{{x0,y1,z0},{x0,y1,z1},{x1,y1,z1},{x1,y1,z0}};
                case NORTH -> new float[][]{{x1,y1,z0},{x1,y0,z0},{x0,y0,z0},{x0,y1,z0}};
                case SOUTH -> new float[][]{{x0,y1,z1},{x0,y0,z1},{x1,y0,z1},{x1,y1,z1}};
                case WEST -> new float[][]{{x0,y1,z0},{x0,y0,z0},{x0,y0,z1},{x0,y1,z1}};
                case EAST -> new float[][]{{x1,y1,z1},{x1,y0,z1},{x1,y0,z0},{x1,y1,z0}};
            };
            float factor = !shade ? 1F : switch (face) { case DOWN -> 0.5F; case UP -> 1F; case NORTH, SOUTH -> 0.8F; default -> 0.6F; };
            int color = 0xFF000000 | (int)((rgb >> 16 & 255)*factor) << 16 | (int)((rgb >> 8 & 255)*factor) << 8 | (int)((rgb & 255)*factor);
            var sprite = sprites.apply(face);
            for (float[] point : points) {
                int rotation=rotations==null?0:rotations[face.get3DDataValue()];
                float[] uv=LegacyBoxUvs.project(face,point[0],point[1],point[2],x0,y0,z0,x1,y1,z1,rotation);
                out.addVertex(poses.last(), point[0], point[1], point[2]).setColor(color).setUv(sprite.getU(uv[0]), sprite.getV(uv[1])).setOverlay(overlay).setLight(light).setNormal(poses.last(), face.getStepX(), face.getStepY(), face.getStepZ());
            }
        }
    }
}
