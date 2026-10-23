package com.hbm.client.cable;

import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

/** Source-sized block quads and authored OBJ faces, remapped onto the modern atlas. */
final class CableQuads {
    private static final int VERTEX_STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / Integer.BYTES;
    private CableQuads() {}
    static float[][] face(Direction side, float x0, float y0, float z0, float x1, float y1, float z1) {
        // Original RenderBlocksCT's TL/TR/BL/BR order, viewed from outside.
        return switch (side) {
            case DOWN -> new float[][]{{x0,y0,z1},{x1,y0,z1},{x0,y0,z0},{x1,y0,z0}};
            case UP -> new float[][]{{x0,y1,z0},{x1,y1,z0},{x0,y1,z1},{x1,y1,z1}};
            case NORTH -> new float[][]{{x1,y1,z0},{x0,y1,z0},{x1,y0,z0},{x0,y0,z0}};
            case SOUTH -> new float[][]{{x0,y1,z1},{x1,y1,z1},{x0,y0,z1},{x1,y0,z1}};
            case WEST -> new float[][]{{x0,y1,z0},{x0,y1,z1},{x0,y0,z0},{x0,y0,z1}};
            case EAST -> new float[][]{{x1,y1,z1},{x1,y1,z0},{x1,y0,z1},{x1,y0,z0}};
        };
    }
    static BakedQuad face(TextureAtlasSprite sprite, Direction side, float[][] points, float u0, float v0, float u1, float v1, int rotation) {
        float[][] uv = {{u0,v0},{u1,v0},{u0,v1},{u1,v1}};
        int[] order = {0,2,3,1};
        int[] data = new int[VERTEX_STRIDE * 4];
        int packedNormal = normal(side.getStepX(), side.getStepY(), side.getStepZ());
        for (int index = 0; index < 4; index++) {
            int point = order[index];
            int rotated = order[(index + rotation) % 4];
            vertex(data, index, points[point], sprite.getU(uv[rotated][0]), sprite.getV(uv[rotated][1]), -1, packedNormal);
        }
        return new BakedQuad(data, -1, side, sprite, true);
    }
    static void cube(List<BakedQuad> out, TextureAtlasSprite sprite, float x0, float y0, float z0, float x1, float y1, float z1) {
        for (Direction side : Direction.values()) croppedFace(out,sprite,side,x0,y0,z0,x1,y1,z1,0);
    }
    static void croppedFace(List<BakedQuad> out, TextureAtlasSprite sprite, Direction side, float x0, float y0, float z0, float x1, float y1, float z1, int rotation) {
        float[][] points=face(side,x0,y0,z0,x1,y1,z1);
        int[] order={0,2,3,1},data=new int[VERTEX_STRIDE*4];
        int packedNormal=normal(side.getStepX(),side.getStepY(),side.getStepZ());
        for(int index=0;index<4;index++) {
            float[] point=points[order[index]];
            float[] uv=com.hbm.block.pipe.client.LegacyBoxUvs.project(side,point[0],point[1],point[2],x0,y0,z0,x1,y1,z1,rotation);
            vertex(data,index,point,sprite.getU(uv[0]),sprite.getV(uv[1]),-1,packedNormal);
        }
        out.add(new BakedQuad(data,-1,side,sprite,true));
    }

    static void vertex(int[] data, int index, float[] pos, float u, float v, int color, int normal) {
        int at = index * VERTEX_STRIDE;
        data[at] = Float.floatToRawIntBits(pos[0]); data[at+1] = Float.floatToRawIntBits(pos[1]); data[at+2] = Float.floatToRawIntBits(pos[2]);
        data[at+3] = color; data[at+4] = Float.floatToRawIntBits(u); data[at+5] = Float.floatToRawIntBits(v);
        data[at+6] = 0; data[at+7] = normal;
    }
    private static int normal(float x, float y, float z) { return ((int)(x*127)&255) | (((int)(y*127)&255)<<8) | (((int)(z*127)&255)<<16); }
    static List<BakedQuad> obj(WavefrontMesh mesh, TextureAtlasSprite sprite, Predicate<WavefrontMesh.Face> selected, boolean inventory) {
        List<BakedQuad> out = new ArrayList<>();
        for (int faceIndex = 0; faceIndex < mesh.faces().size(); faceIndex++) {
            var face = mesh.faces().get(faceIndex);
            if (!selected.test(face)) continue;
            if (face.corners().size() == 4) out.add(objFace(mesh, face, sprite, new int[]{0,1,2,3}, inventory));
            else for (var tri : mesh.triangles()) if (tri.face() == faceIndex)
                out.add(objFace(mesh, face, sprite, new int[]{tri.first(),tri.second(),tri.third(),tri.third()}, inventory));
        }
        return out;
    }
    private static BakedQuad objFace(WavefrontMesh mesh, WavefrontMesh.Face face, TextureAtlasSprite sprite, int[] corners, boolean inventory) {
        int[] data = new int[VERTEX_STRIDE * 4];
        float nx = face.normal().x(), ny = face.normal().y(), nz = face.normal().z();
        if (inventory) { nx = -nx; nz = -nz; }
        float brightness = inventory ? 1 : Math.max(.45F, ny*.3F+.7F-Math.abs(nx)*.1F+Math.abs(nz)*.1F);
        int channel = (int)(brightness*255), color = 0xff000000 | channel<<16 | channel<<8 | channel;
        for (int i = 0; i < 4; i++) {
            var corner = face.corners().get(corners[i]);
            var p = mesh.positions().get(corner.position());
            float x = p.x(), y = p.y(), z = p.z();
            if (inventory) { x *= -1.25F; y *= 1.25F; z *= -1.25F; }
            float u = 0, v = 0;
            if (corner.texture() >= 0) { var uv = mesh.textureCoordinates().get(corner.texture()); u = uv.u(); v = 1-uv.v(); }
            vertex(data,i,new float[]{x+.5F,y+.5F,z+.5F},sprite.getU(u),sprite.getV(v),color,normal(nx,ny,nz));
        }
        return new BakedQuad(data,-1,Direction.getNearest(nx,ny,nz),sprite,false,false);
    }
    static float[] interpolate(float[][] face, float u, float v) {
        return new float[]{
            face[0][0]+(face[1][0]-face[0][0])*u+(face[2][0]-face[0][0])*v,
            face[0][1]+(face[1][1]-face[0][1])*u+(face[2][1]-face[0][1])*v,
            face[0][2]+(face[1][2]-face[0][2])*u+(face[2][2]-face[0][2])*v};
    }
}
