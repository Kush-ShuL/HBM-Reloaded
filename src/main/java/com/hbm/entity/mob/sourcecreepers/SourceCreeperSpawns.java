package com.hbm.entity.mob.sourcecreepers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.*;
/** Old EntityMob skylight32/blocklight8 RNG rules and source dimension/height limits, not the modern light0 spawn replacement. */
public final class SourceCreeperSpawns {
 public static boolean canSpawn(EntityType<? extends Creeper> type,ServerLevelAccessor world,MobSpawnType reason,BlockPos pos,RandomSource random){
  if(world.getDifficulty()==Difficulty.PEACEFUL)return false;
  if(type!=SourceCreeperContent.NUCLEAR.get()&&!world.getLevel().dimension().equals(Level.OVERWORLD))return false;
  if((type==SourceCreeperContent.VOLATILE.get()||type==SourceCreeperContent.GOLD.get())&&pos.getY()>40)return false;
  if(world.getBrightness(LightLayer.SKY,pos)>random.nextInt(32))return false;
  int light=world.getMaxLocalRawBrightness(pos,world.getLevel().isThundering()?10:world.getSkyDarken());
  return light<=random.nextInt(8)&&Mob.checkMobSpawnRules(type,world,reason,pos,random);
 }
 private SourceCreeperSpawns(){}
}
