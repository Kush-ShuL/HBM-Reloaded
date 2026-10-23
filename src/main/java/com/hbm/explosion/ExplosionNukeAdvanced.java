package com.hbm.explosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
/** Original MK3's three independent spiral shells, including protection-based vertical skips. */
public final class ExplosionNukeAdvanced extends SpiralColumnExplosion {
 public ExplosionNukeAdvanced(int x,int y,int z,Level world,int radius,float coefficient,int type){super(x,y,z,world,radius,Math.min(Math.max((radius+coefficient*(y-60))/(coefficient*radius),1/coefficient),1F),1F);this.type=type;}
 @Override protected int startY(int distance){return distance;}
 @Override protected float bottomY(int distance){return -distance*explosionCoefficient;}
 @Override protected int process(int y,BlockPos pos){return switch(type){case 0->{int protection=ExplosionNukeGeneric.destruction(worldObj,pos.getX(),pos.getY(),pos.getZ());yield y<8?protection:0;}case 1->ExplosionNukeGeneric.vaporDest(worldObj,pos.getX(),pos.getY(),pos.getZ());case 2->{if(radius>=95)ExplosionNukeGeneric.wasteDest(worldObj,pos);else ExplosionNukeGeneric.wasteDestNoSchrab(worldObj,pos);yield 0;}default->0;};}
}
