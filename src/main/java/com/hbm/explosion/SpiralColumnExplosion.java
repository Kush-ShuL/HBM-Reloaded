package com.hbm.explosion;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/** Original MK3 square spiral, retaining its exact column order while a native tick can yield mid-column. */
abstract class SpiralColumnExplosion {
 public int posX,posY,posZ,lastposX,lastposZ,radius,radius2;
 public Level worldObj;
 public float explosionCoefficient=1F,explosionCoefficient2=1F;
 public int type;
 private int n=1,nlimit,shell,leg,element,columnY;
 private float bottom;
 private boolean column;
 public boolean yielded;
 private final NuclearChunkAccess chunks=new NuclearChunkAccess();
 public void close(){chunks.close();}
 protected final BlockPos.MutableBlockPos cursor=new BlockPos.MutableBlockPos();
 protected SpiralColumnExplosion(int x,int y,int z,Level world,int radius,float coefficient,float coefficient2){posX=x;posY=y;posZ=z;worldObj=world;this.radius=radius;radius2=radius*radius;nlimit=radius2*4;explosionCoefficient=coefficient;explosionCoefficient2=coefficient2;}
 protected abstract int startY(int distance);
 protected abstract float bottomY(int distance);
 /** Number of additional cells skipped by the original protection return value. */
 protected abstract int process(int y,BlockPos pos);
 public boolean update(){return updateUntil(Long.MAX_VALUE);}
 public boolean updateUntil(long deadline){
  yielded=false;
  if(System.nanoTime()>=deadline){yielded=true;return false;}
  int square=radius2-(lastposX*lastposX+lastposZ*lastposZ);
  if(square>0){
   if(worldObj instanceof ServerLevel server&&chunks.getWithNeighbors(server,(posX+lastposX)>>4,(posZ+lastposZ)>>4)==null){yielded=true;return false;}
   if(!column){int distance=(int)Math.sqrt(square);columnY=startY(distance);bottom=bottomY(distance);column=true;}
   // Outside the native world every legacy operation sees air and consumes no random values.
   columnY=Math.min(columnY,worldObj.getMaxBuildHeight()-1-posY);
   while(columnY>bottom&&posY+columnY>=worldObj.getMinBuildHeight()){
    if(System.nanoTime()>=deadline){yielded=true;return false;}
    cursor.set(posX+lastposX,posY+columnY,posZ+lastposZ);
    columnY-=process(columnY,cursor);columnY--;
   }
  }
  column=false;
  shell=(int)Math.floor((Math.sqrt(n)+1)/2);int shell2=shell*2;if(shell2==0){close();return true;}
  leg=(int)Math.floor((n-(shell2-1)*(shell2-1))/shell2);
  element=(n-(shell2-1)*(shell2-1))-shell2*leg-shell+1;
  lastposX=leg==0?shell:leg==1?-element:leg==2?-shell:element;
  lastposZ=leg==0?element:leg==1?shell:leg==2?-element:-shell;
  n++;boolean complete=n>nlimit;if(complete)close();return complete;
 }
 public void saveToNbt(CompoundTag tag,String name){
  tag.putInt(name+"posX",posX);tag.putInt(name+"posY",posY);tag.putInt(name+"posZ",posZ);tag.putInt(name+"lastposX",lastposX);tag.putInt(name+"lastposZ",lastposZ);tag.putInt(name+"radius",radius);tag.putInt(name+"radius2",radius2);tag.putInt(name+"n",n);tag.putInt(name+"nlimit",nlimit);tag.putInt(name+"shell",shell);tag.putInt(name+"leg",leg);tag.putInt(name+"element",element);tag.putFloat(name+"explosionCoefficient",explosionCoefficient);tag.putFloat(name+"explosionCoefficient2",explosionCoefficient2);tag.putInt(name+"type",type);
  tag.putBoolean(name+"hbmColumn",column);tag.putInt(name+"hbmColumnY",columnY);tag.putFloat(name+"hbmBottom",bottom);
 }
 public void readFromNbt(CompoundTag tag,String name){
  posX=tag.getInt(name+"posX");posY=tag.getInt(name+"posY");posZ=tag.getInt(name+"posZ");lastposX=tag.getInt(name+"lastposX");lastposZ=tag.getInt(name+"lastposZ");radius=tag.getInt(name+"radius");radius2=tag.getInt(name+"radius2");n=tag.getInt(name+"n");nlimit=tag.getInt(name+"nlimit");shell=tag.getInt(name+"shell");leg=tag.getInt(name+"leg");element=tag.getInt(name+"element");explosionCoefficient=tag.getFloat(name+"explosionCoefficient");if(tag.contains(name+"explosionCoefficient2"))explosionCoefficient2=tag.getFloat(name+"explosionCoefficient2");type=tag.getInt(name+"type");column=tag.getBoolean(name+"hbmColumn");columnY=tag.getInt(name+"hbmColumnY");bottom=tag.getFloat(name+"hbmBottom");
 }
}
