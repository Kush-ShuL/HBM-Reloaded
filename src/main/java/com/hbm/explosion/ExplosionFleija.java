package com.hbm.explosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
/** Actual original Fleija ellipsoid; it deliberately ignores ordinary blast resistance. */
public final class ExplosionFleija extends SpiralColumnExplosion {
 public ExplosionFleija(int x,int y,int z,Level world,int radius,float coefficient,float coefficient2){super(x,y,z,world,radius,coefficient,coefficient2);}
 @Override protected int startY(int distance){return (int)(distance/explosionCoefficient2);}
 @Override protected float bottomY(int distance){return -distance/explosionCoefficient;}
 @Override protected int process(int y,BlockPos pos){if(pos.getY()>worldObj.getMinBuildHeight()&&!(worldObj.getBlockState(pos).getBlock() instanceof ExplosionFleijaExempt))worldObj.setBlock(pos,Blocks.AIR.defaultBlockState(),3);return 0;}
}
