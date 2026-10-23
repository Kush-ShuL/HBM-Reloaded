package com.hbm.satellite.attack;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** Source EntityOrbitalLaser: actual CrossSmooth1000 LASER/piercing50/.5 and5tick visual lifetime. */
public final class OrbitalLaserEntity extends Entity {
 public static final int MAX_AGE=5;
 public OrbitalLaserEntity(EntityType<? extends OrbitalLaserEntity> type,Level level){super(type,level);noCulling=true;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override protected void readAdditionalSaveData(CompoundTag tag){}
 @Override protected void addAdditionalSaveData(CompoundTag tag){}
 @Override public void tick(){if(tickCount>=MAX_AGE&&!level().isClientSide)discard();}
 public void explode(){var blast=new ExplosionVNT(level(),getX(),getY(),getZ(),5F);blast.setBlockAllocator(new BlockAllocatorStandard());blast.setBlockProcessor(new BlockProcessorStandard());blast.setEntityProcessor(new EntityProcessorCrossSmooth(1,1000F).setupPiercing(50F,.5F).setDamageClass(DamageClass.LASER));blast.setPlayerProcessor(new PlayerProcessorStandard());blast.setSFX(new ExplosionEffectWeapon(15,3.5F,1.25F));blast.explode();}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
}
