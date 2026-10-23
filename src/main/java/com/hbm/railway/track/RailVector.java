package com.hbm.railway.track;

/** Exact mutable-era vector operations used by source rail math, including reversed subtract and table trig. */
public final class RailVector {
    public double xCoord,yCoord,zCoord;
    private RailVector(double x,double y,double z){xCoord=x==0?0:x;yCoord=y==0?0:y;zCoord=z==0?0:z;}
    public static RailVector createVectorHelper(double x,double y,double z){return new RailVector(x,y,z);}
    public RailVector subtract(RailVector other){return createVectorHelper(other.xCoord-xCoord,other.yCoord-yCoord,other.zCoord-zCoord);}
    public RailVector addVector(double x,double y,double z){return createVectorHelper(xCoord+x,yCoord+y,zCoord+z);}
    public double lengthVector(){return (float)Math.sqrt(xCoord*xCoord+yCoord*yCoord+zCoord*zCoord);}
    public RailVector normalize(){double length=lengthVector();return length<1.0E-4D?createVectorHelper(0,0,0):createVectorHelper(xCoord/length,yCoord/length,zCoord/length);}
    public void rotateAroundX(float angle){float cos=net.minecraft.util.Mth.cos(angle),sin=net.minecraft.util.Mth.sin(angle);double y=yCoord*cos+zCoord*sin,z=zCoord*cos-yCoord*sin;yCoord=y;zCoord=z;}
    public void rotateAroundY(float angle){float cos=net.minecraft.util.Mth.cos(angle),sin=net.minecraft.util.Mth.sin(angle);double x=xCoord*cos+zCoord*sin,z=zCoord*cos-xCoord*sin;xCoord=x;zCoord=z;}
    public net.minecraft.world.phys.Vec3 nativeVector(){return new net.minecraft.world.phys.Vec3(xCoord,yCoord,zCoord);}
}
