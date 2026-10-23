package com.hbm.bomb.nuclear;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.explosion.IExplosionRay;
import com.hbm.explosion.ExplosionNukeRayBatched;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.exposure.HbmExposure;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** X5846's actual cached spiral-ray MK5 explosion and subsequent fallout entity. */
public final class NuclearExplosionMK5 extends NuclearChunkloadingEntity implements NuclearWork {
 public int strength,speed,length;
 public boolean fallout=true;
 private int falloutAdd;
 private boolean falloutStarted;
 private IExplosionRay explosion;
 /** Observers may consume the same real source burst; the repository is always updated below. */
 public static final List<Consumer<NuclearExplosionMK5>> HIGH_BURST=new CopyOnWriteArrayList<>();
 public NuclearExplosionMK5(EntityType<? extends NuclearExplosionMK5> type,Level level){super(type,level);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override public void tick(){
  if(level().isClientSide)return;
  if(strength==0){discard();return;}
  if(!level().isClientSide)loadChunk(Mth.floor(getX()/16D),Mth.floor(getZ()/16D));
  if(!level().isClientSide&&fallout&&explosion!=null&&tickCount<10&&strength>=75)radiate(2_500_000F/(tickCount*5+1),length*2);
  if(level() instanceof net.minecraft.server.level.ServerLevel server){var advancement=server.getServer().getAdvancements().get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm","manhattan"));if(advancement!=null)for(var player:server.players())player.getAdvancements().award(advancement,"detonated");}
  ExplosionNukeGeneric.dealDamage(level(),getX(),getY(),getZ(),length*2);
  if(explosion==null){explosion=new ExplosionNukeRayBatched(level(),(int)getX(),(int)getY(),(int)getZ(),strength,speed,length);SatelliteBursts.reportEvent(level(),SatelliteBursts.DURATION_HIGH,SatelliteBursts.BurstIntensity.HIGH,getX(),getZ());for(var event:HIGH_BURST)event.accept(this);}
  if(!explosion.isComplete()){
   long deadline=NuclearTickBudget.deadline(level(),this);
   if(explosion instanceof ExplosionNukeRayBatched batched){
    long now=System.nanoTime();long cacheDeadline=batched.isAusf3Complete?now:now+Math.max(0,deadline-now)/2;
    batched.cacheUntil(cacheDeadline);batched.destroyUntil(deadline);
    // A quiet or cold write queue may yield early; return this worker's remaining grant to its real rays.
    if(System.nanoTime()<deadline&&!batched.isAusf3Complete)batched.cacheUntil(deadline);
   }
   else if(System.nanoTime()<deadline){int remaining=(int)Math.max(1,(deadline-System.nanoTime())/1_000_000L);explosion.cacheChunksTick(remaining);remaining=(int)Math.max(0,(deadline-System.nanoTime())/1_000_000L);if(remaining>0)explosion.destructionTick(remaining);}
  }
  else {
   // Source MK5 starts its full fallout sweep only after all terrain work is done.
   if(fallout&&!falloutStarted){var rain=new NuclearFalloutRain(NuclearContent.FALLOUT_RAIN.get(),level());rain.setPos(position());rain.setScale((int)(length*2.5+falloutAdd)*NuclearConfig.FALLOUT_RANGE.get()/100);rain.followBlast(this);falloutStarted=level().addFreshEntity(rain);}
   discard();
  }
 }
 public boolean isTerrainComplete(int chunkX,int chunkZ){return explosion instanceof ExplosionNukeRayBatched batched?batched.isTerrainComplete(chunkX,chunkZ):explosion!=null&&explosion.isComplete();}
 public boolean isTracingComplete(){return explosion instanceof ExplosionNukeRayBatched batched?batched.isAusf3Complete:explosion!=null&&explosion.isComplete();}
 private void radiate(float rads,double range){
  for(var living:level().getEntitiesOfClass(LivingEntity.class,new AABB(getX(),getY(),getZ(),getX(),getY(),getZ()).inflate(range))){
   var vector=LegacyVector.createVectorHelper(living.getX()-getX(),living.getEyeY()-getY(),living.getZ()-getZ());double len=vector.lengthVector();vector=vector.normalize();float resistance=0;
   for(int i=1;i<len;i++){var pos=new BlockPos(Mth.floor(getX()+vector.xCoord*i),Mth.floor(getY()+vector.yCoord*i),Mth.floor(getZ()+vector.zCoord*i));resistance+=level().getBlockState(pos).getBlock().getExplosionResistance();}
   if(resistance<1)resistance=1;float dose=rads;dose/=resistance;dose/=(float)(len*len);HbmExposure.contaminate(living,HbmExposure.Contamination.RAD_BYPASS,dose);
  }
 }
 @Override public void remove(RemovalReason reason){if(explosion!=null){if(reason.shouldDestroy()||reason==RemovalReason.CHANGED_DIMENSION)explosion.cancel();else if(explosion instanceof ExplosionNukeRayBatched batched)batched.suspend();}super.remove(reason);}
 @Override protected void readAdditionalSaveData(CompoundTag tag){tickCount=tag.getInt("ticksExisted");strength=tag.getInt("strength");speed=tag.getInt("speed");length=tag.getInt("length");fallout=!tag.contains("fallout")||tag.getBoolean("fallout");falloutAdd=tag.getInt("falloutAdd");falloutStarted=tag.getBoolean("falloutStarted");loadTicket(tag);}
 @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putInt("ticksExisted",tickCount);tag.putInt("strength",strength);tag.putInt("speed",speed);tag.putInt("length",length);tag.putBoolean("fallout",fallout);tag.putInt("falloutAdd",falloutAdd);tag.putBoolean("falloutStarted",falloutStarted);saveTicket(tag);}
 public static NuclearExplosionMK5 statFac(Level level,int r,double x,double y,double z){if(r==0)r=25;r*=2;var entity=new NuclearExplosionMK5(NuclearContent.MK5.get(),level);entity.strength=r;entity.speed=(int)Math.ceil(100000/entity.strength);entity.length=entity.strength/2;entity.setPos(x,y,z);entity.loadChunk(Mth.floor(x/16D),Mth.floor(z/16D));return entity;}
 public static NuclearExplosionMK5 statFacNoRad(Level level,int r,double x,double y,double z){var entity=statFac(level,r,x,y,z);entity.fallout=false;return entity;}
 public NuclearExplosionMK5 moreFallout(int extra){falloutAdd=extra;return this;}
}
