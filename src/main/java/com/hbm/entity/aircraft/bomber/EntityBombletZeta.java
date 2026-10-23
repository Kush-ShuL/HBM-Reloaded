package com.hbm.entity.aircraft.bomber;
import com.hbm.bomb.nuclear.*;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.effect.EntityMist;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.fluid.HbmFluids;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
/** Source position-only impact, exactly one move then XZ drag/Y gravity, deliberately unsaved and unsynchronized type. */
public final class EntityBombletZeta extends EntityThrowableNT implements LegacyTrackingRange {
 public int type;
 public EntityBombletZeta(EntityType<? extends EntityBombletZeta> type,Level level){super(type,level);noCulling=true;}
 @Override public void tick(){
  xo=xOld=getX();yo=yOld=getY();zo=zOld=getZ();var motion=getDeltaMovement();setPos(position().add(motion));
  setDeltaMovement(motion.x*.99,motion.y-.05D,motion.z*.99);rotation();
  if(!level().getBlockState(new BlockPos((int)getX(),(int)getY(),(int)getZ())).is(Blocks.AIR)&&!level().isClientSide){
   if(type==0||type==1){
    var processor=new BlockProcessorStandard();if(type==1)processor.withBlockEffect(new BlockMutatorFire());
    new ExplosionVNT(level(),getX()+.5F,getY()+1.5F,getZ()+.5F,4F).setBlockAllocator(new BlockAllocatorStandard())
     .setBlockProcessor(processor).setEntityProcessor(new EntityProcessorCrossSmooth(1,100)).setPlayerProcessor(new PlayerProcessorStandard())
     .setSFX(type==0?new ExplosionEffectWeapon(15,3.5F,1.25F):new ExplosionEffectWeapon(15,5F,1.75F)).explode();
   }
   if(type==2){
    level().playSound(null,getX()+.5F,getY()+.5F,getZ()+.5F,SoundEvents.FIRE_EXTINGUISH,SoundSource.NEUTRAL,5F,2.6F+(random.nextFloat()-random.nextFloat())*.8F);
    var mist=new EntityMist(level()).setType(HbmFluids.CHLORINE);var current=getDeltaMovement();mist.setPos(getX()-current.x,getY()-current.y,getZ()-current.z);mist.setArea(15,7.5F);level().addFreshEntity(mist);
   }
   if(type==4){
    level().addFreshEntity(NuclearExplosionMK5.statFac(level(),(int)(NuclearConfig.radius("fatman")*1.5),getX(),getY(),getZ()));
    NuclearMukeEffects.sendAtOrigin(level(),getX(),getY()+.5,getZ(),"muke",random.nextInt(100)==0,getX(),getY(),getZ());
    level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("weapon.mukeExplosion"),SoundSource.NEUTRAL,15F,1F);
   }
   discard();
  }
 }
 public void rotation(){var motion=getDeltaMovement();float horizontal=(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z);setYRot((float)(Math.atan2(motion.x,motion.z)*180D/Math.PI));setXRot((float)(Math.atan2(motion.y,horizontal)*180D/Math.PI)-90);while(getXRot()-xRotO< -180)xRotO-=360;while(getXRot()-xRotO>=180)xRotO+=360;while(getYRot()-yRotO< -180)yRotO-=360;while(getYRot()-yRotO>=180)yRotO+=360;}
 @Override protected void onImpact(HitResult impact){}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
 @Override public int legacyTrackingRange(){return 1000;}
}
