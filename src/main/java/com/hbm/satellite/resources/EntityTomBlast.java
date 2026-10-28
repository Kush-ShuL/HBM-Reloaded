package com.hbm.satellite.resources;

import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.bomb.nuclear.NuclearWork;
import com.hbm.bomb.nuclear.NuclearChunkloadingEntity;
import com.hbm.bomb.nuclear.NuclearTickBudget;
import com.hbm.bomb.nuclear.NuclearConfig;
import com.hbm.saveddata.TomSaveData;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** Configurable fast spiral-column batches, with shared CPU and complete-neighbour safeguards. */
@EventBusSubscriber(modid="hbm")
public final class EntityTomBlast extends NuclearChunkloadingEntity implements com.hbm.entity.LegacyTrackingRange,NuclearWork {
    private static final Set<EntityTomBlast> ACTIVE=Collections.newSetFromMap(new IdentityHashMap<>());
    private int batchRemaining;
    private boolean batchComplete;
    private int startupWorkTicks;
    public int age,destructionRange,speed=1;public boolean did;public ExplosionTom exp;
    public EntityTomBlast(EntityType<?> type,Level level){super(type,level);}
    public EntityTomBlast(Level level){this(SatelliteResources113.BLAST.get(),level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){}
    @Override public void tick(){
        super.tick();if(!(level() instanceof ServerLevel world))return;
        int startupDuration=NuclearConfig.TOM_STARTUP_TICKS.get();
        boolean terrainWorked=false;
        try{
            loadChunk(net.minecraft.util.Mth.floor(getX()/16D),net.minecraft.util.Mth.floor(getZ()/16D));ACTIVE.add(this);
            if(!did){com.hbm.logging.SourceActionLog.info("[NUKE] Initialized TOM explosion at {} / {} / {} with strength {}!",getX(),getY(),getZ(),destructionRange);exp=new ExplosionTom((int)getX(),(int)getY(),(int)getZ(),world,destructionRange);did=true;}
            // User-configured faster batches retain their exact column/y cursor across
            // both asynchronous FULL-chunk waits and this tick's CPU time slice.
            if(batchRemaining==0){
                speed=(int)Math.min(NuclearConfig.TOM_MAX_COLUMNS.get(),Math.max(
                        NuclearConfig.TOM_MIN_COLUMNS.get(),(long)speed+NuclearConfig.TOM_ACCELERATION.get()));
                batchRemaining=speed;batchComplete=false;
            }
            double startup=startupDuration==0?1:Math.min(1,(double)startupWorkTicks/startupDuration);
            double ramp=startup*startup;
            int fullBudget=NuclearConfig.TOM_TICK_BUDGET.get();
            int initialBudget=Math.min(fullBudget,NuclearConfig.TOM_STARTUP_BUDGET.get());
            long budgetNanos=(long)((initialBudget+(fullBudget-initialBudget)*ramp)*1_000_000L);
            int initialColumns=Math.min(NuclearConfig.TOM_MIN_COLUMNS.get(),NuclearConfig.TOM_MAX_COLUMNS.get());
            int columnLimit=startup==1?Integer.MAX_VALUE:1+(int)((initialColumns-1)*ramp);
            long deadline=Math.min(NuclearTickBudget.deadline(world,this),System.nanoTime()+budgetNanos);
            int processedColumns=0;
            // Keep the original batch (including its damage/sounds) intact while
            // spreading the first terrain work over several real server ticks.
            while(batchRemaining>0&&processedColumns<columnLimit){
                if(System.nanoTime()>=deadline)return;
                boolean complete=exp.updateUntil(deadline);
                if(!exp.waitingForChunks())terrainWorked=true;
                if(exp.waitingForChunks()||exp.yielded())return;
                batchRemaining--;processedColumns++;
                if(complete){batchComplete=true;var data=TomSaveData.forWorld(world);data.impact=true;data.fire=1F;data.setDirty();}
            }
            if(batchRemaining>0)return;
            boolean complete=batchComplete;batchRemaining=0;batchComplete=false;
            // Keep all original remaining source calls after its first complete result;
            // removal/lease close is deferred only until that same batch has ended.
            if(complete)discard();
            if(random.nextInt(5)==0)world.playSound(null,getX(),getY(),getZ(),SoundEvents.GENERIC_EXPLODE,SoundSource.NEUTRAL,10000,.8F+random.nextFloat()*.2F);
            if(!complete){world.playSound(null,getX(),getY(),getZ(),SoundEvents.LIGHTNING_BOLT_THUNDER,SoundSource.NEUTRAL,10000,.8F+random.nextFloat()*.2F);ExplosionNukeGeneric.dealDamage(world,getX(),getY(),getZ(),destructionRange*2);}
            age++;
        }catch(RuntimeException|Error failure){releaseChunks();clearChunkLoader();throw failure;}
        finally{if(terrainWorked&&startupWorkTicks<startupDuration)startupWorkTicks++;}
    }
    @Override public int legacyTrackingRange(){return 1000;}
    private void releaseChunks(){
        if(exp!=null)exp.close();
        ACTIVE.remove(this);
    }
    @Override public void remove(RemovalReason reason){releaseChunks();super.remove(reason);}
    @SubscribeEvent public static void leave(EntityLeaveLevelEvent event){if(event.getEntity() instanceof EntityTomBlast blast)blast.releaseChunks();}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){for(var blast:List.copyOf(ACTIVE))if(blast.level()==event.getLevel())blast.releaseChunks();}
    @SubscribeEvent public static void stop(ServerStoppingEvent event){for(var blast:List.copyOf(ACTIVE))if(blast.level() instanceof ServerLevel world&&world.getServer()==event.getServer())blast.releaseChunks();}
    @Override protected void addAdditionalSaveData(CompoundTag tag){
        saveTicket(tag);tag.putInt("age",age);tag.putInt("destructionRange",destructionRange);tag.putInt("speed",speed);tag.putBoolean("did",did);if(exp!=null)exp.saveToNbt(tag,"exp_");
        tag.putInt("hbm_tom_batchRemaining",batchRemaining);tag.putBoolean("hbm_tom_batchComplete",batchComplete);
        tag.putInt("hbm_tom_startupWorkTicks",startupWorkTicks);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        if(exp!=null)exp.close();
        loadTicket(tag);age=tag.getInt("age");destructionRange=tag.getInt("destructionRange");speed=tag.getInt("speed");did=tag.getBoolean("did");
        boolean hasCursor=did&&tag.contains("exp_n",net.minecraft.nbt.Tag.TAG_INT)
                &&tag.contains("exp_nlimit",net.minecraft.nbt.Tag.TAG_INT);
        exp=null;did=false;
        if(hasCursor&&level() instanceof ServerLevel server){
            exp=new ExplosionTom((int)getX(),(int)getY(),(int)getZ(),server,destructionRange);
            exp.readFromNbt(tag,"exp_");did=true;
        }
        batchRemaining=hasCursor?tag.getInt("hbm_tom_batchRemaining"):0;
        batchComplete=hasCursor&&tag.getBoolean("hbm_tom_batchComplete");
        startupWorkTicks=tag.getInt("hbm_tom_startupWorkTicks");
    }
}
