package com.hbm.bomb.conventional.grenade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** Source EntityFireLingering, which deliberately does not persist across world loads. */
public final class LingeringFire extends Entity {
    private static final EntityDataAccessor<Integer> TYPE=SynchedEntityData.defineId(LingeringFire.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> WIDTH=SynchedEntityData.defineId(LingeringFire.class,EntityDataSerializers.FLOAT),HEIGHT=SynchedEntityData.defineId(LingeringFire.class,EntityDataSerializers.FLOAT);
    private boolean initialized;public int maxAge=150;
    public LingeringFire(EntityType<? extends LingeringFire> type,Level level){super(type,level);initialized=true;}
    public LingeringFire(Level level){this(GrenadeContent.LINGERING.get(),level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(TYPE,0);builder.define(WIDTH,0F);builder.define(HEIGHT,0F);}
    public LingeringFire area(float width,float height){entityData.set(WIDTH,width);entityData.set(HEIGHT,height);return this;}
    public LingeringFire duration(int duration){maxAge=duration;return this;}
    public LingeringFire type(int type){entityData.set(TYPE,type);return this;}
    @Override public EntityDimensions getDimensions(Pose pose){return initialized?EntityDimensions.scalable(entityData.get(WIDTH),entityData.get(HEIGHT)):EntityDimensions.scalable(.6F,1.8F);}
    @Override public void tick(){float width=entityData.get(WIDTH),height=entityData.get(HEIGHT);if(getBbWidth()!=width||getBbHeight()!=height)refreshDimensions();setPos(getX(),getY(),getZ());int type=entityData.get(TYPE);
        if(!level().isClientSide){if(tickCount>=maxAge)discard();for(var entity:level().getEntities(this,new AABB(getX()-width/2,getY(),getZ()-width/2,getX()+width/2,getY()+height,getZ()+width/2))){if(entity instanceof LivingEntity living){var data=FireStatus.data(living);if(type==0&&data.fire<60)data.fire=60;if(type==2&&data.fire<300)data.fire=300;if(type==1&&data.balefire<100)data.balefire=100;if(type==4){if(data.blackFire<200)data.blackFire=200;else data.blackFire+=5;}}else entity.igniteForSeconds(4);}}
        else for(int i=0;i<(width>=5?2:1);i++){double x=getX()-width/2+random.nextDouble()*width,z=getZ()-width/2+random.nextDouble()*width;var top=new Vec3(x,getY()+height,z);var down=new Vec3(x,getY()-height,z);var hit=level().clip(new ClipContext(top,down,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));if(hit.getType()==HitResult.Type.BLOCK)down=hit.getLocation();if(type==0||type==2)FlameEffects.client(level(),x,down.y,z,0);if(type==1)FlameEffects.client(level(),x,down.y,z,1);if(type==4)FlameEffects.client(level(),x,down.y,z,4);}
    }
    @Override public boolean displayFireAnimation(){return false;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}
    @Override protected void readAdditionalSaveData(CompoundTag tag){discard();}
}
