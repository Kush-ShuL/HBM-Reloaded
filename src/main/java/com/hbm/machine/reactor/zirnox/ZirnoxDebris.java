package com.hbm.machine.reactor.zirnox;
import com.hbm.exposure.ExposureEffects;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
/** Source EntityZirnoxDebris + its Y/X/Z collision path, bounce, real salvage, irradiation and heavy-debris ceiling destruction. */
@net.neoforged.fml.common.EventBusSubscriber(modid="hbm")
public final class ZirnoxDebris extends Entity implements com.hbm.entity.LegacyTrackingRange {
    public enum Type {BLANK,ELEMENT,SHRAPNEL,GRAPHITE,CONCRETE,EXCHANGER}
    private static final EntityDataAccessor<Integer> TYPE=SynchedEntityData.defineId(ZirnoxDebris.class,EntityDataSerializers.INT);
    private static final Set<ZirnoxDebris> ACTIVE=Collections.newSetFromMap(new IdentityHashMap<>());
    private final com.hbm.bomb.nuclear.NuclearChunkAccess chunkAccess=new com.hbm.bomb.nuclear.NuclearChunkAccess();
    private int coldTicks;
    private boolean applyingStep;
    public float rotation,lastRotation;
    @Override public int legacyTrackingRange(){return 1000;}
    public ZirnoxDebris(EntityType<? extends ZirnoxDebris> type,Level level){super(type,level);rotation=lastRotation=random.nextFloat()*360;}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(TYPE,0);}
    public void setType(Type type){entityData.set(TYPE,type.ordinal());refreshDimensions();}
    public Type debrisType(){return entityData==null?Type.BLANK:Type.values()[Math.abs(entityData.get(TYPE))%Type.values().length];}
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key){super.onSyncedDataUpdated(key);if(TYPE.equals(key))refreshDimensions();}
    @Override public EntityDimensions getDimensions(Pose pose){return switch(debrisType()){case BLANK,SHRAPNEL->EntityDimensions.fixed(.5F,.5F);case ELEMENT,CONCRETE->EntityDimensions.fixed(.75F,.5F);case GRAPHITE->EntityDimensions.fixed(.25F,.25F);case EXCHANGER->EntityDimensions.fixed(1,.5F);};}
    @Override public boolean canBeCollidedWith(){return true;}
    /** Legacy canBeCollidedWith also enabled cursor selection; modern Minecraft separates that flag. */
    @Override public boolean isPickable(){return true;}
    @Override public InteractionResult interact(Player player,InteractionHand hand){if(!level().isClientSide){String item=switch(debrisType()){case BLANK->"debris_metal";case ELEMENT->"debris_element";case SHRAPNEL->"debris_shrapnel";case GRAPHITE->"debris_graphite";case CONCRETE->"debris_concrete";case EXCHANGER->"debris_exchanger";};if(player.getInventory().add(new ItemStack(HbmContent.item(item))))discard();player.inventoryMenu.broadcastChanges();}return InteractionResult.PASS;}
    @Override public void tick(){
        if(level() instanceof ServerLevel server){
            try{
                if(!prepareStep(server)){coldTicks++;return;}
                applyingStep=true;
                try{sourceStep();}finally{applyingStep=false;releaseChunks();}
            }catch(RuntimeException|Error failure){releaseChunks();throw failure;}
        }else sourceStep();
    }
    private boolean prepareStep(ServerLevel server){
        var movement=getDeltaMovement().add(0,-.04,0);
        var sweep=getBoundingBox().expandTowards(movement);
        double minX=sweep.minX,maxX=sweep.maxX,minZ=sweep.minZ,maxZ=sweep.maxZ;
        var type=debrisType();
        if((type==Type.CONCRETE||type==Type.EXCHANGER)&&movement.y>0){
            // After Y/X/Z clipping each horizontal velocity is unchanged or reflected by -.75.
            // Include the exact possible next position + original two-motion ceiling ray.
            minX=Math.min(minX,getX()+Math.min(3*movement.x,-1.5*movement.x));
            maxX=Math.max(maxX,getX()+Math.max(3*movement.x,-1.5*movement.x));
            minZ=Math.min(minZ,getZ()+Math.min(3*movement.z,-1.5*movement.z));
            maxZ=Math.max(maxZ,getZ()+Math.max(3*movement.z,-1.5*movement.z));
        }
        ACTIVE.add(this);
        boolean ready=true;
        for(int x=net.minecraft.util.Mth.floor(minX)>>4;x<=net.minecraft.util.Mth.floor(maxX)>>4;x++)
            for(int z=net.minecraft.util.Mth.floor(minZ)>>4;z<=net.minecraft.util.Mth.floor(maxZ)>>4;z++)
                if(chunkAccess.getWithNeighbors(server,x,z)==null)ready=false;
        return ready;
    }
    private void releaseChunks(){if(applyingStep)return;chunkAccess.close();if(level() instanceof ServerLevel)ACTIVE.remove(this);}
    @Override public void remove(RemovalReason reason){try{super.remove(reason);}finally{releaseChunks();}}
    @net.neoforged.bus.api.SubscribeEvent public static void leave(net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent event){if(event.getEntity() instanceof ZirnoxDebris debris&&debris.level() instanceof ServerLevel)debris.releaseChunks();}
    @net.neoforged.bus.api.SubscribeEvent public static void unload(net.neoforged.neoforge.event.level.LevelEvent.Unload event){if(!(event.getLevel() instanceof ServerLevel))return;for(var debris:List.copyOf(ACTIVE))if(debris.level()==event.getLevel())debris.releaseChunks();}
    @net.neoforged.bus.api.SubscribeEvent public static void stop(net.neoforged.neoforge.event.server.ServerStoppingEvent event){for(var debris:List.copyOf(ACTIVE))if(debris.level() instanceof ServerLevel server&&server.getServer()==event.getServer())debris.releaseChunks();}
    private void sourceStep(){xo=getX();yo=getY();zo=getZ();setDeltaMovement(getDeltaMovement().add(0,-.04,0));moveLegacy(getDeltaMovement());lastRotation=rotation;if(onGround())setDeltaMovement(getDeltaMovement().multiply(.85,-.5,.85));else{rotation+=10;if(rotation>=360){rotation-=360;lastRotation-=360;}}
        if(level() instanceof ServerLevel server){var type=debrisType();if((type==Type.CONCRETE||type==Type.EXCHANGER)&&getDeltaMovement().y>0){var from=position();var to=from.add(getDeltaMovement().scale(2));var hit=server.clip(new ClipContext(from,to,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));if(hit.getType()==HitResult.Type.BLOCK){var pos=hit.getBlockPos();for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){int distance=Math.abs(x)+Math.abs(y)+Math.abs(z);if(distance<=1||random.nextInt(distance)==0)server.removeBlock(pos.offset(x,y,z),false);}discard();}}
            if(type==Type.ELEMENT||type==Type.GRAPHITE)for(var living:server.getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(2.5)))living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION,1200,type==Type.ELEMENT?7:4));if(!server.getGameRules().getBoolean(ReactorWorldRules.PERMANENT_SCRAP)&&tickCount-coldTicks>lifetime()+getId()%50)discard();}
    }
    private int lifetime(){return switch(debrisType()){case BLANK->3600;case ELEMENT->12000;case SHRAPNEL,GRAPHITE->18000;case CONCRETE,EXCHANGER->1200;};}
    private void moveLegacy(Vec3 movement){var box=getBoundingBox();var shapeList=new ArrayList<VoxelShape>();level().getBlockCollisions(this,box.expandTowards(movement)).forEach(shapeList::add);shapeList.addAll(level().getEntityCollisions(this,box.expandTowards(movement)));double dy=Shapes.collide(Direction.Axis.Y,box,shapeList,movement.y);box=box.move(0,dy,0);double dx=Shapes.collide(Direction.Axis.X,box,shapeList,movement.x);box=box.move(dx,0,0);double dz=Shapes.collide(Direction.Axis.Z,box,shapeList,movement.z);box=box.move(0,0,dz);setBoundingBox(box);setPosRaw((box.minX+box.maxX)/2,box.minY,(box.minZ+box.maxZ)/2);horizontalCollision=movement.x!=dx||movement.z!=dz;verticalCollision=movement.y!=dy;setOnGround(movement.y!=dy&&movement.y<0);setDeltaMovement(movement.x!=dx?movement.x*-.75:movement.x,movement.y!=dy?0:movement.y,movement.z!=dz?movement.z*-.75:movement.z);checkInsideBlocks();}
    @Override protected void readAdditionalSaveData(CompoundTag tag){releaseChunks();coldTicks=0;entityData.set(TYPE,tag.getInt("debtype"));refreshDimensions();}@Override protected void addAdditionalSaveData(CompoundTag tag){tag.putInt("debtype",entityData.get(TYPE));}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<16384;}
}
