package com.hbm.world.structure.generated.compat;
/** Supplied-era mutable Vec3 Y rotation, retaining vanilla float sine-table precision. */
public final class Vec3 {public double xCoord,yCoord,zCoord;private Vec3(double x,double y,double z){xCoord=x;yCoord=y;zCoord=z;}public static Vec3 createVectorHelper(double x,double y,double z){return new Vec3(x,y,z);}public void rotateAroundY(float angle){float c=net.minecraft.util.Mth.cos(angle),s=net.minecraft.util.Mth.sin(angle);double x=xCoord*c+zCoord*s,z=zCoord*c-xCoord*s;xCoord=x;zCoord=z;}}
