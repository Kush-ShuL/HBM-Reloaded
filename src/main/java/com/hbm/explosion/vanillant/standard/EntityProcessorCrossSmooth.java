package com.hbm.explosion.vanillant.standard;

import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.damage.SednaDamageSource;
import com.hbm.explosion.vanillant.ExplosionVNT;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** X5846's fixed-damage cross sampling, including self damage and its density threshold. */
public class EntityProcessorCrossSmooth extends EntityProcessorCross {
 protected float fixedDamage,pierceDT,pierceDR;
 protected DamageClass clazz=DamageClass.EXPLOSIVE;
 public EntityProcessorCrossSmooth(double nodeDist,float fixedDamage){super(nodeDist);this.fixedDamage=fixedDamage;setAllowSelfDamage();}
 public EntityProcessorCrossSmooth setupPiercing(float pierceDT,float pierceDR){this.pierceDT=pierceDT;this.pierceDR=pierceDR;return this;}
 public EntityProcessorCrossSmooth setDamageClass(DamageClass clazz){this.clazz=clazz;return this;}
 @Override public void attackEntity(Entity entity,ExplosionVNT source,float amount){
  if(!entity.isAlive())return;if(source.exploder==entity)amount*=.5F;
  var damage=SednaDamageSource.create(source.world,null,source.exploder instanceof LivingEntity living?living:null,clazz);
  if(entity instanceof LivingEntity living){LegacyEntityDamage.attackEntityFromNT(living,damage,amount,true,false,0F,pierceDT,pierceDR);if(!living.isAlive())com.hbm.explosion.ExplosionDeathEffects.decide(living,damage);}else entity.hurt(damage,amount);
 }
 @Override public float calculateDamage(double distanceScaled,double density,double knockback,float size){return density<.125?0:(float)(fixedDamage*(1-distanceScaled));}
}
