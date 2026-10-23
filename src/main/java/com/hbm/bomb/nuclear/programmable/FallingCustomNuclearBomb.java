package com.hbm.bomb.nuclear.programmable;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

/** EntityFallingNuke's original non-swept falling motion, point block-impact trigger and seven persistent yields. */
public final class FallingCustomNuclearBomb extends Entity implements LegacyTrackingRange {
 private static final EntityDataAccessor<Byte> META=SynchedEntityData.defineId(FallingCustomNuclearBomb.class,EntityDataSerializers.BYTE);
 private CustomBombPayload payload=CustomBombPayload.ZERO;
 public FallingCustomNuclearBomb(EntityType<? extends FallingCustomNuclearBomb> type,Level level){super(type,level);noCulling=true;}
 public FallingCustomNuclearBomb(Level level,CustomBombPayload payload){this(ProgrammableNuclearContent.FALLING.get(),level);this.payload=payload;yRotO=90;setYRot(90);xRotO=90;setXRot(90);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(META,(byte)0);}
 public int metadata(){return entityData.get(META);}public void metadata(int value){entityData.set(META,(byte)value);}
 @Override public int legacyTrackingRange(){return 1000;}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
 @Override public void tick(){xo=getX();yo=getY();zo=getZ();xOld=getX();yOld=getY();zOld=getZ();var motion=getDeltaMovement();setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);setDeltaMovement(motion.x*.99,Math.max(motion.y-.05D,-1),motion.z*.99);xRotO=getXRot();if(getXRot()>-75)setXRot(getXRot()-2);if(!level().isClientSide&&!level().getBlockState(blockPosition()).isAir()){CustomNuclearExplosion.explode(level(),getX(),getY(),getZ(),payload);discard();}}
 @Override protected void addAdditionalSaveData(CompoundTag tag){payload.save(tag);tag.putByte("hbmMetadata",(byte)metadata());}
 @Override protected void readAdditionalSaveData(CompoundTag tag){payload=CustomBombPayload.load(tag);metadata(tag.getByte("hbmMetadata"));}
}
