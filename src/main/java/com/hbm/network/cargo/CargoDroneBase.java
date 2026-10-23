package com.hbm.network.cargo;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/** Actual nonliving EntityDroneBase: old Vec3 normalized steering, no extra gravity, original client lerp and4smoke outlets. */
public abstract class CargoDroneBase extends Entity implements LegacyTrackingRange {
 private static final EntityDataAccessor<Byte> APPEARANCE=SynchedEntityData.defineId(CargoDroneBase.class,EntityDataSerializers.BYTE);
 protected int turnProgress;protected double syncPosX,syncPosY,syncPosZ,velocityX,velocityY,velocityZ;public double targetX=-1,targetY=-1,targetZ=-1;
 protected CargoDroneBase(EntityType<?> type,Level level){super(type,level);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(APPEARANCE,(byte)0);}
 public void setTarget(double x,double y,double z){targetX=x;targetY=y;targetZ=z;}public int getAppearance(){return entityData.get(APPEARANCE);}public void setAppearance(int style){entityData.set(APPEARANCE,(byte)style);}
 @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}@Override public boolean isPickable(){return true;}@Override public boolean isAttackable(){return true;}@Override public boolean skipAttackInteraction(Entity attacker){if(attacker instanceof Player)discard();return false;}@Override public int legacyTrackingRange(){return 250;}
 @Override public void tick(){if(level().isClientSide){if(turnProgress>0){double x=getX()+(syncPosX-getX())/turnProgress,y=getY()+(syncPosY-getY())/turnProgress,z=getZ()+(syncPosZ-getZ())/turnProgress;--turnProgress;setPos(x,y,z);}else setPos(getX(),getY(),getZ());CargoVisuals.smoke.add(getX()+1.125,getY()+.75,getZ(),0,-.2,0);CargoVisuals.smoke.add(getX()-1.125,getY()+.75,getZ(),0,-.2,0);CargoVisuals.smoke.add(getX(),getY()+.75,getZ()+1.125,0,-.2,0);CargoVisuals.smoke.add(getX(),getY()+.75,getZ()-1.125,0,-.2,0);}else{double mx=0,my=0,mz=0;if(targetY!=-1){var distance=LegacyVector.createVectorHelper(targetX-getX(),targetY-getY(),targetZ-getZ());double speed=Math.min(getSpeed(),distance.lengthVector());distance=distance.normalize();mx=distance.xCoord*speed;my=distance.yCoord*speed;mz=distance.zCoord*speed;}if(horizontalCollision)my+=1;setDeltaMovement(mx,my,mz);loadNeighboringChunks();move(MoverType.SELF,getDeltaMovement());}super.tick();}
 protected void loadNeighboringChunks(){}public double getSpeed(){return .125D;}public boolean isRequestDrone(){return false;}
 @Override public void lerpMotion(double x,double y,double z){velocityX=x;velocityY=y;velocityZ=z;setDeltaMovement(x,y,z);}@Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int steps){syncPosX=x;syncPosY=y;syncPosZ=z;turnProgress=steps;setDeltaMovement(velocityX,velocityY,velocityZ);}
 @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putDouble("tX",targetX);tag.putDouble("tY",targetY);tag.putDouble("tZ",targetZ);tag.putByte("app",entityData.get(APPEARANCE));}@Override protected void readAdditionalSaveData(CompoundTag tag){if(tag.contains("tY")){targetX=tag.getDouble("tX");targetY=tag.getDouble("tY");targetZ=tag.getDouble("tZ");}entityData.set(APPEARANCE,tag.getByte("app"));}
 public static double sourceSpeed(Vec3 motion){return LegacyVector.createVectorHelper(motion.x,motion.y,motion.z).lengthVector();}
}
