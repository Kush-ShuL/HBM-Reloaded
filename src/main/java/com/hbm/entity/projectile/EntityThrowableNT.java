package com.hbm.entity.projectile;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;

/** Original untangled throwable simulation, including swept entity penetration. */
public abstract class EntityThrowableNT extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Byte> STUCK=SynchedEntityData.defineId(EntityThrowableNT.class,EntityDataSerializers.BYTE);
    private BlockPos stuckBlockPos=new BlockPos(-1,-1,-1);
    private Block stuckBlock=Blocks.AIR;
    protected boolean inGround;
    public int throwableShake,ticksInGround,ticksInAir;
    protected LivingEntity thrower;
    private String throwerName;
    protected EntityThrowableNT(EntityType<?> type,Level level){super(type,level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(STUCK,(byte)0);}
    public void setStuckIn(int side){entityData.set(STUCK,(byte)side);}
    public int getStuckIn(){return entityData.get(STUCK);}
    @Override public boolean shouldRenderAtSqrDistance(double distance){double perimeter=getBoundingBox().getSize()*4*64;return distance<perimeter*perimeter;}
    /** Only the deprecated vanilla-Throwable descendants opt into their actual parent rules. */
    protected boolean sourceVanillaThrowable(){return false;}
    protected double sourceRotationPi(){return Math.PI;}
    protected double headingForceMult(){return .0075D;}
    protected double motionMult(){return 1;}
    public void setThrowableHeading(double x,double y,double z,float velocity,float inaccuracy){
        float length=(float)Math.sqrt(x*x+y*y+z*z);x/=length;y/=length;z/=length;
        x+=random.nextGaussian()*headingForceMult()*inaccuracy;y+=random.nextGaussian()*headingForceMult()*inaccuracy;z+=random.nextGaussian()*headingForceMult()*inaccuracy;
        x*=velocity;y*=velocity;z*=velocity;setDeltaMovement(x,y,z);
        float horizontal=(float)Math.sqrt(x*x+z*z);setYRot((float)(Math.atan2(x,z)*180/sourceRotationPi()));setXRot((float)(Math.atan2(y,horizontal)*180/sourceRotationPi()));yRotO=getYRot();xRotO=getXRot();ticksInGround=0;
    }
    @Override public void lerpMotion(double x,double y,double z){
        setDeltaMovement(x,y,z);
        if(xRotO==0&&yRotO==0){float horizontal=(float)Math.sqrt(x*x+z*z);setYRot((float)(Math.atan2(x,z)*180/sourceRotationPi()));setXRot((float)(Math.atan2(y,horizontal)*180/sourceRotationPi()));yRotO=getYRot();xRotO=getXRot();}
    }
    @Override public void tick(){
        super.tick();if(throwableShake>0)throwableShake--;boolean wasInGround=inGround;
        if(inGround){
            if(level().getBlockState(stuckBlockPos).getBlock()==stuckBlock){ticksInGround++;if(groundDespawn()>0&&ticksInGround==groundDespawn())setDead();return;}
            inGround=false;setDeltaMovement(getDeltaMovement().multiply(random.nextFloat()*.2F,random.nextFloat()*.2F,random.nextFloat()*.2F));ticksInGround=ticksInAir=0;
        }
        if(!wasInGround||sourceVanillaThrowable()){
            if(!wasInGround)ticksInAir++;Vec3 position=position(),next=position.add(getDeltaMovement().scale(motionMult()));HitResult impact=null;
            if(!isSpectral()){var block=level().clip(new ClipContext(position,next,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));if(block.getType()!=HitResult.Type.MISS)impact=block;}
            position=position();next=position.add(getDeltaMovement().scale(motionMult()));if(impact!=null)next=impact.getLocation();
            if(!level().isClientSide&&doesImpactEntities()){
                Entity hitEntity=null;Vec3 hitPoint=null;double nearest=0;
                List<Entity> candidates=level().getEntities(this,getBoundingBox().expandTowards(getDeltaMovement().scale(motionMult())).inflate(1));
                for(Entity entity:candidates)if(entity.isPickable()&&(entity!=getThrower()||ticksInAir>=selfDamageDelay())&&(sourceVanillaThrowable()||entity.isAlive())){
                    var hit=entityBoxIntercept(entity.getBoundingBox().inflate((double).3F),position,next);
                    if(hit.isPresent()){
                        if(doesPenetrate())onImpact(new EntityHitResult(entity,hit.get()));
                        else{double distance=position.distanceTo(hit.get());if(distance<nearest||nearest==0){hitEntity=entity;nearest=distance;hitPoint=hit.get();}}
                    }
                }
                if(!doesPenetrate()&&hitEntity!=null)impact=new EntityHitResult(hitEntity,hitPoint);
            }
            if(impact!=null){
                if(impact instanceof BlockHitResult block&&level().getBlockState(block.getBlockPos()).getBlock() instanceof Portal portal&&acceptsPortal(portal))setAsInsidePortal(portal,block.getBlockPos());
                else onImpact(impact);
            }
            if(pauseAfterImpact())return;
            continueSourceFlight();
        }
    }
    /** Default false: only a real descendant waiting for its source impact's FULL neighbours pauses this same flight step. */
    protected boolean pauseAfterImpact(){return false;}
    /** The unchanged original post-impact rotation/move/water/drag/gravity tail, also usable once after an asynchronous impact resumes. */
    protected final void continueSourceFlight(){
        if(sourceVanillaThrowable()||!onGround()){
            Vec3 motion=getDeltaMovement();float horizontal=(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z);setYRot((float)(Math.atan2(motion.x,motion.z)*180/sourceRotationPi()));setXRot((float)(Math.atan2(motion.y,horizontal)*180/sourceRotationPi()));
            while(getXRot()-xRotO< -180)xRotO-=360;while(getXRot()-xRotO>=180)xRotO+=360;while(getYRot()-yRotO< -180)yRotO-=360;while(getYRot()-yRotO>=180)yRotO+=360;
            setXRot(xRotO+(getXRot()-xRotO)*.2F);setYRot(yRotO+(getYRot()-yRotO)*.2F);
        }
        float drag=getAirDrag();double gravity=getGravityVelocity();Vec3 motion=getDeltaMovement();
        if(fullBlockCollisions())move(MoverType.SELF,motion.scale(motionMult()));else setPos(position().add(motion.scale(motionMult())));
        if(isInWater()){
            for(int i=0;i<4;i++)level().addParticle(ParticleTypes.BUBBLE,getX()-motion.x*.25F,getY()-motion.y*.25F,getZ()-motion.z*.25F,motion.x,motion.y,motion.z);
            drag=getWaterDrag();
        }
        setDeltaMovement(getDeltaMovement().scale(drag).add(0,-gravity,0));setPos(getX(),getY(),getZ());
    }
    /** Defaults preserve other real throwable families; a source core may supply its own exact contract. */
    protected java.util.Optional<Vec3> entityBoxIntercept(AABB box,Vec3 start,Vec3 end){return box.clip(start,end);}
    protected boolean acceptsPortal(Portal portal){return !sourceVanillaThrowable()||portal instanceof NetherPortalBlock;}
    public boolean fullBlockCollisions(){return false;}public boolean doesImpactEntities(){return true;}public boolean doesPenetrate(){return false;}public boolean isSpectral(){return false;}public int selfDamageDelay(){return 5;}
    public void getStuck(int x,int y,int z,int side){stuckBlockPos=new BlockPos(x,y,z);stuckBlock=level().getBlockState(stuckBlockPos).getBlock();inGround=true;setDeltaMovement(Vec3.ZERO);setStuckIn(side);forceTeleport();}
    public double getGravityVelocity(){return .03D;}
    protected abstract void onImpact(HitResult impact);
    protected void forceTeleport(){if(level() instanceof ServerLevel server)server.getChunkSource().broadcastAndSend(this,new ClientboundTeleportEntityPacket(this));}
    public void setDead(){discard();}
    @Override protected void addAdditionalSaveData(CompoundTag tag){
        // Stable block ids and full-height integer positions replace the old
        // numeric-byte registry and short coordinates in the changed world format.
        tag.putInt("xTile",stuckBlockPos.getX());tag.putInt("yTile",stuckBlockPos.getY());tag.putInt("zTile",stuckBlockPos.getZ());tag.putString("inTile",BuiltInRegistries.BLOCK.getKey(stuckBlock).toString());
        tag.putByte("shake",(byte)throwableShake);tag.putByte("inGround",(byte)(inGround?1:0));tag.putInt("ticksInGround",ticksInGround);tag.putInt("ticksInAir",ticksInAir);
        if((throwerName==null||throwerName.isEmpty())&&thrower instanceof Player player)throwerName=player.getGameProfile().getName();tag.putString("ownerName",throwerName==null?"":throwerName);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        stuckBlockPos=new BlockPos(tag.getInt("xTile"),tag.getInt("yTile"),tag.getInt("zTile"));var block=ResourceLocation.tryParse(tag.getString("inTile"));stuckBlock=block==null?Blocks.AIR:BuiltInRegistries.BLOCK.get(block);
        throwableShake=tag.getByte("shake")&255;inGround=tag.getByte("inGround")==1;throwerName=tag.getString("ownerName");ticksInGround=tag.getInt("ticksInGround");ticksInAir=tag.getInt("ticksInAir");if(throwerName.isEmpty())throwerName=null;
    }
    public void setThrower(LivingEntity entity){thrower=entity;}
    public LivingEntity getThrower(){if(thrower==null&&throwerName!=null)for(Player player:level().players())if(player.getGameProfile().getName().equals(throwerName)){thrower=player;break;}return thrower;}
    @Override public LivingEntity getOwner(){return getThrower();}
    protected float getAirDrag(){return .99F;}protected float getWaterDrag(){return .8F;}protected int groundDespawn(){return 1200;}
}
