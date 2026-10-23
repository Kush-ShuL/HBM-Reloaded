package com.hbm.entity.projectile.cataclysm;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNukeGeneric;
import java.util.Collections;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** The actual Cataclysm satellite, retaining the two original explosion backends and source update/rotation. */
@EventBusSubscriber(modid="hbm")
public final class EntityBurningFOEQ extends EntityThrowableNT implements LegacyTrackingRange {
    private static final Set<EntityBurningFOEQ> ACTIVE=Collections.newSetFromMap(new IdentityHashMap<>());
    private final NuclearChunkAccess flightAccess=new NuclearChunkAccess();
    // Six-by-six target chunks plus their mutation halo fit the sole backend's64 resident limit.
    // Extra owners exist only for genuinely accessed regions, not a fixed preloaded kill sphere.
    private final Map<Long,NuclearChunkAccess> workAccess=new LinkedHashMap<>();
    private final Set<Long> neededOwners=new HashSet<>();
    private boolean impacted,sampled,executing,closing,partitionWork;
    private int blastIndex;
    private double blastX,blastY,blastZ;
    private boolean blastCloud;
    private record ChunkRect(int minX,int maxX,int minZ,int maxZ,boolean mutation){}

    public EntityBurningFOEQ(EntityType<? extends EntityBurningFOEQ> type,Level level){super(type,level);noCulling=true;}
    public EntityBurningFOEQ(Level level){this(CataclysmContent.FOEQ.get(),level);}
    @Override public int legacyTrackingRange(){return 1000;}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<100000D;}
    @Override protected boolean sourceVanillaThrowable(){return true;}
    @Override protected void onImpact(HitResult hit){}

    @Override public void tick(){
        if(impacted){if(!level().isClientSide)completeImpact();return;}
        var next=position().add(getDeltaMovement());
        int x=(int)next.x,y=(int)next.y,z=(int)next.z;
        if(level() instanceof ServerLevel world&&!world.isOutsideBuildHeight(y)){
            ACTIVE.add(this);
            try{if(flightAccess.get(world,x>>4,z>>4)==null)return;}
            catch(RuntimeException|Error failure){release();throw failure;}
        }
        try{
            // Source bypasses parent EntityThrowable.onUpdate: no drag, ray hit, timer or .03gravity.
            xo=xOld=getX();yo=yOld=getY();zo=zOld=getZ();setPos(next);
            var velocity=getDeltaMovement();if(velocity.y>-4D)setDeltaMovement(velocity.x,velocity.y-.1D,velocity.z);
            rotation();
            // Legacy outside-height block lookup was AIR; native VOID_AIR is the same empty-space case.
            if(!level().isOutsideBuildHeight(y)){
                var state=level().getBlockState(new BlockPos(x,y,z));
                // Modern vanilla splits the source's single AIR block into these three identities.
                if(!state.is(Blocks.AIR)&&!state.is(Blocks.CAVE_AIR)&&!state.is(Blocks.VOID_AIR)){
                    if(level().isClientSide)discard();else{impacted=true;completeImpact();}
                }
            }
        }finally{
            flightAccess.close();if(!impacted&&level() instanceof ServerLevel)ACTIVE.remove(this);
        }
    }

    public void rotation(){
        var v=getDeltaMovement();float horizontal=(float)Math.sqrt(v.x*v.x+v.z*v.z);
        setYRot((float)(Math.atan2(v.x,v.z)*180D/Math.PI));setXRot((float)(Math.atan2(v.y,horizontal)*180D/Math.PI)-90);
        while(getXRot()-xRotO< -180F)xRotO-=360F;while(getXRot()-xRotO>=180F)xRotO+=360F;
        while(getYRot()-yRotO< -180F)yRotO-=360F;while(getYRot()-yRotO>=180F)yRotO+=360F;
    }

    private boolean prepareRect(ServerLevel world,int minX,int maxX,int minZ,int maxZ,boolean mutation){
        boolean ready=true;
        for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++){
            long key=ownerKey(x,z);
            var access=workAccess.computeIfAbsent(key,ignored->new NuclearChunkAccess());
            if((mutation?access.getWithNeighbors(world,x,z):access.get(world,x,z))==null)ready=false;
        }
        return ready;
    }

    private long ownerKey(int x,int z){return partitionWork?ChunkPos.asLong(Math.floorDiv(x,6),Math.floorDiv(z,6)):Long.MIN_VALUE;}
    private boolean prepareAll(ServerLevel world,List<ChunkRect> rectangles){
        Set<Long> exact=new HashSet<>();
        for(var r:rectangles)for(int x=r.minX;x<=r.maxX;x++)for(int z=r.minZ;z<=r.maxZ;z++){
            int halo=r.mutation?1:0;
            for(int dx=-halo;dx<=halo;dx++)for(int dz=-halo;dz<=halo;dz++)exact.add(ChunkPos.asLong(x+dx,z+dz));
        }
        // The normal blast/waste window fits a SINGLE lease owner. Partition only an
        // actual giant entity's density footprint when its real required union exceeds64.
        partitionWork=exact.size()>64;neededOwners.clear();
        for(var r:rectangles)for(int x=r.minX;x<=r.maxX;x++)for(int z=r.minZ;z<=r.maxZ;z++)neededOwners.add(ownerKey(x,z));
        pruneWork();
        boolean ready=true;
        for(var r:rectangles)if(!prepareRect(world,r.minX,r.maxX,r.minZ,r.maxZ,r.mutation))ready=false;
        return ready;
    }

    private boolean prepareBlast(ServerLevel world){
        var rectangles=new ArrayList<ChunkRect>();
        // Native10F boundary rays start with at most13F energy, lose.225 per.3 step:
        // their maximum block position is within18. The same target chunks get one mutation halo.
        rectangles.add(new ChunkRect((int)Math.floor(blastX-18)>>4,(int)Math.floor(blastX+18)>>4,(int)Math.floor(blastZ-18)>>4,(int)Math.floor(blastZ+18)>>4,true));
        // Native density reads only current loaded entity targets inside the actual20block effect distance.
        // Prepare their real AABB-to-center footprint; do not generate an empty55block kill sphere.
        Vec3 center=new Vec3(blastX,blastY,blastZ);
        for(Entity target:world.getEntities((Entity)null,new AABB(blastX-21,blastY-21,blastZ-21,blastX+21,blastY+21,blastZ+21))){
            if(target.distanceToSqr(center)>400D)continue;
            var box=target.getBoundingBox();
            // Actual getSeenPercent shifts horizontal samples by these native offsets.
            double stepX=1D/((box.maxX-box.minX)*2+1),stepZ=1D/((box.maxZ-box.minZ)*2+1);
            double offsetX=(1D-Math.floor(1D/stepX)*stepX)/2D,offsetZ=(1D-Math.floor(1D/stepZ)*stepZ)/2D;
            double minX=Math.min(box.minX+offsetX,blastX),maxX=Math.max(box.maxX+offsetX,blastX),minZ=Math.min(box.minZ+offsetZ,blastZ),maxZ=Math.max(box.maxZ+offsetZ,blastZ);
            // Native traverseBlocks extends each ray endpoint by1e-7 of its delta.
            double epsX=(maxX-minX)*1E-7D,epsZ=(maxZ-minZ)*1E-7D;
            rectangles.add(new ChunkRect((int)Math.floor(minX-epsX)>>4,(int)Math.floor(maxX+epsX)>>4,(int)Math.floor(minZ-epsZ)>>4,(int)Math.floor(maxZ+epsZ)>>4,false));
        }
        return prepareAll(world,rectangles);
    }

    private void completeImpact(){
        if(!(level() instanceof ServerLevel world)||!impacted)return;
        ACTIVE.add(this);
        try{
            while(blastIndex<25){
                if(!sampled){
                    blastX=getX()+.5F+random.nextGaussian()*5;blastY=getY()+.5F+random.nextGaussian()*5;blastZ=getZ()+.5F+random.nextGaussian()*5;blastCloud=random.nextBoolean();sampled=true;
                }
                if(!prepareBlast(world))return;
                executing=true;
                try{ExplosionLarge.explode(world,blastX,blastY,blastZ,10F,blastCloud,false,false);}
                finally{executing=false;releaseWork();}
                if(isRemoved()||closing){release();return;}
                blastIndex++;sampled=false;
            }
            int x=(int)getX(),y=(int)getY(),z=(int)getZ();
            // waste35 still executes ALL70³ original RNG tests. Only its actual possible reads/writes
            // (distance²<612+nextInt122, hence radius<28) need neighbour preparation.
            if(!prepareAll(world,List.of(new ChunkRect((x-28)>>4,(x+28)>>4,(z-28)>>4,(z+28)>>4,true))))return;
            executing=true;
            try{ExplosionNukeGeneric.waste(world,x,y,z,35);impacted=false;discard();}
            finally{executing=false;release();}
        }catch(RuntimeException|Error failure){release();throw failure;}
    }

    private void pruneWork(){var iterator=workAccess.entrySet().iterator();while(iterator.hasNext()){var entry=iterator.next();if(!neededOwners.contains(entry.getKey())){entry.getValue().close();iterator.remove();}}}
    private void releaseWork(){if(executing)return;for(var access:workAccess.values())access.close();workAccess.clear();neededOwners.clear();}
    private void release(){if(executing){closing=true;return;}closing=false;flightAccess.close();releaseWork();if(level() instanceof ServerLevel)ACTIVE.remove(this);}
    @Override public void remove(RemovalReason reason){try{super.remove(reason);}finally{release();}}
    @SubscribeEvent public static void leave(EntityLeaveLevelEvent event){if(event.getEntity() instanceof EntityBurningFOEQ foeq&&foeq.level() instanceof ServerLevel)foeq.release();}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(!(event.getLevel() instanceof ServerLevel))return;for(var entity:List.copyOf(ACTIVE))if(entity.level()==event.getLevel())entity.release();}
    @SubscribeEvent public static void stop(ServerStoppingEvent event){for(var entity:List.copyOf(ACTIVE))if(entity.level() instanceof ServerLevel world&&world.getServer()==event.getServer())entity.release();}
    @Override protected void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);tag.remove("ticksInGround");tag.remove("ticksInAir");
        if(impacted){var pending=new CompoundTag();pending.putInt("index",blastIndex);pending.putBoolean("sampled",sampled);if(sampled){pending.putDouble("x",blastX);pending.putDouble("y",blastY);pending.putDouble("z",blastZ);pending.putBoolean("cloud",blastCloud);}tag.put("hbm_cataclysm_pending",pending);}
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        release();super.readAdditionalSaveData(tag);impacted=sampled=false;blastIndex=0;
        if(tag.contains("hbm_cataclysm_pending",Tag.TAG_COMPOUND)){var pending=tag.getCompound("hbm_cataclysm_pending");impacted=true;blastIndex=pending.getInt("index");sampled=pending.getBoolean("sampled");if(sampled){blastX=pending.getDouble("x");blastY=pending.getDouble("y");blastZ=pending.getDouble("z");blastCloud=pending.getBoolean("cloud");}}
    }
}
