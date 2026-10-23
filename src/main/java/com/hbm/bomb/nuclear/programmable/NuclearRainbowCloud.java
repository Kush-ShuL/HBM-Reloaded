package com.hbm.bomb.nuclear.programmable;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** Original EntityCloudFleijaRainbow with actual age/scale/maxAge spawn phase and source per-tick lightning. */
public final class NuclearRainbowCloud extends Entity implements LegacyTrackingRange,net.neoforged.neoforge.entity.IEntityWithComplexSpawn {
 private static final EntityDataAccessor<Integer> MAX_AGE=SynchedEntityData.defineId(NuclearRainbowCloud.class,EntityDataSerializers.INT);
 public int maxAge=100,age;public float scale;private boolean parameterized;
 public NuclearRainbowCloud(EntityType<? extends NuclearRainbowCloud> type,Level level){super(type,level);}
 public NuclearRainbowCloud(EntityType<? extends NuclearRainbowCloud> type,Level level,int maxAge){this(type,level);parameterized=true;refreshDimensions();setMaxAge(maxAge);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(MAX_AGE,0);}
 public void setMaxAge(int maxAge){entityData.set(MAX_AGE,maxAge);}
 public int getMaxAge(){return entityData.get(MAX_AGE);}
 @Override public void writeSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeInt(age);buffer.writeFloat(scale);buffer.writeInt(getMaxAge());}
 @Override public void readSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer){age=buffer.readInt();scale=buffer.readFloat();setMaxAge(buffer.readInt());}
 public NuclearRainbowCloud(Level level,int maxAge){this(ProgrammableNuclearContent.RAINBOW.get(),level,maxAge);}
 @Override public int legacyTrackingRange(){return 1000;}
 @Override public EntityDimensions getDimensions(Pose pose){return parameterized?EntityDimensions.scalable(20,40):super.getDimensions(pose);}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
 @Override public void tick(){age++;var lightning=EntityType.LIGHTNING_BOLT.create(level());lightning.setPos(getX(),getY()+200,getZ());level().addFreshEntity(lightning);if(age>=getMaxAge()){age=0;discard();}scale++;}
 @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putShort("age",(short)age);tag.putShort("scale",(short)scale);}
 @Override protected void readAdditionalSaveData(CompoundTag tag){age=tag.getShort("age");scale=tag.getShort("scale");}
}
