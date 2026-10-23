package com.hbm.entity.mob.botprime;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.mob.maskman.LegacyNpcBullets;
import com.hbm.entity.projectile.EntityBulletBaseNT;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;
/** Real BurrowingNT/WormBaseNT/BOTPrimeBase path: independent physical mobs sharing damage through their predecessor chain. */
public abstract class BotPrimeBase extends PathfinderMob implements LegacyTrackingRange {
 public int attackCounter,aggroCooldown,courseChangeCooldown,damageCooldown;public double waypointX,waypointY,waypointZ;protected LivingEntity targeted,followed;protected boolean wasNearGround,didCheck;protected BlockPos spawnPoint=BlockPos.ZERO;protected int partNumber;private UUID headUUID;private int legacyHeadID;protected final BotPrimeTargeting targeting=new BotPrimeTargeting(this);
 protected BotPrimeBase(EntityType<? extends BotPrimeBase> type,Level world){super(type,world);noPhysics=true;setNoGravity(true);}
 public static AttributeSupplier.Builder attributes(boolean head){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,15000).add(Attributes.KNOCKBACK_RESISTANCE,1).add(Attributes.MOVEMENT_SPEED,head?.15:.7).add(Attributes.FOLLOW_RANGE,16);}
 @Override public boolean shouldRenderAtSqrDistance(double distance){double range=getBoundingBox().getSize()*64*15;return distance<range*range;}
 @Override protected void registerGoals(){}@Override public int legacyTrackingRange(){return 1000;}public abstract boolean isHead();public abstract float contactDamage(Entity target);
 public void head(BotPrimeHead head){headUUID=head.getUUID();legacyHeadID=head.getId();}public UUID headUUID(){return isHead()?getUUID():headUUID;}public int headID(){return isHead()?getId():legacyHeadID;}public boolean sameHead(BotPrimeBase other){return headUUID()!=null&&headUUID().equals(other.headUUID())||headUUID()==null&&other.headUUID()==null&&headID()==other.headID();}
 public int partNumber(){return partNumber;}public void partNumber(int n){partNumber=n;}public Entity getHead(){if(level() instanceof ServerLevel server&&headUUID()!=null)return server.getEntity(headUUID());return level().getEntity(legacyHeadID);}
 @Override public boolean hurt(DamageSource source,float amount){if(isInvulnerableTo(source)||source.is(DamageTypes.DROWN)||source.is(DamageTypes.IN_WALL)||source.getEntity() instanceof BotPrimeBase other&&sameHead(other))return false;hurtMarked=true;if(isHead())return super.hurt(source,amount);return targeted!=null?targeted.hurt(source,amount):super.hurt(source,amount);}
 protected void wormActionState(){if(level().getDifficulty()==Difficulty.PEACEFUL)discard();if(targeted!=null&&targeted.isRemoved())targeted=null;var motion=getDeltaMovement();int min=level().getMinBuildHeight();if(getY()<min-10)setDeltaMovement(motion.x,1,motion.z);else if(getY()<min+3)setDeltaMovement(motion.x,.3,motion.z);if(tickCount%5==0)for(var target:level().getEntities(this,getBoundingBox().inflate(.5)))if(target instanceof LivingEntity&&(!(target instanceof BotPrimeBase other)||!sameHead(other)))doHurtTarget(target);}
 @Override public boolean doHurtTarget(Entity target){boolean hit=target.hurt(damageSources().mobAttack(this),contactDamage(target));if(hit){setNoActionTime(0);double x=target.getX()-(getBoundingBox().minX+getBoundingBox().maxX)/2,z=target.getZ()-(getBoundingBox().minZ+getBoundingBox().maxZ)/2,y=target.getY()-(getBoundingBox().minY+getBoundingBox().maxY)/2,force=x*x+y*y+z*z+.1;target.push(x/force,y/force,z/force);}return hit;}
 @Override public void push(double x,double y,double z){}@Override public void lookAt(Entity entity,float yaw,float pitch){}@Override public boolean onClimbable(){return false;}@Override protected void checkFallDamage(double delta,boolean ground,net.minecraft.world.level.block.state.BlockState state,BlockPos p){}@Override public boolean causeFallDamage(float distance,float multiplier,DamageSource source){return false;}@Override public void checkDespawn(){}
 @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}
 /** Source isEntityInsideOpaqueBlock's8eye samples,not a collision-box/material surrogate. */
 protected boolean insideOpaque(){if(noPhysics){/* Source still samples opaque blocks even for noClip. */}float eye=getEyeHeight();for(int i=0;i<8;i++){double x=getX()+((i>>0)%2-.5F)*getBbWidth()*.8F,y=getY()+eye+((i>>1)%2-.5F)*.1F,z=getZ()+((i>>2)%2-.5F)*getBbWidth()*.8F;var p=BlockPos.containing(x,y,z);var s=level().getBlockState(p);if(s.isSuffocating(level(),p))return true;}return false;}
 @Override public void travel(Vec3 controls){float drag=!insideOpaque()&&!isInWater()&&!isInLava()?.995F:.98F;if(!isHead())drag*=.9F;float strafe=(float)controls.x,forward=(float)controls.z,norm=strafe*strafe+forward*forward;if(norm>=1E-4F){norm=Mth.sqrt(norm);if(norm<1)norm=1;norm=.02F/norm;strafe*=norm;forward*=norm;float sin=Mth.sin(getYRot()*(float)Math.PI/180),cos=Mth.cos(getYRot()*(float)Math.PI/180);setDeltaMovement(getDeltaMovement().add(strafe*cos-forward*sin,0,forward*cos+strafe*sin));}move(MoverType.SELF,getDeltaMovement());setDeltaMovement(getDeltaMovement().scale(drag));}
 protected boolean courseTraversable(){return insideOpaque();}
 protected boolean seeThroughNonSolids(Entity target){var hit=level().clip(new ClipContext(new Vec3(getX(),getY()+getEyeHeight(),getZ()),new Vec3(target.getX(),target.getY()+target.getEyeHeight(),target.getZ()),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));return hit.getType()==net.minecraft.world.phys.HitResult.Type.MISS;}
 protected void laserAttack(Entity target,boolean head){if(!(target instanceof LivingEntity living))return;if(head){for(int i=0;i<5;i++)level().addFreshEntity(new EntityBulletBaseNT(level(),LegacyNpcBullets.WORM_LASER,this,living,1,i*.05F));playSound(HbmSounds.get("weapon.ballsLaser"),5,.75F);}else{level().addFreshEntity(new EntityBulletBaseNT(level(),LegacyNpcBullets.WORM_BOLT,this,living,.5F,.125F));playSound(HbmSounds.get("weapon.ballsLaser"),5,1);}}
 protected void updateBodyMovement(){if(targeted!=null&&targeted.distanceToSqr(this)<128*128){waypointX=targeted.getX();waypointY=targeted.getY();waypointZ=targeted.getZ();}if((tickCount%60==0||tickCount==1)&&(targeted==null||followed==null))findPredecessor();double x=waypointX-getX(),y=waypointY-getY(),z=waypointZ-getZ(),length=(float)Math.sqrt(x*x+y*y+z*z),speed=Math.max(0,Math.min(length-3.5,1.4));if(length<3.5*.895)setDeltaMovement(getDeltaMovement().scale(.8));else setDeltaMovement(x/length*speed,y/length*speed,z/length*speed);}
 protected void findPredecessor(){if(getHead() instanceof BotPrimeHead head){followed=head;legacyHeadID=head.getId();if(partNumber==0)targeted=head;}for(var entity:level().getEntitiesOfClass(BotPrimeBase.class,getBoundingBox().inflate(70))){if(!sameHead(entity))continue;if(entity.isHead()){if(partNumber==0)targeted=entity;followed=entity;legacyHeadID=entity.getId();}else if(entity.partNumber()==partNumber-1)targeted=entity;}didCheck=true;}
 protected void faceMotion(Vec3 motion){float horizontal=(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z);yRotO=(float)(Math.atan2(motion.x,motion.z)*180/Math.PI);xRotO=(float)(Math.atan2(motion.y,horizontal)*180/Math.PI);setYRot(yRotO);setXRot(xRotO);yBodyRot=yBodyRotO=yRotO;}
 @Override protected SoundEvent getHurtSound(DamageSource source){return SoundEvents.BLAZE_HURT;}@Override protected SoundEvent getDeathSound(){return HbmSounds.get("entity.bombDet");}@Override protected float getSoundVolume(){return 5;}
 @Override public void remove(RemovalReason reason){if(reason.shouldDestroy()&&!isRemoved())playSound(getDeathSound(),getSoundVolume(),getVoicePitch());super.remove(reason);}
 @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("wormID",headID());if(headUUID()!=null)tag.putUUID("hbm:head_uuid",headUUID());}
 @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);legacyHeadID=tag.getInt("wormID");headUUID=tag.hasUUID("hbm:head_uuid")?tag.getUUID("hbm:head_uuid"):null;}
}
