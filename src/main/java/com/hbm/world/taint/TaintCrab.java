package com.hbm.world.taint;

import com.hbm.entity.mob.crab.CyberCrab;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.exposure.ExposureEffects;
import com.hbm.machine.tesla.TeslaDischarge;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.rifle.RifleBallistics;
import java.util.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** Original Tesla aura,5-tick/50-block ranged AI and real existing7.62FMJ, inheriting the true CyberCrab base. */
public final class TaintCrab extends CyberCrab {
 public List<Vec3> targets=List.of();
 public TaintCrab(EntityType<? extends TaintCrab> type,Level level){super(type,level);noCulling=true;}
 public static AttributeSupplier.Builder attributes(){return CyberCrab.attributes().add(Attributes.MAX_HEALTH,25).add(Attributes.MOVEMENT_SPEED,.5);}
 @Override protected boolean usesPanicGoal(){return false;}
 @Override protected Goal rangedAttackGoal(){return rangedAttackGoal(.5,5,5,50);}
 @Override protected float deathExplosionStrength(){return 3;}
 @Override public void aiStep(){targets=TeslaDischarge.zap(level(),new Vec3(getX(),getY()+1.25,getZ()),10,this);for(var living:level().getEntitiesOfClass(LivingEntity.class,new AABB(getX()-5,getY()-5,getZ()-5,getX()+5,getY()+5,getZ()+5)))if(!(living instanceof CyberCrab)&&!(living instanceof Player player&&player.isSpectator()))living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION,10,15));super.aiStep();}
 @Override public void performRangedAttack(LivingEntity target,float strength){
  RifleBallistics.initialize();var bullet=new EntityBulletBaseMK4(this,RifleBallistics.r762_fmj,10,0,0,0,0);
  if(level() instanceof ServerLevel server){var motion=bullet.getDeltaMovement().scale(.3);server.sendParticles(ParticleTypes.FLAME,bullet.getX(),bullet.getY(),bullet.getZ(),0,motion.x,motion.y,motion.z,1);}
  level().addFreshEntity(bullet);playSound(HbmSounds.get("weapon.sawShoot"),1,.5F);
 }
}
