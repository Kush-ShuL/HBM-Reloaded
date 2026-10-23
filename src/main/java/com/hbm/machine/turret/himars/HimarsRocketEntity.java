package com.hbm.machine.turret.himars;
import api.hbm.entity.IRadarDetectable;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableInterp;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.world.chunk.TicketController;
/** Actual EntityArtilleryRocket; sole inherited throwable sweep/interpolation, original predictive and ballistic-arc guidance. */
public final class HimarsRocketEntity extends EntityThrowableInterp implements IRadarDetectable,LegacyTrackingRange {
 public static final TicketController TICKETS=new TicketController(HimarsTurretContent.id("himars_rocket"));
 private static final EntityDataAccessor<Integer> TYPE=SynchedEntityData.defineId(HimarsRocketEntity.class,EntityDataSerializers.INT);
 public Entity targetEntity;public Vec3 lastTargetPos;public int health=100;
 private boolean steering=true,targeting=true;private final double[][] targetMotion=new double[20][3];private ChunkPos forced;
 public HimarsRocketEntity(EntityType<? extends HimarsRocketEntity> type,Level level){super(type,level);noCulling=true;}
 public HimarsRocketEntity(Level level){this(HimarsTurretContent.ROCKET.get(),level);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(TYPE,0);}
 public HimarsRocketEntity setType(int type){entityData.set(TYPE,type);return this;}public int rocketType(){int type=entityData.get(TYPE);return type>=0&&type<8?type:0;}
 public HimarsRocketEntity setTarget(Entity target){targetEntity=target;return setTarget(target.getX(),target.getY()+target.getBbHeight()*.5,target.getZ());}
 public HimarsRocketEntity setTarget(double x,double y,double z){lastTargetPos=new Vec3(x,y,z);return this;}
 @Override public boolean hurt(DamageSource source,float amount){if(isInvulnerableTo(source))return false;if(health>0&&!level().isClientSide){health-=(int)amount;if(health<=0)setDead();}return true;}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return true;}@Override public int legacyTrackingRange(){return 1000;}
 @Override public void tick(){super.tick();if(!level().isClientSide&&!isRemoved()){if(lastTargetPos!=null){var delta=LegacyVector.createVectorHelper(lastTargetPos.x-getX(),lastTargetPos.y-getY(),lastTargetPos.z-getZ());var motion=getDeltaMovement();double momentum=Math.sqrt(motion.x*motion.x+motion.y*motion.y+motion.z*motion.z)*motionMult();if(delta.lengthVector()<=momentum*1.5)disableSteering(delta,momentum);else{if(targeting&&targetEntity!=null)predictTarget(targetEntity);if(steering)adjustCourse(10,15);}}forceCurrentChunk();}}
 /** Original RocketTargetingPredictive, including eta=distance-speed and its source row-reference shifting. */
 private void predictTarget(Entity target){var motion=getDeltaMovement();var speed=LegacyVector.createVectorHelper(motion.x,motion.y,motion.z);var delta=LegacyVector.createVectorHelper(target.getX()-getX(),target.getY()-getY(),target.getZ()-getZ());double eta=delta.lengthVector()-speed.lengthVector();var targetVelocity=target.getDeltaMovement();double mx=targetVelocity.x,my=targetVelocity.y,mz=targetVelocity.z;for(int i=1;i<20;i++){targetMotion[i-1]=targetMotion[i];mx+=targetMotion[i][0];my+=targetMotion[i][1];mz+=targetMotion[i][2];}targetMotion[19][0]=targetVelocity.x;targetMotion[19][1]=targetVelocity.y;targetMotion[19][2]=targetVelocity.z;if(eta<=1){setTarget(target.getX(),target.getY()+target.getBbHeight()*.5,target.getZ());return;}setTarget(target.getX()+mx/20*eta,target.getY()+target.getBbHeight()*.5+my/20*eta,target.getZ()+mz/20*eta);}
 /** Source yaw uses atan(x/z), not atan2; source modulo, float rotations and horizontal step count are retained. */
 private void adjustCourse(double speed,double maxTurn){var motion=getDeltaMovement();var direction=LegacyVector.createVectorHelper(motion.x,motion.y,motion.z).normalize();double horizontalMomentum=Math.sqrt(motion.x*motion.x+motion.z*motion.z),dx=lastTargetPos.x-getX(),dz=lastTargetPos.z-getZ(),horizontalDelta=Math.sqrt(dx*dx+dz*dz),steps=horizontalDelta/horizontalMomentum;var target=LegacyVector.createVectorHelper(dx,lastTargetPos.y-getY(),dz).normalize();double rocketYaw=yaw(direction),rocketPitch=pitch(direction),targetYaw=yaw(target),targetPitch=pitch(target),turnSpeed=Math.min(maxTurn,45/steps);if(steps<=1)turnSpeed=180;double deltaYaw=((targetYaw-rocketYaw)+180)%360-180,deltaPitch=((targetPitch-rocketPitch)+180)%360-180,turnYaw=Math.min(Math.abs(deltaYaw),turnSpeed)*Math.signum(deltaYaw),turnPitch=Math.min(Math.abs(deltaPitch),turnSpeed)*Math.signum(deltaPitch);var velocity=LegacyVector.createVectorHelper(speed,0,0);velocity.rotateAroundZ((float)-Math.toRadians(rocketPitch+turnPitch));velocity.rotateAroundY((float)Math.toRadians(rocketYaw+turnYaw+90));setDeltaMovement(velocity.xCoord,velocity.yCoord,velocity.zCoord);}
 private static double yaw(LegacyVector vec){return Math.toDegrees(Math.atan(vec.xCoord/vec.zCoord))+(vec.zCoord>=0?180:0);}private static double pitch(LegacyVector vec){return Math.toDegrees(Math.atan(vec.yCoord/Math.sqrt(vec.xCoord*vec.xCoord+vec.zCoord*vec.zCoord)));}
 public void disableSteering(LegacyVector delta,double momentum){if(steering||targeting){steering=targeting=false;var unit=delta.normalize();setDeltaMovement(unit.xCoord*momentum/motionMult(),unit.yCoord*momentum/motionMult(),unit.zCoord*momentum/motionMult());}}
 @Override protected void onImpact(HitResult hit){if(!level().isClientSide)HimarsRocketTypes.impact(rocketType(),this,hit);}
 @Override protected float getAirDrag(){return 1;}@Override public double getGravityVelocity(){return steering?0:.01;}@Override public int approachNum(){return 0;}
 @Override public RadarTargetType getTargetType(){return RadarTargetType.ARTILLERY;}
 @Override public void onAddedToLevel(){super.onAddedToLevel();forceCurrentChunk();}
 private void forceCurrentChunk(){if(!(level() instanceof ServerLevel server)||isRemoved())return;var next=chunkPosition();if(next.equals(forced))return;clearChunkLoader();TICKETS.forceChunk(server,this,next.x,next.z,true,true);forced=next;}
 public void clearChunkLoader(){if(forced!=null&&level() instanceof ServerLevel server)TICKETS.forceChunk(server,this,forced.x,forced.z,false,true);forced=null;}
 public void killAndClear(){setDead();clearChunkLoader();}
 @Override public void remove(RemovalReason reason){if(reason.shouldDestroy()||reason==RemovalReason.CHANGED_DIMENSION)clearChunkLoader();super.remove(reason);}
 @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);if(lastTargetPos==null)lastTargetPos=position();tag.putDouble("targetX",lastTargetPos.x);tag.putDouble("targetY",lastTargetPos.y);tag.putDouble("targetZ",lastTargetPos.z);tag.putInt("type",entityData.get(TYPE));}
 @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);lastTargetPos=new Vec3(tag.getDouble("targetX"),tag.getDouble("targetY"),tag.getDouble("targetZ"));entityData.set(TYPE,tag.getInt("type"));}
}
