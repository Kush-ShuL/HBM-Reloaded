package com.hbm.entity.grenade.disperser;

import com.hbm.bomb.conventional.ConventionalContent;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.effect.EntityMist;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.fluid.*;
import com.hbm.item.disperser.DisperserContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** Original vanilla-Throwable grenade branch, using the existing exact shared simulation; only the source spin/impact extension lives here. */
public final class EntityDisperserCanister extends EntityThrowableNT implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> FLUID=SynchedEntityData.defineId(EntityDisperserCanister.class,EntityDataSerializers.INT),ITEM=SynchedEntityData.defineId(EntityDisperserCanister.class,EntityDataSerializers.INT);
    public EntityDisperserCanister(EntityType<? extends EntityDisperserCanister> type,Level level){super(type,level);}
    public EntityDisperserCanister(Level level){this(DisperserContent.ENTITY.get(),level);}
    public EntityDisperserCanister(Level level,double x,double y,double z){this(level);setPos(x,y,z);}
    public EntityDisperserCanister(Level level,LivingEntity owner){this(level);setThrower(owner);setYRot(owner.getYRot());setXRot(owner.getXRot());float yaw=getYRot()/180F*(float)Math.PI,pitch=getXRot()/180F*(float)Math.PI;setPos(owner.getX()-(double)(Mth.cos(yaw)*.16F),owner.getY()+owner.getEyeHeight()-.10000000149011612D,owner.getZ()-(double)(Mth.sin(yaw)*.16F));float x=-Mth.sin(yaw)*Mth.cos(pitch)*.4F,z=Mth.cos(yaw)*Mth.cos(pitch)*.4F,y=-Mth.sin(pitch)*.4F;setThrowableHeading(x,y,z,1.5F,1F);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(FLUID,0);builder.define(ITEM,0);}
    public EntityDisperserCanister setFluid(int id){entityData.set(FLUID,id);return this;}public HbmFluidType getFluid(){return HbmFluids.fromID(entityData.get(FLUID));}
    /** Named separately because modern Entity.getType returns EntityType. This is the same original raw item watcher. */
    public EntityDisperserCanister setSourceItem(Item item){entityData.set(ITEM,Item.getId(item));return this;}public Item getSourceItem(){return Item.byId(entityData.get(ITEM));}
    @Override public int legacyTrackingRange(){return 250;}@Override protected boolean sourceVanillaThrowable(){return true;}@Override protected double sourceRotationPi(){return (double)(float)Math.PI;}@Override protected double headingForceMult(){return (double).0075F;}@Override public double getGravityVelocity(){return (double).03F;}
    @Override public void tick(){super.tick();xRotO=getXRot();setXRot((float)(getXRot()-getDeltaMovement().length()*25));setYRot((float)(Math.atan2(getDeltaMovement().x,getDeltaMovement().z)*180D/Math.PI));while(getYRot()-yRotO< -180)yRotO-=360;while(getYRot()-yRotO>=180)yRotO+=360;setYRot(yRotO+(getYRot()-yRotO)*.2F);}
    @Override protected void onImpact(HitResult hit){if(hit instanceof EntityHitResult entity)entity.getEntity().hurt(damageSources().thrown(this,getThrower()),0);if(!level().isClientSide&&com.hbm.logging.SourceActionLog.enabled())com.hbm.HbmReloaded.LOGGER.info("[GREN] Set off grenade at {} / {} / {} by {}!",(int)getX(),(int)getY(),(int)getZ(),getThrower() instanceof Player player?player.getDisplayName().getString():"null");explode();}
    public void explode(){if(!level().isClientSide){var mist=new EntityMist(level());mist.setType(getFluid());mist.setPos(getX(),getY(),getZ());mist.setArea(10,5);mist.setDuration(80);level().addFreshEntity(mist);discard();}}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("fluid",entityData.get(FLUID));tag.putInt("item",entityData.get(ITEM));tag.putString("hbm_item",BuiltInRegistries.ITEM.getKey(getSourceItem()).toString());}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);entityData.set(FLUID,tag.getInt("fluid"));if(tag.contains("hbm_item")){var id=ResourceLocation.parse(tag.getString("hbm_item"));if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Missing actual saved disperser item "+id);entityData.set(ITEM,Item.getId(BuiltInRegistries.ITEM.get(id)));}else entityData.set(ITEM,tag.getInt("item"));}
}
