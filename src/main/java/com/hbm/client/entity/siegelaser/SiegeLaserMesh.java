package com.hbm.client.entity.siegelaser;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
/** Generated directly from actual source Tessellator commands: same triangle/quad order and per-vertex alpha. */
final class SiegeLaserMesh {
    private static final float[][] TRIANGLES={{6.0F,0.0F,0.0F,255F},{3.0F,-1.0F,-1.0F,0F},{3.0F,1.0F,-1.0F,0F},{3.0F,-1.0F,1.0F,0F},{6.0F,0.0F,0.0F,255F},{3.0F,1.0F,1.0F,0F},{3.0F,-1.0F,-1.0F,0F},{6.0F,0.0F,0.0F,255F},{3.0F,-1.0F,1.0F,0F},{6.0F,0.0F,0.0F,255F},{3.0F,1.0F,-1.0F,0F},{3.0F,1.0F,1.0F,0F},{6.0F,0.0F,0.0F,255F},{4.0F,-0.5F,-0.5F,255F},{4.0F,0.5F,-0.5F,255F},{4.0F,-0.5F,0.5F,255F},{6.0F,0.0F,0.0F,255F},{4.0F,0.5F,0.5F,255F},{4.0F,-0.5F,-0.5F,255F},{6.0F,0.0F,0.0F,255F},{4.0F,-0.5F,0.5F,255F},{6.0F,0.0F,0.0F,255F},{4.0F,0.5F,-0.5F,255F},{4.0F,0.5F,0.5F,255F}};
    private static final float[][] QUADS={{4.0F,0.5F,-0.5F,255F},{4.0F,0.5F,0.5F,255F},{0.0F,0.5F,0.5F,0F},{0.0F,0.5F,-0.5F,0F},{4.0F,-0.5F,-0.5F,255F},{4.0F,-0.5F,0.5F,255F},{0.0F,-0.5F,0.5F,0F},{0.0F,-0.5F,-0.5F,0F},{4.0F,-0.5F,0.5F,255F},{4.0F,0.5F,0.5F,255F},{0.0F,0.5F,0.5F,0F},{0.0F,-0.5F,0.5F,0F},{4.0F,-0.5F,-0.5F,255F},{4.0F,0.5F,-0.5F,255F},{0.0F,0.5F,-0.5F,0F},{0.0F,-0.5F,-0.5F,0F}};
    static void draw(PoseStack poses,MultiBufferSource buffers,int color){emit(poses,buffers.getBuffer(SiegeLaserRenderTypes.TRIANGLES),TRIANGLES,color);emit(poses,buffers.getBuffer(SiegeLaserRenderTypes.QUADS),QUADS,color);}
    private static void emit(PoseStack poses,VertexConsumer output,float[][] vertices,int color){for(var vertex:vertices)output.addVertex(poses.last(),vertex[0],vertex[1],vertex[2]).setColor(((int)vertex[3]<<24)|(color&0xffffff));}
    private SiegeLaserMesh(){}
}
