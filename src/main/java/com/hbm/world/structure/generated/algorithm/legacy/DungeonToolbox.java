package com.hbm.world.structure.generated.algorithm.legacy;
import com.hbm.world.structure.generated.compat.*;
import java.util.*;
/** Complete used source overloads; ore and flower helpers are owned by their actual separate features. */
public final class DungeonToolbox {
 public static void generateBox(World world,int x,int y,int z,int sx,int sy,int sz,List<MetaBlock> blocks){if(blocks.isEmpty())return;for(int i=x;i<x+sx;i++)for(int j=y;j<y+sy;j++)for(int k=z;k<z+sz;k++){MetaBlock b=getRandom(blocks,world.rand);world.setBlock(i,j,k,b.block,b.meta,2);}}
 public static void generateBox(World world,int x,int y,int z,int sx,int sy,int sz,Block block){generateBox(world,x,y,z,sx,sy,sz,new MetaBlock(block));}
 public static void generateBox(World world,int x,int y,int z,int sx,int sy,int sz,MetaBlock block){for(int i=x;i<x+sx;i++)for(int j=y;j<y+sy;j++)for(int k=z;k<z+sz;k++)world.setBlock(i,j,k,block.block,block.meta,2);}
 public static void generateBox(World world,int x,int y,int z,Vec3 size,List<MetaBlock> blocks){generateBox(world,x,y,z,(int)size.xCoord,(int)size.yCoord,(int)size.zCoord,blocks);}
 public static <T>T getRandom(List<T> list,Random random){return list.isEmpty()?null:list.get(random.nextInt(list.size()));}
}
