package com.hbm.explosion.vanillant.standard;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.ICustomDamageHandler;
import com.hbm.exposure.HbmExposure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
public class CustomDamageHandlerAmat implements ICustomDamageHandler {
 protected float radiation;
 public CustomDamageHandlerAmat(float radiation){this.radiation=radiation;}
 @Override public void handleAttack(ExplosionVNT explosion,Entity entity,double distanceScaled){if(entity instanceof LivingEntity living)HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,(float)(radiation*(1D-distanceScaled)*explosion.size));}
}
