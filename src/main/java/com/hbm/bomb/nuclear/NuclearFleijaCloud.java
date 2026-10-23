package com.hbm.bomb.nuclear;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** Original age/scale and maxAge watcher, including the intentionally unpersisted watcher value. */
public class NuclearFleijaCloud extends Entity implements LegacyTrackingRange,net.neoforged.neoforge.entity.IEntityWithComplexSpawn {
 private static final EntityDataAccessor<Integer> MAX_AGE=SynchedEntityData.defineId(NuclearFleijaCloud.class,EntityDataSerializers.INT);
 public int maxAge=100,age;public float scale;private boolean parameterized;
 public NuclearFleijaCloud(EntityType<? extends NuclearFleijaCloud> type,Level level){super(type,level);}
 public NuclearFleijaCloud(EntityType<? extends NuclearFleijaCloud> type,Level level,int maxAge){this(type,level);parameterized=true;refreshDimensions();setMaxAge(maxAge);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(MAX_AGE,0);}
 public void setMaxAge(int maxAge){entityData.set(MAX_AGE,maxAge);}
 public int getMaxAge(){return entityData.get(MAX_AGE);}
 @Override public void writeSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeInt(age);buffer.writeFloat(scale);buffer.writeInt(getMaxAge());}
 @Override public void readSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer){age=buffer.readInt();scale=buffer.readFloat();setMaxAge(buffer.readInt());}
 protected boolean solinium(){return false;}
 @Override public int legacyTrackingRange(){return 500;}
 @Override public EntityDimensions getDimensions(Pose pose){return parameterized?EntityDimensions.scalable(20,40):super.getDimensions(pose);}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return solinium()?super.shouldRenderAtSqrDistance(distance):distance<25000;}
 @Override public void tick(){age++;if(solinium()){var lightning=EntityType.LIGHTNING_BOLT.create(level());lightning.setPos(getX(),getY()+200,getZ());level().addFreshEntity(lightning);}else if(level().isClientSide)com.hbm.client.bomb.nuclear.NuclearClientEffects.lightningFlash();if((solinium()||!level().isClientSide)&&age>=getMaxAge()){age=0;discard();}scale++;}
 @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putShort("age",(short)age);tag.putShort("scale",(short)scale);}
 @Override protected void readAdditionalSaveData(CompoundTag tag){age=tag.getShort("age");scale=tag.getShort("scale");}
}
