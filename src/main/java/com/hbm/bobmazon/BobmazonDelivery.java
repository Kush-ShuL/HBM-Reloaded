package com.hbm.bobmazon;

import com.hbm.bomb.conventional.SmokeBurstEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Exact original four half-block downward substeps and non-air contact delivery, with persistent full payload. */
public final class BobmazonDelivery extends Entity {
 public ItemStack payload=ItemStack.EMPTY;
 public BobmazonDelivery(EntityType<? extends BobmazonDelivery> type,Level level){super(type,level);noCulling=true;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override public void tick(){
  setDeltaMovement(0,-.5,0);xo=xOld=getX();yo=yOld=getY();zo=zOld=getZ();
  for(int i=0;i<4;i++){
   // Preserve Java's original integer truncation, including negative coordinates; no floor substitution.
   var contact=new BlockPos((int)(getX()-.5),(int)(getY()+1),(int)(getZ()-.5));
   if(!level().isClientSide&&!level().getBlockState(contact).isAir()){
    SmokeBurstEffects.cloud(level(),getX(),getY()+1,getZ(),50);level().playSound(null,getX(),getY(),getZ(),com.hbm.registry.HbmSounds.get("entity.oldExplosion"),SoundSource.BLOCKS,10,.5F+random.nextFloat()*.1F);
    if(!payload.isEmpty()){var pack=new ItemEntity(level(),getX(),getY()+2,getZ(),payload.copy());pack.setDeltaMovement(0,pack.getDeltaMovement().y,0);level().addFreshEntity(pack);}discard();break;
   }
   setPos(getX(),getY()-.5,getZ());
  }
  if(level().isClientSide)BobmazonContent.CLIENT_EXHAUST.accept(this);
 }
 @Override protected void readAdditionalSaveData(CompoundTag tag){payload=ItemStack.parseOptional(registryAccess(),tag.getCompound("payload"));}
 @Override protected void addAdditionalSaveData(CompoundTag tag){if(!payload.isEmpty())tag.put("payload",payload.save(registryAccess()));}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<500000;}
}
