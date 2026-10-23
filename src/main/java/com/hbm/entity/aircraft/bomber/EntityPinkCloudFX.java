package com.hbm.entity.aircraft.bomber;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.bomb.conventional.MistExposure;
import com.hbm.damage.HbmDamageSources;
import com.hbm.entity.particle.EntityModFX;
import com.hbm.machine.pinkcloud.*;
import com.hbm.world.structure.templates.radio.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
/** Actual missing persistent source cloud, not EntityMist and not a client-only visual particle. */
public final class EntityPinkCloudFX extends EntityModFX {
 public EntityPinkCloudFX(EntityType<? extends EntityPinkCloudFX> type,Level level){super(type,level,0,0,0);}
 public EntityPinkCloudFX(Level level,double x,double y,double z,double mx,double my,double mz){
  super(BomberContent.PINK.get(),level,x,y,z,0,0,0);setDeltaMovement(getDeltaMovement().scale(.10000000149011612D).add(mx,my,mz));
  particleRed=particleGreen=particleBlue=(float)(Math.random()*.30000001192092896D);particleScale*=.75F;smokeParticleScale=particleScale;noPhysics=false;
 }
 @Override public void tick(){
  xo=getX();yo=getY();zo=getZ();if(maxAge<900)maxAge=random.nextInt(301)+900;
  if(!level().isClientSide&&random.nextInt(50)==0)corrode((int)getX(),(int)getY(),(int)getZ());
  particleAge++;if(particleAge>=maxAge)discard();setDeltaMovement(getDeltaMovement().scale(.7599999785423279D));
  if(onGround())setDeltaMovement(getDeltaMovement().multiply(.699999988079071D,1,.699999988079071D));
  if(level().isRaining()&&level().canSeeSky(BlockPos.containing(getX(),getY(),getZ())))setDeltaMovement(getDeltaMovement().add(0,-.01,0));
  for(int i=0;i<4;i++){
   var step=getDeltaMovement().scale(.25);setPosRaw(getX()+step.x,getY()+step.y,getZ()+step.z);var pos=new BlockPos((int)getX(),(int)getY(),(int)getZ());var state=level().getBlockState(pos);
   if(state.is(RadioReceiverContent.BLOCK.get())){discard();int meta=state.getValue(RadioReceiverBlock.META);level().setBlock(pos,PinkCloudBroadcasterContent.BLOCK.get().defaultBlockState().setValue(PinkCloudBroadcasterBlock.META,meta),2);}
   state=level().getBlockState(pos);
   if(LegacyBlockTraits.isNormalCube(level(),pos,state)){if(random.nextInt(5)!=0)discard();setPosRaw(getX()-step.x,getY()-step.y,getZ()-step.z);setDeltaMovement(0,0,0);}
  }
 }
 private void corrode(double x,double y,double z){for(var entity:level().getEntitiesOfClass(LivingEntity.class,new AABB(x-2,y-2,z-2,x+2,y+2,z+2))){if(Math.sqrt(entity.distanceToSqr(x,y,z))>2)continue;for(int slot=0;slot<4;slot++)MistExposure.damageSuit(entity,slot,25);entity.hurt(HbmDamageSources.source(level(),HbmDamageSources.pc),5);}}
}
