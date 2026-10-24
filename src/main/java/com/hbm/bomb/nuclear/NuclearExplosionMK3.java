package com.hbm.bomb.nuclear;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.explosion.*;
import com.hbm.registry.HbmSounds;
import java.util.HashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Actual MK3: three independent waste shells, Fleija matter deletion, Solinium chemistry, and AT suppression. */
public final class NuclearExplosionMK3 extends NuclearChunkloadingEntity implements NuclearWork,LegacyTrackingRange {
 public int age,destructionRange,speed=1,extType;
 public ExplosionNukeAdvanced exp,wst,vap;
 public ExplosionFleija expl;
 public ExplosionSolinium sol;
 public float coefficient=1,coefficient2=1;
 public boolean did,did2,waste=true;
 private int phase;
 private boolean destructionComplete;
 public static final HashMap<ATEntry,Long> at=new HashMap<>();
 public NuclearExplosionMK3(EntityType<? extends NuclearExplosionMK3> type,Level level){super(type,level);}
 @Override public int legacyTrackingRange(){return 1000;}
 @Override public boolean hasNuclearWork(){return !isRemoved()&&destructionRange>0;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 private void closeWorkers(){if(exp!=null)exp.close();if(wst!=null)wst.close();if(vap!=null)vap.close();if(expl!=null)expl.close();if(sol!=null)sol.close();}
 @Override public void remove(RemovalReason reason){closeWorkers();super.remove(reason);}
 private void initialize(){
  if(waste){exp=new ExplosionNukeAdvanced((int)getX(),(int)getY(),(int)getZ(),level(),destructionRange,coefficient,0);wst=new ExplosionNukeAdvanced((int)getX(),(int)getY(),(int)getZ(),level(),(int)(destructionRange*1.8),coefficient,2);vap=new ExplosionNukeAdvanced((int)getX(),(int)getY(),(int)getZ(),level(),(int)(destructionRange*2.5),coefficient,1);}
  else if(extType==0)expl=new ExplosionFleija((int)getX(),(int)getY(),(int)getZ(),level(),destructionRange,coefficient,coefficient2);
  else if(extType==1)sol=new ExplosionSolinium((int)getX(),(int)getY(),(int)getZ(),level(),destructionRange,coefficient,coefficient2);
 }
 @Override public void tick(){
  if(!level().isClientSide)loadChunk(Mth.floor(getX()/16D),Mth.floor(getZ()/16D));
  if(!did){
   if(level() instanceof ServerLevel server){var achievement=server.getServer().getAdvancements().get(net.minecraft.resources.ResourceLocation.parse("hbm:manhattan"));if(achievement!=null)for(var player:server.players())player.getAdvancements().award(achievement,"detonated");if(com.hbm.logging.SourceActionLog.enabled())com.hbm.HbmReloaded.LOGGER.info("[NUKE] Initialized mk3 explosion at {} / {} / {} with strength {}!",getX(),getY(),getZ(),destructionRange);}
   initialize();SatelliteBursts.reportEvent(level(),SatelliteBursts.DURATION_HIGH,SatelliteBursts.BurstIntensity.HIGH,getX(),getZ());did=true;
  }
  speed++;boolean flag=waste&&destructionComplete;
  if(!level().isClientSide){
   long deadline=NuclearTickBudget.deadline(level(),this);
   for(int completed=0;completed<speed&&System.nanoTime()<deadline;){
    if(waste){
     if(phase==0){flag=exp.updateUntil(deadline);destructionComplete=flag;if(exp.yielded)break;phase=1;}
     if(phase==1){wst.updateUntil(deadline);if(wst.yielded)break;phase=2;}
     if(phase==2){boolean finished=vap.updateUntil(deadline);if(vap.yielded)break;phase=0;completed++;if(finished){discard();break;}}
    }else if(extType==0){boolean finished=expl.updateUntil(deadline);if(expl.yielded)break;completed++;if(finished){discard();break;}}
    else if(extType==1){boolean finished=sol.updateUntil(deadline);if(sol.yielded)break;completed++;if(finished){discard();break;}}
    else completed++;
   }
  }
  if(!flag){
   float pitch=.8F+random.nextFloat()*.2F;
   if(level().isClientSide)level().playLocalSound(getX(),getY(),getZ(),SoundEvents.LIGHTNING_BOLT_THUNDER,SoundSource.WEATHER,10000F,pitch,false);else level().playSound(null,getX(),getY(),getZ(),SoundEvents.LIGHTNING_BOLT_THUNDER,SoundSource.WEATHER,10000F,pitch);
   if(waste||extType!=1)ExplosionNukeGeneric.dealDamage(level(),getX(),getY(),getZ(),destructionRange*2);
   else ExplosionHurtUtil.doRadiation(level(),getX(),getY(),getZ(),15000,250000,destructionRange);
  }else if(!did2&&waste){var fallout=new NuclearFalloutRain(NuclearContent.FALLOUT_RAIN.get(),level());fallout.setPos(position());fallout.setScale((int)(destructionRange*1.8));level().addFreshEntity(fallout);did2=true;}
  age++;
 }
 @Override protected void addAdditionalSaveData(CompoundTag tag){
  saveTicket(tag);tag.putInt("age",age);tag.putInt("destructionRange",destructionRange);tag.putInt("speed",speed);tag.putFloat("coefficient",coefficient);tag.putFloat("coefficient2",coefficient2);tag.putBoolean("did",did);tag.putBoolean("did2",did2);tag.putBoolean("waste",waste);tag.putInt("extType",extType);tag.putLong("milliTime",System.currentTimeMillis());tag.putInt("hbmPhase",phase);tag.putBoolean("hbmDestructionComplete",destructionComplete);
  if(exp!=null)exp.saveToNbt(tag,"exp_");if(wst!=null)wst.saveToNbt(tag,"wst_");if(vap!=null)vap.saveToNbt(tag,"vap_");if(expl!=null)expl.saveToNbt(tag,"expl_");if(sol!=null)sol.saveToNbt(tag,"sol_");
 }
 @Override protected void readAdditionalSaveData(CompoundTag tag){
  closeWorkers();
  loadTicket(tag);age=tag.getInt("age");destructionRange=tag.getInt("destructionRange");speed=tag.getInt("speed");coefficient=tag.getFloat("coefficient");coefficient2=tag.getFloat("coefficient2");did=tag.getBoolean("did");did2=tag.getBoolean("did2");waste=tag.getBoolean("waste");extType=tag.getInt("extType");phase=tag.getInt("hbmPhase");destructionComplete=tag.getBoolean("hbmDestructionComplete");
  int limit=NuclearConfig.EXPLOSION_LIFESPAN.get();if(limit>0&&System.currentTimeMillis()-tag.getLong("milliTime")>limit*1000L)discard();
  initialize();if(exp!=null)exp.readFromNbt(tag,"exp_");if(wst!=null)wst.readFromNbt(tag,"wst_");if(vap!=null)vap.readFromNbt(tag,"vap_");if(expl!=null)expl.readFromNbt(tag,"expl_");if(sol!=null)sol.readFromNbt(tag,"sol_");did=true;
 }
 public static NuclearExplosionMK3 statFacFleija(Level level,double x,double y,double z,int range){
  var entity=new NuclearExplosionMK3(NuclearContent.MK3.get(),level);entity.setPos(x,y,z);entity.destructionRange=range;entity.speed=NuclearConfig.BLAST_SPEED.get();entity.coefficient=1;entity.waste=false;
  synchronized(at){var iterator=at.entrySet().iterator();while(iterator.hasNext()){
   var next=iterator.next();if(next.getValue()<level.getGameTime()){iterator.remove();continue;}var entry=next.getKey();if(entry.dimension!=level.dimension())continue;
   if(LegacyVector.createVectorHelper(x-entry.x,y-entry.y,z-entry.z).lengthVector()<300){entity.discard();if(!level.isClientSide)for(int i=0;i<2;i++){double ix=i==0?x:entry.x+.5,iy=i==0?y:entry.y+.5,iz=i==0?z:entry.z+.5;level.playSound(null,ix,iy,iz,HbmSounds.get("entity.ufoBlast"),SoundSource.BLOCKS,15F,.7F+level.random.nextFloat()*.2F);NuclearPlasmaBlastEffects.send(level,ix,iy,iz,0,.75F,1,7.5F,0,0);}break;}
  }}
  if(!entity.isRemoved())entity.loadChunk(Mth.floor(x/16D),Mth.floor(z/16D));return entity;
 }
 public NuclearExplosionMK3 makeSol(){extType=1;return this;}
 public static final class ATEntry {
  public ResourceKey<Level> dimension;public int x,y,z;
  public ATEntry(ResourceKey<Level> dimension,int x,int y,int z){this.dimension=dimension;this.x=x;this.y=y;this.z=z;}
  @Override public int hashCode(){int dim=dimension==Level.OVERWORLD?0:dimension==Level.NETHER?-1:dimension==Level.END?1:dimension.hashCode();int h=1;h=27644437*h+dim;h=27644437*h+x;h=27644437*h+y;return 27644437*h+z;}
  @Override public boolean equals(Object other){return this==other||other instanceof ATEntry e&&dimension==e.dimension&&x==e.x&&y==e.y&&z==e.z;}
 }
}
