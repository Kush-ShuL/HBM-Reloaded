package com.hbm.bomb.nuclear;
import net.minecraft.util.Mth;
/** Mutable Vec3 primitive from the actual 1.7.10 convection simulation. */
public final class LegacyVector {
 public double xCoord,yCoord,zCoord;
 private LegacyVector(double x,double y,double z){xCoord=x;yCoord=y;zCoord=z;}
 public static LegacyVector createVectorHelper(double x,double y,double z){return new LegacyVector(x,y,z);}
 public double lengthVector(){return (double)(float)Math.sqrt(xCoord*xCoord+yCoord*yCoord+zCoord*zCoord);}
 public LegacyVector normalize(){double length=lengthVector();return length<1E-4D?createVectorHelper(0,0,0):createVectorHelper(xCoord/length,yCoord/length,zCoord/length);}
 public void rotateAroundY(float angle){float c=Mth.cos(angle),s=Mth.sin(angle);double x=xCoord*(double)c+zCoord*(double)s,z=zCoord*(double)c-xCoord*(double)s;xCoord=x;zCoord=z;}
 public void rotateAroundZ(float angle){float c=Mth.cos(angle),s=Mth.sin(angle);double x=xCoord*(double)c+yCoord*(double)s,y=yCoord*(double)c-xCoord*(double)s;xCoord=x;yCoord=y;}
}
