package com.hbm.bomb.nuclear;

import com.hbm.block.falling.LegacyFallingBlock;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.explosion.LegacyExplosionMath;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/** EntityFallingBlockNT, separate from vanilla's falling entity and retaining its original double constants. */
public final class NuclearFallingBlock extends Entity {
 private static final EntityDataAccessor<BlockState> STATE=SynchedEntityData.defineId(NuclearFallingBlock.class,EntityDataSerializers.BLOCK_STATE);
 public int fallingTicks;public boolean canDrop=true;public CompoundTag tileNBT;
 private boolean destroyOnLand,canHurtEntities,parameterized;
 private int damageCap=40;private float damageAmount=2F;
 public NuclearFallingBlock(EntityType<? extends NuclearFallingBlock> type,Level level){super(type,level);}
 public NuclearFallingBlock(Level level,double x,double y,double z,BlockState state){this(NuclearContent.FALLING_BLOCK.get(),level);parameterized=true;refreshDimensions();entityData.set(STATE,state);blocksBuilding=true;setPos(x,y,z);setDeltaMovement(Vec3.ZERO);xo=x;yo=y;zo=z;}
 @Override public EntityDimensions getDimensions(Pose pose){return parameterized?EntityDimensions.fixed(.6F,1.8F):EntityDimensions.fixed(.98F,.98F);}
 public BlockState carried(){return entityData.get(STATE);}
 /** Constructor(world) uses yOffset=.49, but the source's parameterized constructor intentionally does not. */
 public double legacyY(){return getY()+(parameterized?0:.49D);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(STATE,Blocks.AIR.defaultBlockState());}
 @Override public void recreateFromPacket(net.minecraft.network.protocol.game.ClientboundAddEntityPacket packet){super.recreateFromPacket(packet);if(!parameterized)setPos(getX(),getY()-.49D,getZ());}
 @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int steps){super.lerpTo(x,parameterized?y:y-.49D,z,yaw,pitch,steps);}
 @Override public boolean canBeCollidedWith(){return !isRemoved();}
 @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}
 @Override public boolean displayFireAnimation(){return false;}
 @Override public void tick(){
  var state=carried();if(LegacyMaterials.of(state).name().equals("air")||state.getBlock() instanceof Spotlight){discard();return;}
  xo=getX();yo=getY();zo=getZ();++fallingTicks;setDeltaMovement(getDeltaMovement().add(0,-.04D,0));move(MoverType.SELF,getDeltaMovement());setDeltaMovement(getDeltaMovement().scale(.98D));
  if(level().isClientSide)return;var pos=BlockPos.containing(getX(),legacyY(),getZ());
  if(fallingTicks==1){if(!level().getBlockState(pos).is(state.getBlock())){discard();return;}level().removeBlock(pos,false);}
  if(onGround()){
   setDeltaMovement(getDeltaMovement().multiply(.7D,-.5D,.7D));if(level().getBlockState(pos).is(Blocks.MOVING_PISTON))return;discard();
   if(!destroyOnLand&&replacementCheck(pos)&&level().setBlock(pos,state,3)){
    if(state.getBlock() instanceof LegacyFallingBlock block)block.legacyLanded(level(),pos,LegacyExplosionMath.metadata(state));
    if(state.getBlock() instanceof LandingCallback callback)callback.onLand(level(),pos,state);
    if(tileNBT!=null&&state.hasBlockEntity()&&level().getBlockEntity(pos)!=null){var be=level().getBlockEntity(pos);var tag=be.saveWithoutMetadata(level().registryAccess());for(String key:tileNBT.getAllKeys())if(!key.equals("x")&&!key.equals("y")&&!key.equals("z"))tag.put(key,tileNBT.get(key).copy());be.loadWithComponents(tag,level().registryAccess());be.setChanged();}
   }else if(canDrop&&!destroyOnLand)dropOriginal(pos);
  }else if(fallingTicks>100&&(pos.getY()<level().getMinBuildHeight()+1||pos.getY()>level().getMaxBuildHeight())||fallingTicks>600){if(canDrop)dropOriginal(pos);discard();}
 }
 public boolean replacementCheck(BlockPos pos){return LegacyMaterials.of(level().getBlockState(pos)).isReplaceable()&&carried().canSurvive(level(),pos);}
 private void dropOriginal(BlockPos pos){if(!(level() instanceof net.minecraft.server.level.ServerLevel server))return;var drops=Block.getDrops(carried(),server,pos,null,this,ItemStack.EMPTY);if(!drops.isEmpty()){var stack=drops.getFirst().copyWithCount(1);spawnAtLocation(stack,0);}}
 @Override public boolean causeFallDamage(float distance,float multiplier,DamageSource ignored){
  if(!canHurtEntities)return false;int blocks=Mth.ceil(distance-1F);if(blocks<=0)return false;boolean anvil=carried().is(net.minecraft.tags.BlockTags.ANVIL);var damage=anvil?damageSources().anvil(this):damageSources().fallingBlock(this);for(Entity entity:level().getEntities(this,getBoundingBox()))entity.hurt(damage,Math.min(Mth.floor(blocks*damageAmount),damageCap));
  if(anvil&&random.nextFloat()<.05D+blocks*.05D){int metadata=LegacyExplosionMath.metadata(carried()),stage=(metadata>>2)+1;if(stage>2)destroyOnLand=true;else{var next=AnvilBlock.damage(carried());if(next!=null)entityData.set(STATE,next);}}return false;
 }
 public void setHurtEntities(boolean value){canHurtEntities=value;}
 @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putDouble("hbmLogicalY",legacyY());tag.put("BlockState",NbtUtils.writeBlockState(carried()));tag.putByte("Tile",(byte)Block.getId(carried()));tag.putInt("TileID",Block.getId(carried()));tag.putByte("Data",(byte)LegacyExplosionMath.metadata(carried()));tag.putByte("Time",(byte)fallingTicks);tag.putBoolean("DropItem",canDrop);tag.putBoolean("HurtEntities",canHurtEntities);tag.putFloat("FallHurtAmount",damageAmount);tag.putInt("FallHurtMax",damageCap);if(tileNBT!=null)tag.put("TileEntityData",tileNBT);}
 @Override protected void readAdditionalSaveData(CompoundTag tag){setPos(getX(),(tag.contains("hbmLogicalY",99)?tag.getDouble("hbmLogicalY"):getY())-.49D,getZ());var state=tag.contains("BlockState",10)?NbtUtils.readBlockState(level().holderLookup(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("BlockState")):Block.stateById(tag.contains("TileID",99)?tag.getInt("TileID"):tag.getByte("Tile")&255);if(state.isAir())state=Blocks.SAND.defaultBlockState();entityData.set(STATE,state);fallingTicks=tag.getByte("Time")&255;if(tag.contains("HurtEntities",99)){canHurtEntities=tag.getBoolean("HurtEntities");damageAmount=tag.getFloat("FallHurtAmount");damageCap=tag.getInt("FallHurtMax");}else if(state.is(net.minecraft.tags.BlockTags.ANVIL))canHurtEntities=true;if(tag.contains("DropItem",99))canDrop=tag.getBoolean("DropItem");if(tag.contains("TileEntityData",10))tileNBT=tag.getCompound("TileEntityData");}
 public interface Spotlight{}
 public interface LandingCallback{void onLand(Level level,BlockPos pos,BlockState state);}
}
