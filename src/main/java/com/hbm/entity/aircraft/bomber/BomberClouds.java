package com.hbm.entity.aircraft.bomber;
import com.hbm.entity.particle.EntityModFX;
import com.hbm.entity.particle.EntityOrangeFX;
import java.util.Random;
import net.minecraft.world.level.Level;
/** Only the actually consumed source ExplosionChaos.spawnPoisonCloud branches; one shared private RNG. */
public final class BomberClouds {
 private static final Random RANDOM=new Random();
 public static void spawn(Level level,double x,double y,double z,int count,double speed,boolean pink){
  for(int i=0;i<count;i++){
   EntityModFX cloud=pink?new EntityPinkCloudFX(level,x,y,z,0,0,0):new EntityOrangeFX(level,x,y,z,0,0,0);
   double my=RANDOM.nextGaussian()*speed,mx=RANDOM.nextGaussian()*speed,mz=RANDOM.nextGaussian()*speed;
   cloud.setDeltaMovement(mx,my,mz);level.addFreshEntity(cloud);
  }
 }
 private BomberClouds(){}
}
