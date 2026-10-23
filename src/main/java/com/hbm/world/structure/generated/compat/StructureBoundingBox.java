package com.hbm.world.structure.generated.compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
/** Inclusive integer source geometry, converted only at the native persistent Piece boundary. */
public final class StructureBoundingBox {
    public int minX,minY,minZ,maxX,maxY,maxZ;
    public StructureBoundingBox(int x0,int y0,int z0,int x1,int y1,int z1){minX=x0;minY=y0;minZ=z0;maxX=x1;maxY=y1;maxZ=z1;}
    public StructureBoundingBox(BoundingBox box){this(box.minX(),box.minY(),box.minZ(),box.maxX(),box.maxY(),box.maxZ());}
    public boolean isVecInside(int x,int y,int z){return x>=minX&&x<=maxX&&y>=minY&&y<=maxY&&z>=minZ&&z<=maxZ;}
    public boolean intersectsWith(StructureBoundingBox b){return maxX>=b.minX&&minX<=b.maxX&&maxY>=b.minY&&minY<=b.maxY&&maxZ>=b.minZ&&minZ<=b.maxZ;}
    public void offset(int x,int y,int z){minX+=x;maxX+=x;minY+=y;maxY+=y;minZ+=z;maxZ+=z;}
    public int getYSize(){return maxY-minY+1;}
    public BoundingBox nativeBox(){return new BoundingBox(minX,minY,minZ,maxX,maxY,maxZ);}
    public int[] coordinates(){return new int[]{minX,minY,minZ,maxX,maxY,maxZ};}
}
