package com.hbm.bomb.nuclear;

import com.hbm.explosion.ExplosionBalefire;
import com.hbm.explosion.ExplosionNukeGeneric;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Original EntityBalefire: accelerating spiral terrain erosion and continuous nuclear damage. */
public final class NuclearBalefireExplosion extends NuclearChunkloadingEntity implements NuclearWork {
 public int age,destructionRange,speed=1;public boolean did;public ExplosionBalefire explosion;
 public NuclearBalefireExplosion(EntityType<? extends NuclearBalefireExplosion> type,Level level){super(type,level);}
 @Override public boolean hasNuclearWork(){return !isRemoved()&&destructionRange>0;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override public void remove(RemovalReason reason){if(explosion!=null)explosion.close();super.remove(reason);}
 @Override public void tick(){
  if(level().isClientSide)return;
  loadChunk(Mth.floor(getX()/16D),Mth.floor(getZ()/16D));
  if(!did){com.hbm.logging.SourceActionLog.info("[NUKE] Initialized BF explosion at {} / {} / {} with strength {}!",getX(),getY(),getZ(),destructionRange);explosion=new ExplosionBalefire((int)getX(),(int)getY(),(int)getZ(),level(),destructionRange);did=true;}
  speed++;long deadline=NuclearTickBudget.deadline(level(),this);
  for(int i=0;i<speed&&System.nanoTime()<deadline;i++){
   if(explosion.updateUntil(deadline)){discard();age++;return;}
   if(explosion.yielded)break;
  }
  ExplosionNukeGeneric.dealDamage(level(),getX(),getY(),getZ(),destructionRange*2);age++;
 }

 @Override protected void addAdditionalSaveData(CompoundTag tag){saveTicket(tag);tag.putInt("age",age);tag.putInt("destructionRange",destructionRange);tag.putInt("speed",speed);tag.putBoolean("did",did);if(explosion!=null)explosion.saveToNbt(tag,"exp_");}
 @Override protected void readAdditionalSaveData(CompoundTag tag){loadTicket(tag);age=tag.getInt("age");destructionRange=tag.getInt("destructionRange");speed=tag.getInt("speed");did=true;explosion=new ExplosionBalefire((int)getX(),(int)getY(),(int)getZ(),level(),destructionRange);explosion.readFromNbt(tag,"exp_");}
}
