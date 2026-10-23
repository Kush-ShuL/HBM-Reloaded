package com.hbm.entity.mob.crab;

import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

/** Source EntityCyberCrab, retaining its independent ranged AI, Tau immunity and wet/fire self-destruction. */
public class CyberCrab extends Monster implements RangedAttackMob {
    public CyberCrab(EntityType<? extends CyberCrab> type,Level world){super(type,world);setPathfindingMalus(PathType.WATER,8);}
    public static AttributeSupplier.Builder attributes(){return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,4).add(Attributes.MOVEMENT_SPEED,.75).add(Attributes.FOLLOW_RANGE,16).add(Attributes.ATTACK_DAMAGE,2);}
    @Override protected void registerGoals(){if(usesPanicGoal())goalSelector.addGoal(0,new SourcePanic(this));goalSelector.addGoal(1,new RandomStrollGoal(this,.5));goalSelector.addGoal(4,rangedAttackGoal());targetSelector.addGoal(1,new NearestAttackableTargetGoal<>(this,Player.class,0,true,false,null));targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this,Mob.class,0,true,true,entity->!(entity instanceof CyberCrab||entity instanceof Creeper)));}
    protected boolean usesPanicGoal(){return true;}
    protected Goal rangedAttackGoal(){return rangedAttackGoal(.5,60,80,15);}
    protected final Goal rangedAttackGoal(double speed,int minimum,int maximum,float range){return new SourceRanged(this,speed,minimum,maximum,range);}
    protected float deathExplosionStrength(){return .1F;}
    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source,float amount){return!HbmDamageSources.getIsTau(source)&&super.hurt(source,amount);}
    @Override public int getMaxFallDistance(){return getTarget()==null?3:3+(int)(getHealth()-1);}
    @Override public void tick(){super.tick();if(isInWater()||isInWaterOrRain()||isOnFire())hurt(damageSources().generic(),10);if(getHealth()<=0){discard();level().explode(this,getX(),getY(),getZ(),deathExplosionStrength(),false,Level.ExplosionInteraction.NONE);}}
    @Override protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source){return HbmSounds.get("entity.cybercrab");}
    @Override protected SoundEvent getDeathSound(){return HbmSounds.get("entity.cybercrab");}
    @Override public boolean doHurtTarget(Entity target){return true;}
    @Override public void performRangedAttack(LivingEntity target,float strength){var bullet=new com.hbm.entity.projectile.EntityBullet(level(),this,target,1.6F,2);bullet.setIsCritical(true);bullet.setTau(true);bullet.damage=3;level().addFreshEntity(bullet);playSound(HbmSounds.get("weapon.sawShoot"),1,2);}
    /** Old EntityAIPanic: revenge target or fire, random5x4 destination, no modern freezing/water preference. */
    private static final class SourcePanic extends Goal{
        final CyberCrab mob;Vec3 destination;SourcePanic(CyberCrab mob){this.mob=mob;setFlags(EnumSet.of(Flag.MOVE));}
        @Override public boolean canUse(){if(mob.getLastHurtByMob()==null&&!mob.isOnFire())return false;destination=DefaultRandomPos.getPos(mob,5,4);return destination!=null;}
        @Override public void start(){mob.getNavigation().moveTo(destination.x,destination.y,destination.z,.75);}
        @Override public boolean canContinueToUse(){return!mob.getNavigation().isDone();}
    }
    /** Original ArrowAttack60..80/15 including continuous20-tick sight and the original unsaturated initial cooldown. */
    private static final class SourceRanged extends Goal{
        final CyberCrab mob;final double speed;final int minimum,maximum;final float range;final double rangeSquared;LivingEntity target;int cooldown=-1,seen;
        SourceRanged(CyberCrab mob,double speed,int min,int max,float range){this.mob=mob;this.speed=speed;minimum=min;maximum=max;this.range=range;rangeSquared=range*range;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
        @Override public boolean canUse(){var next=mob.getTarget();if(next==null)return false;target=next;return true;}
        @Override public boolean canContinueToUse(){return canUse()||!mob.getNavigation().isDone();}
        @Override public void stop(){target=null;seen=0;cooldown=-1;}
        @Override public boolean requiresUpdateEveryTick(){return true;}
        @Override public void tick(){if(target==null)return;double distance=mob.distanceToSqr(target.getX(),target.getBoundingBox().minY,target.getZ());boolean visible=mob.getSensing().hasLineOfSight(target);seen=visible?seen+1:0;if(distance<=rangeSquared&&seen>=20)mob.getNavigation().stop();else mob.getNavigation().moveTo(target,speed);mob.getLookControl().setLookAt(target,30,30);if(--cooldown==0){if(distance>rangeSquared||!visible)return;float ratio=(float)Math.sqrt(distance)/range;mob.performRangedAttack(target,Math.max(.1F,Math.min(1,ratio)));cooldown=(int)Math.floor(ratio*(maximum-minimum)+minimum);}else if(cooldown<0)cooldown=(int)Math.floor(Math.sqrt(distance)/range*(maximum-minimum)+minimum);}
    }
}
