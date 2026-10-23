package com.hbm.world.meteor;

import api.hbm.entity.IRadarDetectableNT;
import com.hbm.damage.DamageResistanceHandler;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.registry.*;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.Vec3;

/** Source EntityMeteor movement, weak-block path and buried original Meteorite generator. */
public final class MeteorEntity extends Entity implements IRadarDetectableNT,LegacyTrackingRange {
 public static Consumer<MeteorEntity> CLIENT_TICK=meteor->{},CLIENT_REMOVE=meteor->{};
 private static final Set<Block> WEAK_TREES=Set.of(Blocks.OAK_LEAVES,Blocks.SPRUCE_LEAVES,Blocks.BIRCH_LEAVES,Blocks.JUNGLE_LEAVES,Blocks.OAK_LOG,Blocks.SPRUCE_LOG,Blocks.BIRCH_LOG,Blocks.JUNGLE_LOG);
 public boolean safe;
 public MeteorEntity(EntityType<? extends MeteorEntity> type,Level level){super(type,level);noCulling=true;}
 public MeteorEntity(Level level){this(MeteorContent.METEOR.get(),level);}
 @Override public int legacyTrackingRange(){return 250;}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return true;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override protected void readAdditionalSaveData(CompoundTag tag){safe=tag.getBoolean("safe");}
 @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putBoolean("safe",safe);}
 @Override public boolean hurt(DamageSource source,float amount){if(!level().isClientSide&&amount>=250&&DamageResistanceHandler.CATEGORY_ENERGY.equals(DamageResistanceHandler.typeToCategory(source)))discard();return false;}
 @Override public void remove(RemovalReason reason){super.remove(reason);if(level().isClientSide)CLIENT_REMOVE.accept(this);}
 @Override public void tick(){
  if(!level().isClientSide&&!MeteorConfig.enabled()){discard();return;}
  xo=getX();yo=getY();zo=getZ();
  Vec3 velocity=getDeltaMovement();setDeltaMovement(velocity.x,Math.max(velocity.y-.03,-2.5),velocity.z);
  move(MoverType.SELF,getDeltaMovement());
  // Source260 is the original256 build limit plus4; modern mountains can reach320.
  if(level() instanceof ServerLevel server&&getY()<level().getMaxBuildHeight()+4){
   clearMeteorPath((int)getX(),(int)getY(),(int)getZ());
   if(onGround()){
    server.explode(this,getX(),getY(),getZ(),5+random.nextFloat(),safe?Level.ExplosionInteraction.NONE:Level.ExplosionInteraction.TNT);
    if(MeteorConfig.TAILS.get()){
     ExplosionLarge.spawnRubble(server,getX(),getY(),getZ(),15);
     ExplosionLarge.spawnParticles(server,getX(),getY()+5,getZ(),75);
     ExplosionLarge.spawnParticles(server,getX()+5,getY(),getZ(),75);ExplosionLarge.spawnParticles(server,getX()-5,getY(),getZ(),75);
     ExplosionLarge.spawnParticles(server,getX(),getY(),getZ()+5,75);ExplosionLarge.spawnParticles(server,getX(),getY(),getZ()-5,75);
    }
    // Both horizontal source expressions deliberately use motionZ. Entity.move clears
    // only collided components, as the original moveEntity implementation did.
    int x=(int)(Math.round(getX()-.5)+(safe?0:getDeltaMovement().z*4));
    int y=(int)Math.round(getY()-(safe?0:4));
    int z=(int)(Math.round(getZ()-.5)+(safe?0:getDeltaMovement().z*4));
    new MeteoriteGenerator().generate(server,random,x,y,z,safe,true,true);clearMeteorPath(x,y,z);
    server.playSound(null,getX(),getY(),getZ(),HbmSounds.get("entity.oldExplosion"),SoundSource.HOSTILE,10000,.5F+random.nextFloat()*.1F);discard();
   }
  }
  if(level().isClientSide)CLIENT_TICK.accept(this);
 }
 private void clearMeteorPath(int x,int y,int z){
  if(safe)return;
  var pos=new BlockPos.MutableBlockPos();
  for(int dx=-5;dx<=5;dx++)for(int dy=-5;dy<=5;dy++)for(int dz=-5;dz<=5;dz++)if(dx*dx+dy*dy+dz*dz<=25){
   pos.set(x+dx,y+dy,z+dz);var state=level().getBlockState(pos);var block=state.getBlock();float hardness=state.getDestroySpeed(level(),pos);
   if(WEAK_TREES.contains(block)||(hardness>=0&&hardness<=.3F))level().removeBlock(pos,false);
   else if(hardness>=0&&hardness<=5&&random.nextInt(6)==1){
    Block replacement=null;
    if(block==Blocks.DIRT)replacement=HbmContent.block("dirt_dead");
    else if(block==Blocks.SAND)replacement=random.nextInt(2)==1?Blocks.SANDSTONE:Blocks.GLASS;
    else if(block==Blocks.STONE)replacement=Blocks.COBBLESTONE;
    else if(block==Blocks.GRASS_BLOCK)replacement=HbmContent.block("waste_earth");
    if(replacement!=null)level().setBlock(pos,replacement.defaultBlockState(),3);
   }
  }
 }
 @Override public String getUnlocalizedName(){return "radar.target.meteor";}
 @Override public int getBlipLevel(){return SPECIAL;}
 @Override public boolean canBeSeenBy(Object radar){return true;}
 @Override public boolean paramsApplicable(RadarScanParams params){return params.scanMissiles;}
 @Override public boolean suppliesRedstone(RadarScanParams params){return params.scanMissiles;}
}
