package com.hbm.entity.effect;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.explosion.ThermalContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Original expanding ring: integer tick scale, watcher16 lifetime, and short age/scale persistence. */
public final class EntityEMPBlast extends Entity implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> MAX_AGE=SynchedEntityData.defineId(EntityEMPBlast.class,EntityDataSerializers.INT);
    public int age;
    public float scale;
    public EntityEMPBlast(EntityType<? extends EntityEMPBlast> type,Level level) { super(type,level); }
    public EntityEMPBlast(Level level,int maxAge) { this(ThermalContent.EMP.get(),level);setMaxAge(maxAge); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(MAX_AGE,0); }
    public void setMaxAge(int value) { entityData.set(MAX_AGE,value); }
    public int getMaxAge() { return entityData.get(MAX_AGE); }
    @Override public int legacyTrackingRange() { return 1000; }
    @Override public void tick() { age++;if(age>=getMaxAge()){age=0;discard();}scale++; }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { age=tag.getShort("age");scale=tag.getShort("scale"); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putShort("age",(short)age);tag.putShort("scale",(short)scale); }
}
