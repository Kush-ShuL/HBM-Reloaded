package com.hbm.satellite.resources;

import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

/** Source watcher16 maxAge and independently incremented age; only age is saved, as in X5846. */
public final class EntityCloudTom extends Entity implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> MAX_AGE=SynchedEntityData.defineId(EntityCloudTom.class,EntityDataSerializers.INT);
    public int age;
    public EntityCloudTom(EntityType<?> type,Level level){super(type,level);noCulling=true;}
    public EntityCloudTom(Level level,int maxAge){this(SatelliteResources113.CLOUD.get(),level);setMaxAge(maxAge);refreshDimensions();}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){data.define(MAX_AGE,0);}
    public int getMaxAge(){return entityData.get(MAX_AGE);}public void setMaxAge(int value){entityData.set(MAX_AGE,value);}
    @Override public EntityDimensions getDimensions(Pose pose){return getMaxAge()==0?EntityDimensions.fixed(1,4):EntityDimensions.fixed(20,40);}
    @Override public void tick(){age++;level().setSkyFlashTime(2);if(age>=getMaxAge())discard();}
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putShort("age",(short)age);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){age=tag.getShort("age");}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return true;}
    @Override public int legacyTrackingRange(){return 1000;}
}
