package com.hbm.entity.projectile;

import com.hbm.bomb.conventional.TinyExplosionEffects;
import com.hbm.weapon.sedna.*;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;

/** Source instant beam trace, penetration, coin diversion and callback order. */
public class EntityBulletBeamBase extends Entity implements IEntityWithComplexSpawn,TraceableEntity,com.hbm.entity.LegacyTrackingRange {
    private static final EntityDataAccessor<String> CONFIG=SynchedEntityData.defineId(EntityBulletBeamBase.class,EntityDataSerializers.STRING);
    public LivingEntity thrower;
    public BulletConfig config;
    public float damage;
    public double headingX,headingY,headingZ,beamLength;
    public EntityBulletBeamBase(EntityType<? extends EntityBulletBeamBase> type,Level level){super(type,level);noCulling=true;}
    public EntityBulletBeamBase(Level level){this(SednaProjectiles.BEAM.get(),level);}
    public EntityBulletBeamBase(Level level,BulletConfig config,float baseDamage){this(level);setBulletConfig(config);damage=baseDamage*config.damageMult;}
    public EntityBulletBeamBase(LivingEntity owner,BulletConfig config,float baseDamage){this(owner.level(),config,baseDamage);thrower=owner;}
    public EntityBulletBeamBase(LivingEntity owner,BulletConfig config,float baseDamage,float angularInaccuracy,double sideOffset,double heightOffset,double frontOffset){
        this(owner,config,baseDamage);
        moveTo(owner.getX(),owner.getY()+owner.getEyeHeight(),owner.getZ(),owner.getYRot()+(float)random.nextGaussian()*angularInaccuracy,owner.getXRot()+(float)random.nextGaussian()*angularInaccuracy);
        Vec3 offset=new Vec3(sideOffset,heightOffset,frontOffset).xRot(-getXRot()/180F*(float)Math.PI).yRot(-getYRot()/180F*(float)Math.PI);setPos(position().add(offset));
        directionFromRotations();performHitscanExternal(250);
    }
    public LivingEntity getThrower(){return thrower;}
    @Override public LivingEntity getOwner(){return thrower;}
    @Override public int legacyTrackingRange(){return 250;}
    @Override public boolean shouldRenderAtSqrDistance(double distance){double range=getBoundingBox().getSize()*64*10;return distance<range*range;}
    public void setRotationsFromVector(Vec3 delta){setXRot((float)(-Math.asin(delta.y/delta.length())*180/Math.PI));setYRot((float)(-Math.atan2(delta.x,delta.z)*180/Math.PI));directionFromRotations();}
    private void directionFromRotations(){float yaw=getYRot()/180F*(float)Math.PI,pitch=getXRot()/180F*(float)Math.PI;headingX=-Mth.sin(yaw)*Mth.cos(pitch);headingZ=Mth.cos(yaw)*Mth.cos(pitch);headingY=-Mth.sin(pitch);}
    public void performHitscanExternal(double range){headingX*=range;headingY*=range;headingZ*=range;performHitscan();}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(CONFIG,"");}
    public void setBulletConfig(BulletConfig value){config=value;entityData.set(CONFIG,value.identifier.toString());}
    public BulletConfig getBulletConfig(){return BulletConfig.byName(entityData.get(CONFIG));}
    @Override public void tick(){if(config==null)config=getBulletConfig();if(config==null){discard();return;}if(config.onUpdate!=null)config.onUpdate.accept(this);super.tick();if(!level().isClientSide&&tickCount>config.expires)discard();}
    protected void performHitscan(){
        Vec3 start=position(),end=start.add(headingX,headingY,headingZ);HitResult impact=null;
        if(!isSpectral()){var block=level().clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));if(block.getType()!=HitResult.Type.MISS)impact=block;}
        start=position();end=start.add(headingX,headingY,headingZ);if(impact!=null)end=impact.getLocation();
        if(!level().isClientSide&&doesImpactEntities()){
            List<Entity> candidates=level().getEntities(this,getBoundingBox().expandTowards(headingX,headingY,headingZ).inflate(1));
            double closestCoin=0;EntityCoin coin=null;Vec3 coinPoint=null;
            for(var entity:candidates)if(!entity.isRemoved()&&entity instanceof EntityCoin candidate){var hit=com.hbm.entity.mob.crab.CyberTauBullet.sourceIntercept(entity.getBoundingBox().inflate((double).3F),start,end);if(hit!=null){double distance=start.distanceTo(hit);if(closestCoin==0||distance<closestCoin){closestCoin=distance;coin=candidate;coinPoint=hit;}}}
            double nearest=0;Entity nearestEntity=null;Vec3 nearestPoint=null;
            for(var entity:candidates)if(entity.isPickable()&&entity!=thrower&&entity.isAlive()){
                var hit=com.hbm.entity.mob.crab.CyberTauBullet.sourceIntercept(entity.getBoundingBox().inflate((double).3F),start,end);if(hit==null)continue;double distance=start.distanceTo(hit);
                if(doesPenetrate()){if(coin==null||distance<closestCoin)onImpact(new EntityHitResult(entity,hit));}
                else if(distance<nearest||nearest==0){nearest=distance;nearestEntity=entity;nearestPoint=hit;}
            }
            if(!doesPenetrate()&&nearestEntity!=null)impact=new EntityHitResult(nearestEntity,nearestPoint);
            if(coin!=null){beamLength=coinPoint.distanceTo(position());redirectFromCoin(coin,coinPoint);return;}
        }
        if(impact!=null){if(impact instanceof BlockHitResult hit&&level().getBlockState(hit.getBlockPos()).is(net.minecraft.world.level.block.Blocks.NETHER_PORTAL))setAsInsidePortal((Portal)net.minecraft.world.level.block.Blocks.NETHER_PORTAL,hit.getBlockPos());else onImpact(impact);beamLength=impact.getLocation().distanceTo(position());}
        else beamLength=end.distanceTo(position());
    }
    private void redirectFromCoin(EntityCoin coin,Vec3 origin){
        double range=50;var candidates=level().getEntities((Entity)null,new AABB(origin,origin).inflate(range));
        Entity nextCoin=null,nextPlayer=null,nextMob=null,nextOther=null;double coinDistance=0,playerDistance=0,mobDistance=0,otherDistance=0;coin.setDead();
        for(var entity:candidates){if(entity==getThrower()||entity.isRemoved())continue;double distance=entity.distanceTo(coin);if(distance>range)continue;
            if(entity instanceof EntityCoin){if(coinDistance==0||distance<coinDistance){coinDistance=distance;nextCoin=entity;}}
            else if(entity instanceof Player){if(playerDistance==0||distance<playerDistance){playerDistance=distance;nextPlayer=entity;}}
            else if(entity instanceof Monster){if(mobDistance==0||distance<mobDistance){mobDistance=distance;nextMob=entity;}}
            else if(entity instanceof LivingEntity){if(otherDistance==0||distance<otherDistance){otherDistance=distance;nextOther=entity;}}
        }
        Entity target=nextCoin!=null?nextCoin:nextPlayer!=null?nextPlayer:nextMob!=null?nextMob:nextOther;
        LivingEntity owner=coin.getThrower()!=null?coin.getThrower():thrower;
        var next=new EntityBulletBeamBase(level(),config,damage*1.25F);next.thrower=owner;next.setPos(origin);
        Vec3 delta=target!=null?new Vec3(target.getX()-origin.x,target.getY()+target.getBbHeight()/2D-origin.y,target.getZ()-origin.z):new Vec3(random.nextGaussian()*.5,-1,random.nextGaussian()*.5);
        next.setRotationsFromVector(delta);next.performHitscanExternal(250);level().addFreshEntity(next);
        if(level() instanceof ServerLevel server){var packet=new TinyExplosionEffects(origin.x,origin.y,origin.z);for(var player:server.players())if(player.distanceToSqr(origin)<=100D*100D)PacketDistributor.sendToPlayer(player,packet);}
    }
    protected void onImpact(HitResult hit){if(!level().isClientSide&&config.onImpactBeam!=null)config.onImpactBeam.accept(this,hit);}
    public boolean doesImpactEntities(){return config.impactsEntities;}public boolean doesPenetrate(){return config.doesPenetrate;}public boolean isSpectral(){return config.isSpectral;}
    @Override public boolean fireImmune(){return true;}@Override public boolean displayFireAnimation(){return false;}
    @Override public boolean shouldBeSaved(){return false;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}
    @Override protected void readAdditionalSaveData(CompoundTag tag){discard();}
    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer){buffer.writeDouble(beamLength);buffer.writeFloat(getYRot());buffer.writeFloat(getXRot());}
    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer){beamLength=buffer.readDouble();setYRot(buffer.readFloat());setXRot(buffer.readFloat());}
}
