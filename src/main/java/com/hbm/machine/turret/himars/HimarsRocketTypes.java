package com.hbm.machine.turret.himars;
import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.bomb.conventional.grenade.GrenadeParticleEffects;
import com.hbm.bomb.nuclear.*;
import com.hbm.explosion.*;
import com.hbm.explosion.vanillant.*;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.item.medicine.MedicineEffects;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.artillery.ArtyDebrisMutator;
import com.hbm.world.structure.dependencies.materials.StructureMaterialsContent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.network.PacketDistributor;
/** Actual ItemAmmoHIMARS eight effect definitions. Reuses the sole VNT/debris/nuclear/phosphorus/cloud backends. */
public final class HimarsRocketTypes {
 public static final int SMALL=0,LARGE=1,SMALL_HE=2,SMALL_WP=3,SMALL_TB=4,LARGE_TB=5,SMALL_MINI_NUKE=6,SMALL_LAVA=7;
 public record Type(String name,String textureName,int modelType){public int amount(){return modelType==0?6:1;}public ResourceLocation texture(){return HimarsTurretContent.id("textures/models/projectiles/"+textureName+".png");}}
 public static final Type[] TYPES={new Type("standard","himars_standard",0),new Type("single","himars_single",1),new Type("standard_he","himars_standard_he",0),new Type("standard_wp","himars_standard_wp",0),new Type("standard_tb","himars_standard_tb",0),new Type("single_tb","himars_single_tb",1),new Type("standard_mini_nuke","himars_standard_mini_nuke",0),new Type("standard_lava","himars_standard_lava",0)};
 public static Type type(int index){return TYPES[index>=0&&index<TYPES.length?index:0];}
 private static Vec3 behind(HimarsRocketEntity rocket,HitResult hit){var motion=rocket.getDeltaMovement();var unit=LegacyVector.createVectorHelper(motion.x,motion.y,motion.z).normalize();return hit.getLocation().add(-unit.xCoord,-unit.yCoord,-unit.zCoord);}
 private static BlockState slag(){return StructureMaterialsContent.SLAG.get().fromLegacy(1);}
 public static void standardExplosion(HimarsRocketEntity rocket,HitResult hit,float size,float range,boolean breaksBlocks,BlockState debris){var pos=behind(rocket,hit);var explosion=new ExplosionVNT(rocket.level(),pos.x,pos.y,pos.z,size);if(breaksBlocks){explosion.setBlockAllocator(new BlockAllocatorStandard(48));explosion.setBlockProcessor(new BlockProcessorStandard().setNoDrop().withBlockEffect(new ArtyDebrisMutator(debris)));}explosion.setEntityProcessor(new EntityProcessorCross(7.5).withRangeMod(range));explosion.setPlayerProcessor(new PlayerProcessorStandard());explosion.explode();rocket.killAndClear();}
 private static void visual(HimarsRocketEntity rocket,HitResult hit,boolean large,boolean debris){var bp=hit instanceof BlockHitResult block?block.getBlockPos():BlockPos.ZERO;double x=bp.getX()+.5,y=bp.getY()+.5,z=bp.getZ()+.5;var packet=large?new LargeConventionalEffects(x,y,z,30,6.5F,2,65,25,16,50,1.25F,3,-2,350):new LargeConventionalEffects(x,y,z,15,5,1,45,10,debris?16:0,50,1,3,-2,200);if(rocket.level() instanceof ServerLevel server){double range=large?350:300;for(var player:server.players())if(player.distanceToSqr(x,y,z)<=range*range)PacketDistributor.sendToPlayer(player,packet);}}
 private static void explosionSound(HimarsRocketEntity rocket){rocket.level().playSound(null,rocket.getX(),rocket.getY(),rocket.getZ(),HbmSounds.get("weapon.explosionMedium"),SoundSource.NEUTRAL,20,.9F+rocket.level().random.nextFloat()*.2F);}
 private static void mush(HimarsRocketEntity rocket,HitResult hit,float scale){var pos=hit.getLocation();RbmkMushEffects.sendAtOrigin(rocket.level(),pos.x,pos.y,pos.z,scale,rocket.getX(),rocket.getY(),rocket.getZ());}
 public static void impact(int type,HimarsRocketEntity rocket,HitResult hit){var pos=hit.getLocation();var level=rocket.level();switch(type){
  case SMALL->{standardExplosion(rocket,hit,20,3,false,slag());visual(rocket,hit,false,false);}
  case SMALL_HE->{standardExplosion(rocket,hit,20,3,true,slag());visual(rocket,hit,false,true);}
  case SMALL_LAVA->standardExplosion(rocket,hit,20,3,true,HbmWorldFluids.block("volcanic_lava_block").defaultBlockState());
  case LARGE->{standardExplosion(rocket,hit,50,5,true,slag());visual(rocket,hit,true,true);}
  case SMALL_MINI_NUKE->{rocket.killAndClear();var point=behind(rocket,hit);ExplosionNukeSmall.explode(level,point.x,point.y,point.z,ExplosionNukeSmall.PARAMS_MEDIUM);}
  case SMALL_WP->{explosionSound(rocket);standardExplosion(rocket,hit,20,3,false,slag());ExplosionLarge.spawnShrapnels(level,(int)pos.x,(int)pos.y,(int)pos.z,30);ExplosionChaos.igniteAllBlocks(level,(int)pos.x,(int)pos.y,(int)pos.z,20);for(var entity:level.getEntities(rocket,new AABB(rocket.getX()-30,rocket.getY()-30,rocket.getZ()-30,rocket.getX()+30,rocket.getY()+30,rocket.getZ()+30))){entity.igniteForSeconds(5);if(entity instanceof LivingEntity living){var effect=new MobEffectInstance(MedicineEffects.PHOSPHORUS,600,0,true,true);effect.getCures().clear();living.addEffect(effect);}}for(int i=0;i<10;i++)GrenadeParticleEffects.sendHaze(level,pos.x+level.random.nextGaussian()*15,pos.y,pos.z+level.random.nextGaussian()*15,rocket.getX(),rocket.getY(),rocket.getZ());mush(rocket,hit,15);}
  case SMALL_TB,LARGE_TB->{explosionSound(rocket);standardExplosion(rocket,hit,type==SMALL_TB?20:50,type==SMALL_TB?10:12,true,slag());ExplosionLarge.spawnShrapnels(level,(int)pos.x,(int)pos.y,(int)pos.z,30);mush(rocket,hit,type==SMALL_TB?20:35);}
  default->throw new IllegalArgumentException("Unknown source HIMARS rocket "+type);
 }}
 private HimarsRocketTypes(){}
}
