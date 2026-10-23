package com.hbm.explosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
public final class ExplosionSolinium extends SpiralColumnExplosion {
 public ExplosionSolinium(int x,int y,int z,Level world,int radius,float coefficient,float coefficient2){super(x,y,z,world,radius,coefficient,coefficient2);}
 @Override protected int startY(int distance){return (int)(distance/explosionCoefficient2);}
 @Override protected float bottomY(int distance){return -distance/explosionCoefficient;}
 @Override protected int process(int y,BlockPos pos){ExplosionNukeGeneric.solinium(worldObj,pos.getX(),pos.getY(),pos.getZ());return 0;}
}
