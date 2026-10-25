package com.hbm.world.taint;

import com.hbm.item.medicine.MedicineConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.*;

/** Original15HP/.35speed/regeneration and non-terrain explosion with distinct charged spread rules. */
public final class TaintedCreeper extends Creeper {
 private boolean sourceDied;
 public TaintedCreeper(EntityType<? extends TaintedCreeper> type,Level level){super(type,level);}
 public static AttributeSupplier.Builder attributes(){return Creeper.createAttributes().add(Attributes.MAX_HEALTH,15).add(Attributes.MOVEMENT_SPEED,.35);}
 @Override public void tick(){super.tick();if(isAlive()&&getHealth()<getMaxHealth()&&tickCount%10==0)heal(1);}
 /** Native charged-creeper heads did not exist in the source death branch. */
 @Override protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level,net.minecraft.world.damagesource.DamageSource source,boolean recentlyHit){}
 @Override public void die(net.minecraft.world.damagesource.DamageSource source){if(sourceDied)return;sourceDied=true;super.die(source);com.hbm.entity.mob.sourcecreepers.SourceCreeper.dropSourceSkeletonRecord(this,source);}
 public void sourceExplosion(){
  if(level().isClientSide)return;var level=level();boolean griefing=level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
  level.explode(this,getX(),getY(),getZ(),5,false,Level.ExplosionInteraction.NONE);
  if(griefing){int samples=isPowered()?255:85,range=isPowered()?15:7,half=isPowered()?7:3;for(int i=0;i<samples;i++){
   var at=new BlockPos(random.nextInt(range)+(int)getX()-half,random.nextInt(range)+(int)getY()-half,random.nextInt(range)+(int)getZ()-half);var before=level.getBlockState(at);
   if(!before.isAir()&&com.hbm.block.LegacyBlockTraits.isNormalCube(level,at,before)){
    int meta=isPowered()?(MedicineConfig.TAINT_TRAILS.get()?random.nextInt(3):random.nextInt(3)+5):(MedicineConfig.TAINT_TRAILS.get()?random.nextInt(3)+4:random.nextInt(6)+10);
    level.setBlock(at,TaintContent.TAINT.get().defaultBlockState().setValue(TaintBlock.META,meta),2);
   }
  }}discard();
 }
}
