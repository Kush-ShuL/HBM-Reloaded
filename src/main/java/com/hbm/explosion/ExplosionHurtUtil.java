package com.hbm.explosion;
import com.hbm.exposure.HbmExposure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
public final class ExplosionHurtUtil {
 public static void doRadiation(Level world,double x,double y,double z,float outer,float inner,double radius){for(var living:world.getEntitiesOfClass(LivingEntity.class,new AABB(x-radius,y-radius,z-radius,x+radius,y+radius,z+radius))){double distance=(double)(float)Math.sqrt(living.distanceToSqr(x,y,z));if(distance>radius)continue;float dose=(float)(outer+(inner-outer)*(1-distance/radius));HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,dose);}}
 private ExplosionHurtUtil(){}
}
