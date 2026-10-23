package com.hbm.weapon.b92;
import com.hbm.bomb.nuclear.NuclearExplosionMK3;import com.hbm.bomb.nuclear.programmable.NuclearRainbowCloud;import net.minecraft.sounds.*;import net.minecraft.world.level.Level;
/** Original FLEIJA factory conflict/suppression behavior stays in the one real nuclear implementation. */
public final class B92Blast {
 public static void explode(Level level,double x,double y,double z,int radius){if(level.isClientSide)return;var explosion=NuclearExplosionMK3.statFacFleija(level,x,y,z,radius);if(explosion.isRemoved())return;level.playSound(null,x,y,z,SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,100,level.random.nextFloat()*.1F+.9F);level.addFreshEntity(explosion);var cloud=new NuclearRainbowCloud(level,radius);cloud.setPos(x,y,z);level.addFreshEntity(cloud);}
 private B92Blast(){}
}
