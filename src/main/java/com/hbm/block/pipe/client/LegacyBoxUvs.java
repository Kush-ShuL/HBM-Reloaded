package com.hbm.block.pipe.client;

import net.minecraft.core.Direction;

/** X5846 RenderBlocksNT plus official 1.7.10 RenderBlocks face projection, before atlas remapping. */
public final class LegacyBoxUvs {
    private LegacyBoxUvs() {}
    public static float[] project(Direction face,float x,float y,float z,float x0,float y0,float z0,float x1,float y1,float z1,int rotation) {
        if (face.getAxis()==Direction.Axis.Y) {
            if (rotation==3) return new float[]{1-x,1-z};
            if (rotation==1) return face==Direction.UP ? new float[]{z,1-x}:new float[]{1-z,x};
            if (rotation==2) return face==Direction.UP ? new float[]{1-z,x}:new float[]{z,1-x};
            return new float[]{x,z};
        }
        float axis=face.getAxis()==Direction.Axis.Z?x:z;
        boolean mirrored=face==Direction.NORTH||face==Direction.EAST;
        float minAxis=face.getAxis()==Direction.Axis.Z?x0:z0,maxAxis=face.getAxis()==Direction.Axis.Z?x1:z1;
        if(rotation==1) return mirrored ? new float[]{1-y0-y1+y,axis}:new float[]{y0+y1-y,1-axis};
        if(rotation==2) return mirrored ? new float[]{y0+y1-y,1-axis}:new float[]{1-y0-y1+y,axis};
        if(rotation==3) return new float[]{mirrored?1-minAxis-maxAxis+axis:1-axis,y};
        return new float[]{mirrored?1-axis:axis,1-y};
    }
}
