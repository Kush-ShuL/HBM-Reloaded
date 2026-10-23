package com.hbm.entity.mob.botprime;
import com.hbm.advancement.HbmAdvancements;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;
/** Source real Boss:15000HP,74 independent body entities,original diving/wander steering and5-laser barrage. */
public final class BotPrimeHead extends BotPrimeBase {
 private final ServerBossEvent boss=new ServerBossEvent(getDisplayName(),BossEvent.BossBarColor.PURPLE,BossEvent.BossBarOverlay.PROGRESS);
 public BotPrimeHead(EntityType<? extends BotPrimeHead> type,Level level){super(type,level);xpReward=1000;noCulling=true;boss.setDarkenScreen(true);}
 @Override public boolean isHead(){return true;}@Override public float contactDamage(Entity target){return 1000;}
 @Override public boolean hurt(DamageSource source,float amount){boolean success=super.hurt(source,amount);if(success)damageCooldown=10;return success;}
 @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){head(this);int x=net.minecraft.util.Mth.floor(getX()),y=net.minecraft.util.Mth.floor(getY()),z=net.minecraft.util.Mth.floor(getZ());for(int i=0;i<74;i++){var part=new BotPrimeBody(BotPrimeContent.BODY.get(),level());part.partNumber(i);part.setPos(x,y,z);part.head(this);level().addFreshEntity(part);}setPos(x,y,z);spawnPoint=new BlockPos(x,y,z);return super.finalizeSpawn(world,difficulty,reason,group);}
 @Override protected void customServerAiStep(){wormActionState();targeting.tick(true);updateHeadMovement();if(getHealth()<getMaxHealth()&&tickCount%6==0){if(targeted!=null)heal(1);else if(lastHurtByPlayerTime==0)heal(4);}if(targeted!=null&&targeted.distanceToSqr(this)<150*150&&seeThroughNonSolids(targeted)){if(++attackCounter==30){laserAttack(targeted,true);attackCounter=0;}}else attackCounter=0;boss.setProgress(getHealth()/getMaxHealth());boss.setName(getDisplayName());}
 @Override public void tick(){super.tick();faceMotion(getDeltaMovement());}
 protected void updateHeadMovement(){double x=waypointX-getX(),y=waypointY-getY(),z=waypointZ-getZ(),length=x*x+y*y+z*z;if(courseChangeCooldown--<=0){courseChangeCooldown+=random.nextInt(5)+2;length=(float)Math.sqrt(length);if(getDeltaMovement().lengthSqr()<1){if(!courseTraversable())length*=8;double speed=getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();setDeltaMovement(getDeltaMovement().add(x/length*speed,y/length*speed,z/length*speed));}}if(!courseTraversable())setDeltaMovement(getDeltaMovement().add(0,-.006,0));if(damageCooldown>0)damageCooldown--;aggroCooldown--;if(getTarget()!=null){if(aggroCooldown<=0){targeted=getTarget();aggroCooldown=20;}}else if(targeted==null){waypointX=spawnPoint.getX()-50+random.nextInt(100);waypointY=spawnPoint.getY()-30+random.nextInt(60);waypointZ=spawnPoint.getZ()-50+random.nextInt(100);}var v=getDeltaMovement();setYRot((float)(Math.atan2(v.x,v.z)*180F/Math.PI));setXRot((float)-(Math.atan2(v.y,(float)Math.sqrt(v.x*v.x+v.z*v.z))*180/Math.PI));if(targeted!=null&&targeted.distanceToSqr(this)<150*150){if(wasNearGround){waypointX=targeted.getX();waypointY=targeted.getY();waypointZ=targeted.getZ();if(random.nextInt(80)==0&&getY()>60&&!courseTraversable())wasNearGround=false;}else{waypointX=targeted.getX();waypointY=level().getMinBuildHeight()+10;waypointZ=targeted.getZ();if(getY()<level().getMinBuildHeight()+15)wasNearGround=true;}}}
 @Override public void die(DamageSource source){super.die(source);if(level() instanceof ServerLevel server)for(var player:server.getEntitiesOfClass(ServerPlayer.class,getBoundingBox().inflate(200))){HbmAdvancements.trigger(player,"bossWorm");player.getInventory().add(new ItemStack(HbmContent.item("coin_worm")));}}
 @Override public void startSeenByPlayer(ServerPlayer player){super.startSeenByPlayer(player);boss.addPlayer(player);}@Override public void stopSeenByPlayer(ServerPlayer player){super.stopSeenByPlayer(player);boss.removePlayer(player);}
 @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("spawnX",spawnPoint.getX());tag.putInt("spawnY",spawnPoint.getY());tag.putInt("spawnZ",spawnPoint.getZ());}@Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);spawnPoint=new BlockPos(tag.getInt("spawnX"),tag.getInt("spawnY"),tag.getInt("spawnZ"));}
}
