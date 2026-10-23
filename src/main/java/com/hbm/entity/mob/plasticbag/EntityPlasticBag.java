package com.hbm.entity.mob.plasticbag;

import com.hbm.block.fluid.LegacyFluidBlock;
import com.hbm.block.fluid.LegacyFluidWorld;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.bomb.fishing.EntityItemBuoyant;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.explosion.LegacyExplosionMath;
import com.hbm.item.plasticbag.PlasticBagContent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Actual copied-squid source water mob: any real server attack releases one true buoyant215 bag. */
public final class EntityPlasticBag extends WaterAnimal {
    public float rotation,prevRotation;
    private float randomMotionSpeed,rotationVelocity,randomMotionVecX,randomMotionVecY,randomMotionVecZ;
    public EntityPlasticBag(EntityType<? extends EntityPlasticBag> type,Level level){super(type,level);rotationVelocity=1F/(random.nextFloat()+1F)*.2F;}
    @Override protected void registerGoals(){}
    @Override public boolean hurt(DamageSource source,float amount){
        if(!level().isClientSide){discard();spawnAtLocation(new ItemStack(PlasticBagContent.BAG.get()),0F);}
        return true;
    }
    @Override public ItemEntity spawnAtLocation(ItemStack stack,float offset){
        if(stack.isEmpty())return null;
        var drop=new EntityItemBuoyant(level(),getX(),getY()+(double)offset,getZ(),stack);
        drop.setPickUpDelay(10);
        var capture=captureDrops();if(capture!=null)capture.add(drop);else level().addFreshEntity(drop);
        return drop;
    }
    @Override protected SoundEvent getAmbientSound(){return null;}
    @Override protected SoundEvent getHurtSound(DamageSource source){return null;}
    @Override protected SoundEvent getDeathSound(){return null;}
    @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}
    // Official1.7WaterMob does not override Entity's true pushing flag;1.21WaterAnimal otherwise returnsfalse.
    @Override public boolean isPushedByFluid(){return true;}
    @Override protected void handleAirSupply(int previous){
        if(isAlive()&&!isInWater()){
            setAirSupply(previous-1);
            if(getAirSupply()==-20){setAirSupply(0);hurt(damageSources().drown(),2F);}
        }else setAirSupply(300);
    }
    @Override public boolean isInWater(){
        var box=getBoundingBox();
        // Keep raw old expand(0,-.6,0) bounds even when minY>maxY; a modern AABB would reorder them.
        int minX=Mth.floor(box.minX),maxX=Mth.floor(box.maxX+1D),minY=Mth.floor(box.minY+.6D),maxY=Mth.floor(box.maxY-.6D+1D),minZ=Mth.floor(box.minZ),maxZ=Mth.floor(box.maxZ+1D);
        // Source checkChunksExist was nonloading. Flow reads also need their actual adjacent columns ready.
        if(!level().hasChunksAt(new BlockPos(minX-1,minY-1,minZ-1),new BlockPos(maxX+1,maxY+1,maxZ+1)))return false;
        boolean water=false;double vx=0,vy=0,vz=0;
        for(int x=minX;x<maxX;x++)for(int y=minY;y<maxY;y++)for(int z=minZ;z<maxZ;z++){
            var pos=new BlockPos(x,y,z);var state=level().getBlockState(pos);
            if(!LegacyMaterials.of(state).name().equals("water"))continue;
            int meta=LegacyExplosionMath.metadata(state);if(meta>=8)meta=0;
            double surface=y+1D-(double)((meta+1)/9F);
            if((double)maxY<surface)continue;
            water=true;
            var flow=state.getBlock() instanceof LegacyFluidBlock fluid?fluid.getFlowVector(new LegacyFluidWorld(level()),x,y,z):state.getFluidState().getFlow(level(),pos);
            vx+=flow.x;vy+=flow.y;vz+=flow.z;
        }
        var flow=LegacyVector.createVectorHelper(vx,vy,vz);
        if(flow.lengthVector()>0){flow=flow.normalize();setDeltaMovement(getDeltaMovement().add(flow.xCoord*.014D,flow.yCoord*.014D,flow.zCoord*.014D));}
        return water;
    }
    @Override public void aiStep(){
        super.aiStep();prevRotation=rotation;rotation+=rotationVelocity;
        if(rotation>(float)Math.PI*2F){rotation-=(float)Math.PI*2F;if(random.nextInt(10)==0)rotationVelocity=1F/(random.nextFloat()+1F)*.2F;}
        if(isInWater()){
            float f;
            if(rotation<(float)Math.PI){f=rotation/(float)Math.PI;if((double)f>.75D)randomMotionSpeed=.1F;}
            else randomMotionSpeed*=.999F;
            if(!level().isClientSide)setDeltaMovement((double)(randomMotionVecX*randomMotionSpeed),(double)(randomMotionVecY*randomMotionSpeed),(double)(randomMotionVecZ*randomMotionSpeed));
            var motion=getDeltaMovement();f=(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z);
            yBodyRot+=(-(float)Math.atan2(motion.x,motion.z)*180F/(float)Math.PI-yBodyRot)*.1F;
            setYRot(yBodyRot);setXRot((float)(Math.atan2(motion.y,(double)f)*180D/Math.PI));
        }else if(!level().isClientSide){var motion=getDeltaMovement();setDeltaMovement(0,(motion.y-.08D)*.98D,0);}
    }
    @Override public void travel(Vec3 ignored){move(MoverType.SELF,getDeltaMovement());}
    @Override protected void customServerAiStep(){
        // Native Mob.serverAiStep already increments noActionTime exactly once before this hook.
        if(getNoActionTime()>100){randomMotionVecX=randomMotionVecY=randomMotionVecZ=0F;}
        else if(random.nextInt(50)==0||!super.isInWater()||randomMotionVecX==0F&&randomMotionVecY==0F&&randomMotionVecZ==0F){
            float f=random.nextFloat()*(float)Math.PI*2F;
            randomMotionVecX=Mth.cos(f)*.2F;randomMotionVecY=-.1F+random.nextFloat()*.2F;randomMotionVecZ=Mth.sin(f)*.2F;
        }
        // Existing native Mob handles distance/persistence despawning; no second age/death loop is added.
    }
}
