package com.hbm.entity.projectile.siegelaser;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.explosion.ExplosionNT;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** Active source EntitySiegeLaser, retaining its vanilla-Throwable branch and original configurable impact. */
public final class EntitySiegeLaser extends EntityThrowableNT implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> COLOR=SynchedEntityData.defineId(EntitySiegeLaser.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WAITING=SynchedEntityData.defineId(EntitySiegeLaser.class,EntityDataSerializers.BOOLEAN);
    private static final Set<EntitySiegeLaser> ACTIVE=Collections.newSetFromMap(new IdentityHashMap<>());
    private float damage=2,explosive,breakChance;
    private boolean incendiary,executing;
    private HitResult pending;
    private UUID pendingVictim;
    private Vec3 pendingMotion=Vec3.ZERO;
    private NuclearChunkAccess access;
    private int accessCapacity,pausedTicks,suspendedAge;
    public EntitySiegeLaser(EntityType<? extends EntitySiegeLaser> type,Level world){super(type,world);}
    public EntitySiegeLaser(Level world){this(SiegeLaserContent.LASER.get(),world);}
    public EntitySiegeLaser(Level world,double x,double y,double z){this(world);setPos(x,y,z);}
    public EntitySiegeLaser(Level world,LivingEntity owner){this(world);setThrower(owner);setYRot(owner.getYRot());setXRot(owner.getXRot());float yaw=getYRot()/180F*(float)Math.PI,pitch=getXRot()/180F*(float)Math.PI;setPos(owner.getX()-(double)(Mth.cos(yaw)*.16F),owner.getY()+owner.getEyeHeight()-.10000000149011612D,owner.getZ()-(double)(Mth.sin(yaw)*.16F));setThrowableHeading(-Mth.sin(yaw)*Mth.cos(pitch)*.4F,-Mth.sin(pitch)*.4F,Mth.cos(yaw)*Mth.cos(pitch)*.4F,1.5F,1F);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){super.defineSynchedData(data);data.define(COLOR,0xffffff);data.define(WAITING,false);}
    @Override public int legacyTrackingRange(){return 1000;}
    @Override protected boolean sourceVanillaThrowable(){return true;}
    @Override protected double sourceRotationPi(){return(double)(float)Math.PI;}
    @Override protected double headingForceMult(){return(double).0075F;}
    @Override public double getGravityVelocity(){return 0;}
    public EntitySiegeLaser setDamage(float value){damage=value;return this;}
    public EntitySiegeLaser setExplosive(float value){explosive=value;return this;}
    public EntitySiegeLaser setBreakChance(float value){breakChance=value;return this;}
    public EntitySiegeLaser setIncendiary(){incendiary=true;return this;}
    public EntitySiegeLaser setColor(int value){entityData.set(COLOR,value);return this;}
    public int getColor(){return entityData.get(COLOR);}
    @Override protected boolean pauseAfterImpact(){return pending!=null;}
    @Override public void tick(){
        if(pending!=null||entityData.get(WAITING)){
            pausedTicks++;
            if(level() instanceof ServerLevel server){
                try{if(ready(server)){setDeltaMovement(pendingMotion);executing=true;try{HitResult impact=resolvePending(server);pending=null;entityData.set(WAITING,false);if(impact!=null)impactNow(impact);continueSourceFlight();}finally{executing=false;closeAccess();}expire();}}catch(RuntimeException|Error failure){closeAccess();throw failure;}
            }
            return;
        }
        super.tick();if(pending==null)expire();
    }
    private void expire(){if(tickCount-pausedTicks>60)discard();}
    @Override protected void onImpact(HitResult hit){
        if(level() instanceof ServerLevel server&&needsReady(hit)){
            pending=hit;pendingVictim=hit instanceof EntityHitResult entity?entity.getEntity().getUUID():null;pendingMotion=getDeltaMovement();suspendedAge=tickCount-pausedTicks;
            try{
                if(!ready(server)){ACTIVE.add(this);entityData.set(WAITING,true);setDeltaMovement(Vec3.ZERO);hurtMarked=true;forceTeleport();return;}
                pending=null;executing=true;try{impactNow(hit);}finally{executing=false;closeAccess();}
            }catch(RuntimeException|Error failure){closeAccess();throw failure;}
        }else impactNow(hit);
    }
    private boolean needsReady(HitResult hit){return explosive>0||hit instanceof BlockHitResult&&(incendiary||breakChance>0);}
    private DamageSource laserDamage(){var key=ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.parse("hbm:maskman_laser"));var holder=level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);var owner=getThrower();return owner==null?new DamageSource(holder):new DamageSource(holder,this,owner);}
    private void impactNow(HitResult hit){
        if(hit instanceof EntityHitResult entity){
            if(entity.getEntity().hurt(laserDamage(),damage)){discard();if(incendiary)entity.getEntity().igniteForSeconds(3);if(explosive>0)sourceExplosion(hit.getLocation());}
        }else if(hit instanceof BlockHitResult block){
            if(explosive>0)sourceExplosion(hit.getLocation());
            else if(incendiary){var fire=block.getBlockPos().relative(block.getDirection());if(level().getBlockState(fire).canBeReplaced())level().setBlock(fire,Blocks.FIRE.defaultBlockState(),3);}
            // Always draw this source nextFloat, including the default breakChance0.
            if(random.nextFloat()<breakChance)level().destroyBlock(block.getBlockPos(),false);
            discard();
        }
    }
    private void sourceExplosion(Vec3 center){
        var blast=new ExplosionNT(level(),this,center.x,center.y,center.z,explosive);blast.isSmoking=false;blast.explode();if(incendiary)blast.igniteVanillaAffectedBlocks();
        if(level() instanceof ServerLevel server)for(var player:server.players())if(player.distanceToSqr(center)<4096D)player.connection.send(new ClientboundExplodePacket(center.x,center.y,center.z,explosive,List.of(),blast.getHitPlayers().get(player),Explosion.BlockInteraction.KEEP,ParticleTypes.EXPLOSION,ParticleTypes.EXPLOSION_EMITTER,SoundEvents.GENERIC_EXPLODE));
    }
    /** No RNG or block lookups while planning. Only actual source blast rays, mutation cells and loaded-target LOS can demand chunks. */
    private boolean ready(ServerLevel world){
        var demand=new LinkedHashSet<Long>();HitResult hit=resolvePending(world);if(hit==null)return true;
        if(hit instanceof BlockHitResult block){add(demand,block.getBlockPos());if(incendiary)add(demand,block.getBlockPos().relative(block.getDirection()));}
        if(explosive>0){
            Vec3 center=hit.getLocation();double radius=Math.ceil(explosive*1.3D/(.3D*.75D))*.3D+1D;
            int minX=Mth.floor((center.x-radius)/16D),maxX=Mth.floor((center.x+radius)/16D),minZ=Mth.floor((center.z-radius)/16D),maxZ=Mth.floor((center.z+radius)/16D);
            for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++){double dx=Math.max(Math.max(x*16D-center.x,0),center.x-(x*16D+16D)),dz=Math.max(Math.max(z*16D-center.z,0),center.z-(z*16D+16D));if(dx*dx+dz*dz<=radius*radius)demand.add(ChunkPos.asLong(x,z));}
            double range=explosive*2D;for(var entity:world.getEntities(this,new AABB(center,center).inflate(range+1))){
                if(entity.distanceToSqr(center)>range*range)continue;var box=entity.getBoundingBox();
                // Every original density sample lies in this target's real AABB; only its corridor is prepared, not the entire damage-query sphere.
                for(int x=Mth.floor(Math.min(center.x,box.minX)/16D);x<=Mth.floor(Math.max(center.x,box.maxX)/16D);x++)for(int z=Mth.floor(Math.min(center.z,box.minZ)/16D);z<=Mth.floor(Math.max(center.z,box.maxZ)/16D);z++)demand.add(ChunkPos.asLong(x,z));
            }
        }
        var halo=new HashSet<Long>();for(long chunk:demand){int x=ChunkPos.getX(chunk),z=ChunkPos.getZ(chunk);for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)halo.add(ChunkPos.asLong(x+dx,z+dz));}
        if(access==null||halo.size()>accessCapacity){closeAccess();accessCapacity=Math.max(9,halo.size());access=new NuclearChunkAccess(accessCapacity);ACTIVE.add(this);}
        boolean complete=true;for(long chunk:demand)if(access.getWithNeighbors(world,ChunkPos.getX(chunk),ChunkPos.getZ(chunk))==null)complete=false;return complete;
    }
    private static void add(Set<Long> needed,BlockPos pos){needed.add(ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4));}
    private HitResult resolvePending(ServerLevel world){
        if(pending==null)return null;
        if(pendingVictim==null)return pending;
        var victim=world.getEntity(pendingVictim);return victim==null?null:new EntityHitResult(victim,pending.getLocation());
    }
    private void closeAccess(){ACTIVE.remove(this);if(access!=null){access.close();access=null;}accessCapacity=0;}
    @Override public void remove(RemovalReason reason){if(!executing)closeAccess();super.remove(reason);}
    public static void registerLifecycle(){NeoForge.EVENT_BUS.addListener((LevelEvent.Unload event)->{if(event.getLevel() instanceof ServerLevel world)for(var entity:List.copyOf(ACTIVE))if(entity.level()==world)entity.closeAccess();});NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event)->{for(var entity:List.copyOf(ACTIVE))if(entity.level() instanceof ServerLevel world&&world.getServer()==event.getServer())entity.closeAccess();});}
    @Override protected void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);tag.putFloat("damage",damage);tag.putFloat("explosive",explosive);tag.putFloat("breakChance",breakChance);tag.putBoolean("incendiary",incendiary);tag.putInt("color",getColor());
        if(pending!=null){var saved=new CompoundTag();Vec3 hit=pending.getLocation();saved.putDouble("x",hit.x);saved.putDouble("y",hit.y);saved.putDouble("z",hit.z);saved.putDouble("mx",pendingMotion.x);saved.putDouble("my",pendingMotion.y);saved.putDouble("mz",pendingMotion.z);saved.putInt("age",suspendedAge);if(pendingVictim!=null)saved.putUUID("victim",pendingVictim);else if(pending instanceof BlockHitResult block){saved.putLong("block",block.getBlockPos().asLong());saved.putInt("side",block.getDirection().get3DDataValue());}tag.put("hbm_pendingImpact",saved);}
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);damage=tag.getFloat("damage");explosive=tag.getFloat("explosive");breakChance=tag.getFloat("breakChance");incendiary=tag.getBoolean("incendiary");setColor(tag.getInt("color"));closeAccess();pending=null;pendingVictim=null;entityData.set(WAITING,false);
        if(tag.contains("hbm_pendingImpact",10)){var saved=tag.getCompound("hbm_pendingImpact");var hit=new Vec3(saved.getDouble("x"),saved.getDouble("y"),saved.getDouble("z"));pendingMotion=new Vec3(saved.getDouble("mx"),saved.getDouble("my"),saved.getDouble("mz"));suspendedAge=saved.getInt("age");pausedTicks=tickCount-suspendedAge;if(saved.hasUUID("victim")){pendingVictim=saved.getUUID("victim");pending=new BlockHitResult(hit,Direction.DOWN,BlockPos.containing(hit),false);}else pending=new BlockHitResult(hit,Direction.from3DDataValue(saved.getInt("side")),BlockPos.of(saved.getLong("block")),false);entityData.set(WAITING,true);setDeltaMovement(Vec3.ZERO);if(level() instanceof ServerLevel)ACTIVE.add(this);}
    }
}
