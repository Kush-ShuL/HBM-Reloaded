package com.hbm.entity.projectile;

import com.hbm.weapon.sedna.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/** Original MK4 velocity multiplier, gravity, impact callbacks and server authority. */
public class EntityBulletBaseMK4 extends EntityThrowableInterp implements IEntityWithComplexSpawn, com.hbm.explosion.ExplosionKnockbackExempt, com.hbm.entity.LegacyTrackingRange {
    private static final EntityDataAccessor<String> CONFIG=SynchedEntityData.defineId(EntityBulletBaseMK4.class,EntityDataSerializers.STRING);
    public BulletConfig config;
    public double velocity,prevVelocity,accel;
    public float damage;
    public int ricochets;
    public Entity lockonTarget;
    public EntityBulletBaseMK4(EntityType<? extends EntityBulletBaseMK4> type,Level level){super(type,level);}
    public EntityBulletBaseMK4(Level level){this(SednaProjectiles.BULLET.get(),level);}
    @Override public int legacyTrackingRange(){return 250;}
    public EntityBulletBaseMK4(Level level,LivingEntity owner,BulletConfig config,float damage,float gunSpread,double x,double y,double z,double motionX,double motionY,double motionZ){
        this(level);thrower=owner;setBulletConfig(config);this.damage=damage;moveTo(x,y,z,0,0);setThrowableHeading(motionX,motionY,motionZ,1,config.spread+gunSpread);
    }
    public EntityBulletBaseMK4(LivingEntity owner,BulletConfig config,float baseDamage,float gunSpread,double sideOffset,double heightOffset,double frontOffset){
        this(SednaProjectiles.BULLET.get(),owner,config,baseDamage,gunSpread,sideOffset,heightOffset,frontOffset);
    }
    /** Shared original trajectory for source MK4 subclasses with their own real entity type. */
    protected EntityBulletBaseMK4(EntityType<? extends EntityBulletBaseMK4> type,LivingEntity owner,BulletConfig config,float baseDamage,float gunSpread,double sideOffset,double heightOffset,double frontOffset){
        this(type,owner.level());thrower=owner;setBulletConfig(config);damage=baseDamage*config.damageMult;
        moveTo(owner.getX(),owner.getY()+owner.getEyeHeight(),owner.getZ(),owner.getYRot(),owner.getXRot());
        Vec3 offset=new Vec3(sideOffset,heightOffset,frontOffset).xRot(-getXRot()/180F*(float)Math.PI).yRot(-getYRot()/180F*(float)Math.PI);setPos(position().add(offset));
        heading(gunSpread);
    }
    /** Source turret constructor: radians, with negative pitch convention. */
    public EntityBulletBaseMK4(Level level,BulletConfig config,float baseDamage,float gunSpread,float yaw,float pitch){
        this(level);setBulletConfig(config);damage=baseDamage*config.damageMult;setYRot(yaw*180F/(float)Math.PI);setXRot(-pitch*180F/(float)Math.PI);yRotO=getYRot();xRotO=getXRot();heading(gunSpread);
    }
    private void heading(float spread){
        float yaw=getYRot()/180F*(float)Math.PI,pitch=getXRot()/180F*(float)Math.PI;
        setThrowableHeading(-Mth.sin(yaw)*Mth.cos(pitch),-Mth.sin(pitch),Mth.cos(yaw)*Mth.cos(pitch),1,spread);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(CONFIG,"");}
    public void setBulletConfig(BulletConfig config){this.config=config;entityData.set(CONFIG,config.identifier.toString());}
    public BulletConfig getBulletConfig(){return BulletConfig.byName(entityData.get(CONFIG));}
    @Override public void tick(){
        if(config==null)config=getBulletConfig();if(config==null){setDead();return;}
        xo=getX();yo=getY();zo=getZ();super.tick();double dx=getX()-xo,dy=getY()-yo,dz=getZ()-zo;
        if(!inGround&&lockonTarget!=null&&!lockonTarget.isRemoved()){
            Vec3 motion=getDeltaMovement(),delta=new Vec3(lockonTarget.getX()-getX(),lockonTarget.getY()+lockonTarget.getBbHeight()/2D-getY(),lockonTarget.getZ()-getZ());
            float turn=Math.min(.005F*tickCount,1);
            // Source Vec3NT.lengthVector/normalizeSelf use float-rounded sqrt and two sequential multiplies.
            double speed=(double)(float)Math.sqrt(motion.lengthSqr());
            Vec3 steered=new Vec3(motion.x+(delta.x-motion.x)*turn,motion.y+(delta.y-motion.y)*turn,motion.z+(delta.z-motion.z)*turn);
            double norm=(double)(float)Math.sqrt(steered.lengthSqr());
            setDeltaMovement((norm<1.0E-4D?Vec3.ZERO:steered.scale(1D/norm)).scale(speed));
            if(level() instanceof ServerLevel server)server.getChunkSource().broadcastAndSend(this,new ClientboundMoveEntityPacket.Rot(getId(),(byte)Mth.floor(getYRot()*256F/360),(byte)Mth.floor(getXRot()*256F/360),onGround()));
        }
        prevVelocity=velocity;velocity=Math.sqrt(dx*dx+dy*dy+dz*dz);
        if((!config.clientKeepsRotation||!level().isClientSide)&&!inGround&&!onGround()&&velocity>0){
            float horizontal=(float)Math.sqrt(dx*dx+dz*dz);setYRot((float)(Math.atan2(dx,dz)*180/Math.PI));setXRot((float)(Math.atan2(dy,horizontal)*180/Math.PI));
            while(getXRot()-xRotO< -180)xRotO-=360;while(getXRot()-xRotO>=180)xRotO+=360;while(getYRot()-yRotO< -180)yRotO-=360;while(getYRot()-yRotO>=180)yRotO+=360;
        }
        if(!level().isClientSide&&tickCount>config.expires)setDead();if(config.onUpdate!=null)config.onUpdate.accept(this);
    }
    @Override public void setDead(){if(!isRemoved())forceTeleport();super.setDead();}
    public void forceNetworkTeleport(){forceTeleport();}
    @Override protected void onImpact(HitResult impact){
        if(!level().isClientSide){if(config.onImpact!=null)config.onImpact.accept(this,impact);if(isRemoved()||inGround)return;if(config.onRicochet!=null)config.onRicochet.accept(this,impact);if(config.onEntityHit!=null)config.onEntityHit.accept(this,impact);}
    }
    @Override protected java.util.Optional<Vec3> entityBoxIntercept(AABB box,Vec3 start,Vec3 end){return java.util.Optional.ofNullable(com.hbm.entity.mob.crab.CyberTauBullet.sourceIntercept(box,start,end));}
    @Override protected boolean acceptsPortal(net.minecraft.world.level.block.Portal portal){return portal==(Object)net.minecraft.world.level.block.Blocks.NETHER_PORTAL;}
    @Override protected double headingForceMult(){return 1;}@Override public double getGravityVelocity(){return config.gravity;}@Override protected double motionMult(){return config.velocity+accel;}
    @Override protected float getAirDrag(){return 1;}@Override protected float getWaterDrag(){return 1;}
    @Override public boolean doesImpactEntities(){return config.impactsEntities;}@Override public boolean doesPenetrate(){return config.doesPenetrate;}@Override public boolean isSpectral(){return config.isSpectral;}@Override public int selfDamageDelay(){return config.selfDamageDelay;}
    @Override public boolean displayFireAnimation(){return false;}@Override public boolean fireImmune(){return true;}
    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer){buffer.writeInt(thrower==null?-1:thrower.getId());}
    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer){Entity owner=level().getEntity(buffer.readInt());if(owner instanceof LivingEntity living)thrower=living;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);if(config!=null)tag.putString("hbmConfig",config.identifier.toString());tag.putFloat("hbmDamage",damage);tag.putDouble("hbmAccel",accel);tag.putInt("hbmRicochets",ricochets);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);BulletConfig saved=BulletConfig.byName(tag.getString("hbmConfig"));if(saved!=null)setBulletConfig(saved);damage=tag.getFloat("hbmDamage");accel=tag.getDouble("hbmAccel");ricochets=tag.getInt("hbmRicochets");}
}
