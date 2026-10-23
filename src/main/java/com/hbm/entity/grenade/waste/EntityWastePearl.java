package com.hbm.entity.grenade.waste;

import com.hbm.bomb.conventional.ConventionalContent;
import com.hbm.bomb.nuclear.NuclearTerrain;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.block.gas.*;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableNT;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** X5846's still-registered deprecated grenade, not the deleted pearl item. */
@EventBusSubscriber(modid="hbm")
public final class EntityWastePearl extends EntityThrowableNT implements LegacyTrackingRange {
    private static final Set<EntityWastePearl> ACTIVE=Collections.newSetFromMap(new IdentityHashMap<>());
    private final NuclearChunkAccess chunkAccess=new NuclearChunkAccess();
    private BlockPos pendingImpact;
    private boolean applyingImpact;
    public EntityWastePearl(EntityType<? extends EntityWastePearl> type,Level level){super(type,level);}
    public EntityWastePearl(Level level){this(WastePearlContent.WASTE_PEARL.get(),level);}
    public EntityWastePearl(Level level,double x,double y,double z){this(level);setPos(x,y,z);}
    public EntityWastePearl(Level level,LivingEntity owner){
        this(level);setThrower(owner);setYRot(owner.getYRot());setXRot(owner.getXRot());
        float yaw=getYRot()/180F*(float)Math.PI,pitch=getXRot()/180F*(float)Math.PI;
        setPos(owner.getX()-(double)(Mth.cos(yaw)*.16F),owner.getY()+owner.getEyeHeight()-.10000000149011612D,owner.getZ()-(double)(Mth.sin(yaw)*.16F));
        float x=-Mth.sin(yaw)*Mth.cos(pitch)*.4F,z=Mth.cos(yaw)*Mth.cos(pitch)*.4F,y=-Mth.sin(pitch)*.4F;
        setThrowableHeading(x,y,z,1.5F,1F);
    }
    @Override public int legacyTrackingRange(){return 1000;}
    @Override protected boolean sourceVanillaThrowable(){return true;}
    @Override protected double sourceRotationPi(){return (double)(float)Math.PI;}
    @Override protected double headingForceMult(){return (double).0075F;}
    @Override public double getGravityVelocity(){return (double).03F;}
    @Override public void tick(){
        // A collided pearl retains its operation while FULL neighbour chunks are
        // being prepared. No parent flight/collision/RNG is replayed while waiting.
        if(!level().isClientSide&&pendingImpact!=null){completePendingImpact();return;}
        super.tick();if(pendingImpact!=null){setDeltaMovement(Vec3.ZERO);return;}xRotO=getXRot();setXRot((float)(getXRot()-getDeltaMovement().length()*25));
        setYRot((float)(Math.atan2(getDeltaMovement().x,getDeltaMovement().z)*180D/Math.PI));
        while(getYRot()-yRotO< -180F)yRotO-=360F;
        while(getYRot()-yRotO>=180F)yRotO+=360F;
        setYRot(yRotO+(getYRot()-yRotO)*.2F);
    }
    @Override protected void onImpact(HitResult hit){
        if(!level().isClientSide&&pendingImpact!=null)return;
        if(hit instanceof EntityHitResult entity)entity.getEntity().hurt(damageSources().thrown(this,getThrower()),0F);
        if(!level().isClientSide&&ConventionalContent.EXTENDED_LOGGING.getAsBoolean())
            org.slf4j.LoggerFactory.getLogger("hbm").info("[GREN] Set off grenade at {} / {} / {} by {}!",(int)getX(),(int)getY(),(int)getZ(),getThrower() instanceof Player player?player.getDisplayName().getString():"null");
        explode();
    }
    public void explode(){
        if(level().isClientSide||isRemoved()||pendingImpact!=null)return;
        pendingImpact=new BlockPos(Mth.floor(getX()),Mth.floor(getY()),Mth.floor(getZ()));
        completePendingImpact();
    }
    private void completePendingImpact(){
        if(!(level() instanceof ServerLevel world)||pendingImpact==null)return;
        if(isRemoved()){releaseChunks();return;}
        ACTIVE.add(this);
        try{
            int x=pendingImpact.getX(),y=pendingImpact.getY(),z=pendingImpact.getZ();
            boolean ready=true;
            // The 7x7 footprint touches at most four target chunks. Each target
            // gets the same shared one-chunk mutation halo (union at most 4x4).
            // Request every target even after a cold result; never join/load here.
            for(int cx=(x-3)>>4;cx<=(x+3)>>4;cx++)for(int cz=(z-3)>>4;cz<=(z+3)>>4;cz++)
                if(chunkAccess.getWithNeighbors(world,cx,cz)==null)ready=false;
            if(!ready){setDeltaMovement(Vec3.ZERO);return;}
            applyingImpact=true;
            try{
                // Retain source discard-FIRST, but remove/leave cannot release
                // ready neighbour tickets until the complete original loop ends.
                discard();
                var fallout=NuclearTerrain.FALLOUT.get().defaultBlockState();
                for(int ix=x-3;ix<=x+3;ix++)for(int iy=y-3;iy<=y+3;iy++)for(int iz=z-3;iz<=z+3;iz++){
                    var pos=new BlockPos(ix,iy,iz);
                    if(world.random.nextInt(3)==0&&world.getBlockState(pos).canBeReplaced()&&fallout.canSurvive(world,pos))
                        world.setBlock(pos,fallout,3);
                    else if(world.getBlockState(pos).is(Blocks.AIR))
                        world.setBlock(pos,GasContent.BLOCKS_BY_KIND.get(random.nextBoolean()?GasKind.RADON:GasKind.RADON_DENSE).get().defaultBlockState(),3);
                }
                pendingImpact=null;
            }finally{applyingImpact=false;releaseChunks();}
        }catch(RuntimeException|Error failure){releaseChunks();throw failure;}
    }
    private void releaseChunks(){
        if(applyingImpact)return;
        chunkAccess.close();if(level() instanceof ServerLevel)ACTIVE.remove(this);
    }
    @Override public void remove(RemovalReason reason){try{super.remove(reason);}finally{releaseChunks();}}
    @SubscribeEvent public static void leave(EntityLeaveLevelEvent event){if(event.getEntity() instanceof EntityWastePearl pearl&&pearl.level() instanceof ServerLevel)pearl.releaseChunks();}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(!(event.getLevel() instanceof ServerLevel))return;for(var pearl:List.copyOf(ACTIVE))if(pearl.level()==event.getLevel())pearl.releaseChunks();}
    @SubscribeEvent public static void stop(ServerStoppingEvent event){for(var pearl:List.copyOf(ACTIVE))if(pearl.level() instanceof ServerLevel world&&world.getServer()==event.getServer())pearl.releaseChunks();}
    @Override protected void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        // The actual vanilla parent never persisted these runtime counters.
        tag.remove("ticksInGround");tag.remove("ticksInAir");
        if(pendingImpact!=null){var pending=new CompoundTag();pending.putInt("x",pendingImpact.getX());pending.putInt("y",pendingImpact.getY());pending.putInt("z",pendingImpact.getZ());tag.put("hbm_pending_impact",pending);}
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        releaseChunks();pendingImpact=null;
        var source=tag.copy();WastePearlLegacyNbt.convertBlockId(source);
        source.remove("ticksInGround");source.remove("ticksInAir");super.readAdditionalSaveData(source);
        if(tag.contains("hbm_pending_impact",Tag.TAG_COMPOUND)){var pending=tag.getCompound("hbm_pending_impact");pendingImpact=new BlockPos(pending.getInt("x"),pending.getInt("y"),pending.getInt("z"));setDeltaMovement(Vec3.ZERO);}
    }
}
