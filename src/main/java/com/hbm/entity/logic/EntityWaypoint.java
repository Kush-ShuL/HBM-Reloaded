package com.hbm.entity.logic;
import com.hbm.entity.mob.glyphid.*;
import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
/** Actual source task marker, including its one-time additional waypoint and short-lived nearby-task routing. */
public final class EntityWaypoint extends Entity implements com.hbm.entity.LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> TYPE=SynchedEntityData.defineId(EntityWaypoint.class,EntityDataSerializers.INT);
    public int maxAge=2400,radius=3;public boolean highPriority;
    protected EntityWaypoint additional;private boolean hasSpawned;
    public EntityWaypoint(EntityType<? extends EntityWaypoint> type,Level world){super(type,world);noPhysics=true;}
    public EntityWaypoint(Level world){this(GlyphidContent.WAYPOINT.get(),world);}
    @Override public int legacyTrackingRange(){return 250;}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(TYPE,0);}
    public int getWaypointType(){return entityData.get(TYPE);}
    public void setWaypointType(int value){entityData.set(TYPE,value);}
    public void setAdditionalWaypoint(EntityWaypoint waypoint){additional=waypoint;}
    public void setHighPriority(){highPriority=true;}
    public int getColor(){return switch(getWaypointType()){case EntityGlyphid.TASK_RETREAT_FOR_REINFORCEMENTS->0x5fa6e8;case EntityGlyphid.TASK_BUILD_HIVE,EntityGlyphid.TASK_INITIATE_RETREAT->0x127766;default->0x566573;};}
    @Override public void tick(){
        if(tickCount>=maxAge)discard();var box=new AABB(getX(),getY(),getZ(),getX(),getY(),getZ()).inflate(radius);
        if(!level().isClientSide){if(tickCount%40==0)for(var entity:level().getEntities(this,box))if(entity instanceof EntityGlyphid glyphid){
            if(additional!=null&&!hasSpawned){level().addFreshEntity(additional);hasSpawned=true;}
            boolean exceptions=glyphid.getWaypoint()!=this||glyphid.isScout()||glyphid.isNuclearGlyphid();
            if(!exceptions)glyphid.setCurrentTask(getWaypointType(),additional);
            if(getWaypointType()!=EntityGlyphid.TASK_BUILD_HIVE||glyphid.isScout())discard();
        }}else if(GlyphidConfig.WAYPOINT_DEBUG.get()){
            double x=box.minX+(random.nextDouble()-.5)*(box.maxX-box.minX),y=box.minY+random.nextDouble()*(box.maxY-box.minY),z=box.minZ+(random.nextDouble()-.5)*(box.maxZ-box.minZ);
            LegacyFluidEffects.tower(level(),x,y,z,new LegacyFluidEffects.Tower(.5F,.75F,2F,50+level().random.nextInt(10)).color(getColor()));
        }
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){setWaypointType(tag.getInt("type"));}
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putInt("type",getWaypointType());}
}
