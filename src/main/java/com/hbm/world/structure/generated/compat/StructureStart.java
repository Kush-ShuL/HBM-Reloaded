package com.hbm.world.structure.generated.compat;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
public class StructureStart {
    public final List<StructureComponent> components=new ArrayList<>();protected StructureBoundingBox boundingBox;private final int chunkX,chunkZ;
    public StructureStart(){this(0,0);}public StructureStart(int x,int z){chunkX=x;chunkZ=z;}
    public int func_143019_e(){return chunkX;}public int func_143018_f(){return chunkZ;}
    public void updateBoundingBox(){if(components.isEmpty())return;int x0=Integer.MAX_VALUE,y0=x0,z0=x0,x1=Integer.MIN_VALUE,y1=x1,z1=x1;for(var piece:components){var box=piece.getBoundingBox();x0=Math.min(x0,box.minX);y0=Math.min(y0,box.minY);z0=Math.min(z0,box.minZ);x1=Math.max(x1,box.maxX);y1=Math.max(y1,box.maxY);z1=Math.max(z1,box.maxZ);}boundingBox=new StructureBoundingBox(x0,y0,z0,x1,y1,z1);}
    public void markAvailableHeight(World world,Random random,int padding){int limit=world.getSeaLevel()-padding,top=boundingBox.getYSize()+1;if(top<limit)top+=random.nextInt(limit-top);int offset=top-boundingBox.maxY;boundingBox.offset(0,offset,0);for(var piece:components)piece.getBoundingBox().offset(0,offset,0);}
    public void func_143017_b(CompoundTag tag){}public void func_143022_a(CompoundTag tag){}
}
