package com.hbm.entity.boat;

import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** X5846 EntityBoatRubber: original single-seat, server-driven rudder and five-slice buoyancy. */
public final class EntityBoatRubber extends Entity implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> TIME=SynchedEntityData.defineId(EntityBoatRubber.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DIRECTION=SynchedEntityData.defineId(EntityBoatRubber.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE=SynchedEntityData.defineId(EntityBoatRubber.class,EntityDataSerializers.FLOAT);
    private boolean boatEmpty=true;
    private double speedMultiplier=.07D;
    private int positionIncrements;
    private double boatX,boatY,boatZ,boatYaw,boatPitch,velocityX,velocityY,velocityZ;
    public float prevRenderYaw;

    public EntityBoatRubber(EntityType<? extends EntityBoatRubber> type,Level level){super(type,level);blocksBuilding=true;}
    public EntityBoatRubber(Level level,double x,double y,double z){
        this(RubberBoatContent.ENTITY.get(),level);
        setPos(x,y+(double)(.6F/2F),z);setDeltaMovement(Vec3.ZERO);xo=x;yo=y;zo=z;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(TIME,0);builder.define(DIRECTION,1);builder.define(DAMAGE,0F);}
    /** Preserve the original logical posY/yOffset anchor instead of silently moving the old model/seat. */
    @Override protected AABB makeBoundingBox(){double half=getBbWidth()/2D,bottom=getY()-(double)(getBbHeight()/2F);return new AABB(getX()-half,bottom,getZ()-half,getX()+half,bottom+getBbHeight(),getZ()+half);}
    @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}
    @Override public int legacyTrackingRange(){return 250;}
    @Override public boolean isPickable(){return !isRemoved();}
    @Override public boolean isPushable(){return true;}
    @Override public boolean canBeCollidedWith(){return !isRemoved();}
    // Original getCollisionBox(other) returns that entity's actual bounding box, regardless of its own pushability.
    @Override public boolean canCollideWith(Entity other){return !isPassengerOfSameVehicle(other);}
    @Override protected boolean canAddPassenger(Entity passenger){return getPassengers().isEmpty();}
    // Do not grant local vehicle-position authority: native PlayerInput already supplies xxa/zza to the server.
    @Override public LivingEntity getControllingPassenger(){return null;}
    @Override public boolean hurt(DamageSource source,float amount){
        if(isInvulnerableTo(source))return false;
        if(!level().isClientSide&&!isRemoved()){
            setForwardDirection(-getForwardDirection());setTimeSinceHit(10);setDamageTaken(getDamageTaken()+amount*10F);markHurt();
            boolean creative=source.getEntity() instanceof Player player&&player.getAbilities().instabuild;
            if(creative||getDamageTaken()>40F){ejectPassengers();if(!creative)dropBoat();discard();}
        }
        return true;
    }
    @Override public void animateHurt(float yaw){setForwardDirection(-getForwardDirection());setTimeSinceHit(10);setDamageTaken(getDamageTaken()*11F);}
    @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int increments){
        if(boatEmpty)positionIncrements=increments;
        else{double dx=x-getX(),dy=y-getY(),dz=z-getZ();if(dx*dx+dy*dy+dz*dz<=1D)return;positionIncrements=3;}
        boatX=x;boatY=y;boatZ=z;boatYaw=yaw;boatPitch=pitch;setDeltaMovement(velocityX,velocityY,velocityZ);
    }
    @Override public void lerpMotion(double x,double y,double z){velocityX=x;velocityY=y;velocityZ=z;setDeltaMovement(x,y,z);}
    @Override public double lerpTargetX(){return positionIncrements>0?boatX:getX();}
    @Override public double lerpTargetY(){return positionIncrements>0?boatY:getY();}
    @Override public double lerpTargetZ(){return positionIncrements>0?boatZ:getZ();}
    @Override public float lerpTargetXRot(){return positionIncrements>0?(float)boatPitch:getXRot();}
    @Override public float lerpTargetYRot(){return positionIncrements>0?(float)boatYaw:getYRot();}

    @Override public void tick(){
        super.tick();
        if(getTimeSinceHit()>0)setTimeSinceHit(getTimeSinceHit()-1);
        if(getDamageTaken()>0)setDamageTaken(getDamageTaken()-1F);
        xo=getX();yo=getY();zo=getZ();
        double submerged=0;var box=getBoundingBox();
        for(int i=0;i<5;i++){
            double lower=box.minY+(box.maxY-box.minY)*(double)i/5D-.125D;
            double upper=box.minY+(box.maxY-box.minY)*(double)(i+1)/5D-.125D;
            if(sourceWater(new AABB(box.minX,lower,box.minZ,box.maxX,upper,box.maxZ)))submerged+=1D/5D;
        }
        if(level().isClientSide&&boatEmpty){
            if(positionIncrements>0){
                double x=getX()+(boatX-getX())/positionIncrements,y=getY()+(boatY-getY())/positionIncrements,z=getZ()+(boatZ-getZ())/positionIncrements;
                setYRot((float)(getYRot()+Mth.wrapDegrees(boatYaw-getYRot())/positionIncrements));
                setXRot((float)(getXRot()+(boatPitch-getXRot())/positionIncrements));positionIncrements--;setPos(x,y,z);
            }else{setPos(position().add(getDeltaMovement()));if(onGround())setDeltaMovement(getDeltaMovement().scale(.5D));passiveDecelerate();}
        }else{
            var motion=getDeltaMovement();double mx=motion.x,my=motion.y,mz=motion.z;
            if(submerged<1D)my+=.04D*(submerged*2D-1D);else{if(my<0)my/=2D;my+=.007000000216066837D;}
            double previousSpeed=Math.sqrt(mx*mx+mz*mz);
            var rider=getFirstPassenger();
            if(rider instanceof LivingEntity living){
                if(living.zza!=0||living.xxa!=0){
                    var direction=new Vec3(0,0,1).yRot((float)(-(getYRot()+90)*Math.PI/180D));
                    mx+=direction.x*speedMultiplier*living.zza*.05D;mz+=direction.z*speedMultiplier*living.zza*.05D;
                    float previousYaw=getYRot();setYRot(getYRot()-living.xxa*3);
                    var turned=new Vec3(mx,0,mz).yRot((float)(-(getYRot()-previousYaw)*Math.PI/180D));mx=turned.x;mz=turned.z;
                    // Same existing modern tracker bridge used by source missiles for their forced lastYaw update.
                    if(level() instanceof ServerLevel server)server.getChunkSource().broadcastAndSend(this,new ClientboundMoveEntityPacket.Rot(getId(),(byte)Mth.floor(getYRot()*256F/360F),(byte)Mth.floor(getXRot()*256F/360F),onGround()));
                }
            }else{mx*=.95D;my*=.95D;mz*=.95D;}
            double speed=Math.sqrt(mx*mx+mz*mz);
            if(speed>.5D){double ratio=.5D/speed;mx*=ratio;mz*=ratio;speed=.5D;}
            if(speed>previousSpeed&&speedMultiplier<.5D){speedMultiplier+=(.5D-speedMultiplier)/50D;if(speedMultiplier>.5D)speedMultiplier=.5D;}
            else{speedMultiplier-=(speedMultiplier-.07D)/35D;if(speedMultiplier<.07D)speedMultiplier=.07D;}
            for(int index=0;index<4;index++){
                int x=Mth.floor(getX()+((double)(index%2)-.5D)*.8D),z=Mth.floor(getZ()+((double)(index/2)-.5D)*.8D);
                for(int dy=0;dy<2;dy++){
                    var pos=new BlockPos(x,Mth.floor(getY())+dy,z);var state=level().getBlockState(pos);
                    if(state.is(Blocks.SNOW)){level().removeBlock(pos,false);horizontalCollision=false;}
                    else if(state.is(Blocks.LILY_PAD)){level().destroyBlock(pos,true);horizontalCollision=false;}
                }
            }
            if(onGround()){mx*=.5D;my*=.5D;mz*=.5D;}
            setDeltaMovement(mx,my,mz);move(MoverType.SELF,getDeltaMovement());
            if(horizontalCollision&&previousSpeed>.2D)setDeltaMovement(getDeltaMovement().scale(.25D));else passiveDecelerate();
            setXRot(0);
            if(!(rider instanceof LivingEntity)){
                double yaw=getYRot(),dx=xo-getX(),dz=zo-getZ();
                if(dx*dx+dz*dz>.001D)yaw=(float)(Math.atan2(dz,dx)*180D/Math.PI);
                setYRot((float)(getYRot()+Mth.clamp(Mth.wrapDegrees(yaw-getYRot()),-20D,20D)));
            }
            setRot(getYRot(),getXRot());
            if(!level().isClientSide){
                for(var entity:level().getEntities(this,getBoundingBox().inflate(.2D,0,.2D)))
                    if(entity!=rider&&entity.isPushable()&&(entity instanceof EntityBoatRubber||entity instanceof Boat))entity.push(this);
                if(rider!=null&&!rider.isAlive())rider.stopRiding();
            }
        }
        double mx=xo-getX(),mz=zo-getZ(),speed=Math.sqrt(mx*mx+mz*mz);
        if(speed>.2625D){
            double cos=Math.cos(getYRot()*Math.PI/180D),sin=Math.sin(getYRot()*Math.PI/180D);
            for(double j=0;j<1D+speed*60D;j++){
                double offset=(double)(random.nextFloat()*2F-1F),side=(double)(random.nextInt(2)*2-1)*.7D,x,z;
                if(random.nextBoolean()){x=getX()-cos*offset*.8D+sin*side;z=getZ()-sin*offset*.8D-cos*side;}
                else{x=getX()+cos+sin*offset*.7D;z=getZ()+sin-cos*offset*.7D;}
                // Source world method is a server no-op too; preserve consumption of this entity RNG on both sides.
                level().addParticle(ParticleTypes.SPLASH,x,getY()-.125D,z,mx,.1D,mz);
            }
        }
    }
    /** Official1.7 material query tests occupied blocks, not liquid surface-height integration. */
    private boolean sourceWater(AABB box){
        var pos=new BlockPos.MutableBlockPos();
        for(int x=Mth.floor(box.minX);x<Mth.floor(box.maxX+1D);x++)for(int y=Mth.floor(box.minY);y<Mth.floor(box.maxY+1D);y++)for(int z=Mth.floor(box.minZ);z<Mth.floor(box.maxZ+1D);z++){
            pos.set(x,y,z);if(isSourceWater(level().getBlockState(pos)))return true;
        }
        return false;
    }
    private static boolean isSourceWater(BlockState state){return state.getBlock() instanceof LiquidBlock&&state.getFluidState().is(FluidTags.WATER);}
    private void passiveDecelerate(){setDeltaMovement(getDeltaMovement().multiply(.99D,.95D,.99D));}
    @Override protected void positionRider(Entity passenger,MoveFunction move){
        if(!hasPassenger(passenger))return;
        double dx=Math.cos(getYRot()*Math.PI/180D)*.4D,dz=Math.sin(getYRot()*Math.PI/180D)*.4D;
        // Old EntityPlayer.getYOffset=yOffset-.5; subtract its old yOffset again to obtain modern feet.
        move.accept(passenger,getX()+dx,getY()-.3D-(passenger instanceof Player?.5D:0D),getZ()+dz);
        if(passenger instanceof Player player)player.yBodyRot=Mth.wrapDegrees(getYRot()+90F);
    }
    @Override public InteractionResult interact(Player player,InteractionHand hand){
        var rider=getFirstPassenger();
        if(rider instanceof Player&&rider!=player)return InteractionResult.sidedSuccess(level().isClientSide);
        if(!level().isClientSide){if(rider==player)player.stopRiding();else{if(rider!=null)rider.stopRiding();player.startRiding(this);}}
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override protected void checkFallDamage(double fall,boolean grounded,BlockState state,BlockPos pos){
        if(grounded){
            if(fallDistance>5F){causeFallDamage(fallDistance,1F,damageSources().fall());if(!level().isClientSide&&!isRemoved()){discard();dropBoat();}fallDistance=0;}
        }else if(!isSourceWater(level().getBlockState(BlockPos.containing(getX(),getY()-1,getZ())))&&fall<0)fallDistance=(float)(fallDistance-fall);
    }
    public void dropBoat(){spawnAtLocation(new ItemStack(RubberBoatContent.ITEM.get()),0F);}
    public void setDamageTaken(float value){entityData.set(DAMAGE,value);}public float getDamageTaken(){return entityData.get(DAMAGE);}
    public void setTimeSinceHit(int value){entityData.set(TIME,value);}public int getTimeSinceHit(){return entityData.get(TIME);}
    public void setForwardDirection(int value){entityData.set(DIRECTION,value);}public int getForwardDirection(){return entityData.get(DIRECTION);}
    public void setIsBoatEmpty(boolean empty){boatEmpty=empty;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}
    @Override protected void readAdditionalSaveData(CompoundTag tag){}
}
