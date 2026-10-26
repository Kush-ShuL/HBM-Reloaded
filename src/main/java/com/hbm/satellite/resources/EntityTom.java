package com.hbm.satellite.resources;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Source EntityTom motion/chime/contact; the old EntityThrowable impact method is deliberately empty. */
public final class EntityTom extends Entity implements LegacyTrackingRange {
    public EntityTom(EntityType<?> type,Level level){super(type,level);noCulling=true;}
    public EntityTom(Level level){this(SatelliteResources113.TOM.get(),level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){}
    @Override public void tick(){
        xo=xOld=getX();yo=yOld=getY();zo=zOld=getZ();var motion=getDeltaMovement();setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);
        if(tickCount%100==0)level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("alarm.chime"),SoundSource.NEUTRAL,10000,1);
        setDeltaMovement(getDeltaMovement().x,-.5,getDeltaMovement().z);
        // Source casts to int, not floor, including negative X/Z.
        if(!level().getBlockState(new BlockPos((int)getX(),(int)getY(),(int)getZ())).isAir()||getY()<level().getMinBuildHeight()+10){
            if(!level().isClientSide){var blast=new EntityTomBlast(level());blast.setPos(position());blast.destructionRange=600;level().addFreshEntity(blast);var cloud=new EntityCloudTom(level(),500);cloud.setPos(position());level().addFreshEntity(cloud);}discard();
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){}
    @Override protected void readAdditionalSaveData(CompoundTag tag){}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<500000;}
    @Override public int legacyTrackingRange(){return 1000;}
}
