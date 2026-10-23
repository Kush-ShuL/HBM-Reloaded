package com.hbm.satellite.attack;
import com.hbm.bomb.nuclear.*;
import com.hbm.registry.HbmSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** Source EntityDeathBlast:60ticks before the exact no-radiation40 MK5, not a generic explosion. */
public final class DeathBlastEntity extends Entity {
 public static final int MAX_AGE=60;
 public DeathBlastEntity(EntityType<? extends DeathBlastEntity> type,Level level){super(type,level);noCulling=true;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override protected void readAdditionalSaveData(CompoundTag tag){}
 @Override protected void addAdditionalSaveData(CompoundTag tag){}
 @Override public void tick(){if(tickCount>=MAX_AGE&&!level().isClientSide){discard();level().addFreshEntity(NuclearExplosionMK5.statFacNoRad(level(),40,getX(),getY(),getZ()));NuclearMukeEffects.sendAtOrigin(level(),getX(),getY()+.5,getZ(),"muke",false,getX(),getY(),getZ());level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("weapon.mukeExplosion"),SoundSource.NEUTRAL,25F,.9F);}}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
}
