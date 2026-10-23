package com.hbm.network.cargo;
/** Client effect dispatch only; source smoke and held-tool debug lines have no server-side particle simulation. */
public final class CargoVisuals {
 @FunctionalInterface public interface Smoke{void add(double x,double y,double z,double vx,double vy,double vz);}
 @FunctionalInterface public interface Line{void add(double x,double y,double z,double dx,double dy,double dz,int color);}
 public static Smoke smoke=(x,y,z,vx,vy,vz)->{};public static Line line=(x,y,z,dx,dy,dz,color)->{};public static java.util.function.Consumer<net.minecraft.core.BlockPos> dust=pos->{};private CargoVisuals(){}
}
