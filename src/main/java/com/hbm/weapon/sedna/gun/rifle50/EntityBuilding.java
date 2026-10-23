package com.hbm.weapon.sedna.gun.rifle50;
import com.hbm.bomb.conventional.RubbleProjectile;
import com.hbm.damage.HbmDamageSources;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.specialvehicle.VehicleShockEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
/** Original EntityBuilding's manual falling simulation, impact, 1000 damage and 250 real brick rubble projectiles. */
public final class EntityBuilding extends EntityThrowableNT implements LegacyTrackingRange {
 public EntityBuilding(Level level){this(Rifle50Entities.BUILDING.get(),level);}
 public EntityBuilding(EntityType<?> type,Level level){super(type,level);noCulling=true;}
 @Override public void tick(){
  if(!level().isClientSide&&tickCount==1)for(int i=0;i<100;i++)VehicleShockEffects.balefire(level(),getX()+(random.nextDouble()-.5)*15,getY()+(random.nextDouble()-.5)*15,getZ()+(random.nextDouble()-.5)*15,getX(),getY(),getZ());
  xOld=xo=getX();yOld=yo=getY();zOld=zo=getZ();var motion=getDeltaMovement();setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);setDeltaMovement(motion.x,Math.max(motion.y-.03D,-1.5D),motion.z);
  // Source casts deliberately truncate, including negative coordinates; it does not floor or sweep.
  if(!level().getBlockState(new BlockPos((int)getX(),(int)getY(),(int)getZ())).isAir())impact();
 }
 private void impact(){discard();if(level().isClientSide)return;
  level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("entity.oldExplosion"),SoundSource.NEUTRAL,10000F,.5F+random.nextFloat()*.1F);
  ExplosionLarge.spawnParticles(level(),getX(),getY()+3,getZ(),150);
  for(double strength:new double[]{6,5,4,3,3})VehicleShockEffects.shock(level(),getX(),getY()+1,getZ(),24,strength);
  for(var entity:level().getEntities(null,new AABB(getX()-8,getY()-8,getZ()-8,getX()+8,getY()+8,getZ()+8)))entity.hurt(HbmDamageSources.source(level(),HbmDamageSources.building),1000F);
  for(int i=0;i<250;i++){
   // Original Vec3.rotateAroundZ is clockwise. Its negative input makes these fragments travel upward.
   float pitch=(float)(-random.nextFloat()*Math.PI/2),yaw=(float)(random.nextFloat()*Math.PI*2);
   double x=Mth.cos(pitch),y=-Mth.sin(pitch),z=0,next=x*Mth.cos(yaw)+z*Mth.sin(yaw);z=z*Mth.cos(yaw)-x*Mth.sin(yaw);x=next;
   var rubble=new RubbleProjectile(level(),getX(),getY()+3,getZ());rubble.setMaterial(Blocks.BRICKS.defaultBlockState());rubble.setDeltaMovement(x,y,z);level().addFreshEntity(rubble);
  }
 }
 @Override protected void onImpact(HitResult ignored){}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
 @Override public int legacyTrackingRange(){return 1000;}
}
