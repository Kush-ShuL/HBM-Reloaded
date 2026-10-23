package com.hbm.entity.mob.hunterchopper;
import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.HbmSounds;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;
/** Source custom EntityFlying controller; this mob does not acquire modern flight goals or a replacement gun. */
public final class HunterChopper extends FlyingMob implements Enemy {
 private static final EntityDataAccessor<Boolean> ATTACKING=SynchedEntityData.defineId(HunterChopper.class,EntityDataSerializers.BOOLEAN),DYING=SynchedEntityData.defineId(HunterChopper.class,EntityDataSerializers.BOOLEAN);
 public static Consumer<HunterChopper> CLIENT_TICK=e->{},CLIENT_REMOVE=e->{};
 public int courseChangeCooldown,prevAttackCounter,attackCounter,mineDropCounter;public double waypointX,waypointY,waypointZ;private Entity targetedEntity;private boolean dying;
 private final ServerBossEvent boss=new ServerBossEvent(getDisplayName(),BossEvent.BossBarColor.PURPLE,BossEvent.BossBarOverlay.PROGRESS);
 public HunterChopper(EntityType<? extends HunterChopper> type,Level world){super(type,world);xpReward=500;noCulling=true;boss.setDarkenScreen(true);}
 public static AttributeSupplier.Builder attributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,750);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(ATTACKING,false);builder.define(DYING,false);}
 @Override protected void registerGoals(){}
 public boolean isDying(){return entityData.get(DYING);}public boolean attacking(){return entityData.get(ATTACKING);}
 @Override public void checkDespawn(){if(level().getDifficulty()==Difficulty.PEACEFUL)discard();}
 @Override public boolean checkSpawnRules(LevelAccessor world,MobSpawnType reason){return random.nextInt(20)==0&&world.getDifficulty()!=Difficulty.PEACEFUL;}@Override public boolean checkSpawnObstruction(LevelReader world){return world.isUnobstructed(this)&&world.noCollision(this,getBoundingBox())&&!world.containsAnyLiquid(getBoundingBox());}@Override public int getMaxSpawnClusterSize(){return 1;}
 @Override public void startSeenByPlayer(ServerPlayer player){super.startSeenByPlayer(player);boss.addPlayer(player);}@Override public void stopSeenByPlayer(ServerPlayer player){super.stopSeenByPlayer(player);boss.removePlayer(player);}
 @Override public void tick(){super.tick();if(level().isClientSide)CLIENT_TICK.accept(this);else{boss.setProgress(getHealth()/getMaxHealth());boss.setName(getDisplayName());}}
 @Override public void remove(RemovalReason reason){if(level().isClientSide)CLIENT_REMOVE.accept(this);else boss.removeAllPlayers();super.remove(reason);}
 @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
 @Override protected SoundEvent getHurtSound(DamageSource source){return null;}@Override protected SoundEvent getDeathSound(){return null;}@Override protected float getSoundVolume(){return 10;}
 private void sound(String id,float volume,float pitch){level().playSound(null,getX(),getY(),getZ(),HbmSounds.get(id),SoundSource.HOSTILE,volume,pitch);}
 @Override public boolean hurt(DamageSource source,float amount){if(!(source.is(HbmDamageSources.shrapnel)||source.is(HbmDamageSources.nuclearBlast)||source.is(HbmDamageSources.blackhole)||source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)||HbmDamageSources.getIsTau(source)||HbmDamageSources.getIsSubatomic(source)))amount*=.1F;
  // The old EntityDamageSource/Indirect inheritance rejects any entity-attributed source, even after the scale exceptions.
  if(isInvulnerableTo(source)||oldEntityDamageSource(source)||getHealth()<=.1F)return false;
  if(amount>=getHealth()){initDeath();entityData.set(DYING,true);setHealth(.1F);return false;}
  if(random.nextInt(15)==0&&!level().isClientSide&&!dying){blast(5);dropDamageItem();}
  for(int j=0;j<3;j++){double x=random.nextDouble()/20*random.nextInt(2)==0?-1:1,y=random.nextDouble()/20*random.nextInt(2)==0?-1:1,z=random.nextDouble()/20*random.nextInt(2)==0?-1:1;for(int i=0;i<8;i++)if(level().isClientSide)level().addParticle(ParticleTypes.FIREWORK,getX(),getY(),getZ(),x*i*.25,y*i*.25,z*i*.25);}
  return super.hurt(source,amount);
 }
 /** Vanilla1.7 setExplosionSource attributed only living exploders or a primed TNT living owner.
  * Modern vanilla explosion sources additionally store nonliving direct entities: that is not old EntityDamageSource. */
 private static boolean oldEntityDamageSource(DamageSource source){if(source.is(DamageTypes.EXPLOSION)||source.is(DamageTypes.PLAYER_EXPLOSION)){var direct=source.getDirectEntity();return direct instanceof LivingEntity||direct instanceof PrimedTnt tnt&&tnt.getOwner()!=null;}return source.getDirectEntity()!=null||source.getEntity()!=null;}
 private void blast(float strength){level().explode(this,getX(),getY(),getZ(),strength,false,Level.ExplosionInteraction.BLOCK);}
 private void initDeath(){blast(10);if(!dying)sound("entity.chopperDamage",10,1);dying=true;}
 private void drop(String name,int amount){var id=ResourceLocation.fromNamespaceAndPath("hbm",name);if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Missing real HunterChopper drop "+id);spawnAtLocation(new ItemStack(BuiltInRegistries.ITEM.get(id),amount));}
 private void dropDamageItem(){if(random.nextInt(10)<6)drop("combine_scrap",1);else drop("plate_combine_steel",1);}
 private int surface(int x,int z){int top=level().getHeight(Heightmap.Types.WORLD_SURFACE,x,z);for(int y=top-1;y>=level().getMinBuildHeight();y--){var p=new BlockPos(x,y,z);if(level().getBlockState(p).getLightBlock(level(),p)>0)return y+1;}return level().getMinBuildHeight();}
 private void newWaypoint(){var origin=targetedEntity==null?this:targetedEntity;waypointX=origin.getX()+(random.nextFloat()*2F-1F)*16F;waypointZ=origin.getZ()+(random.nextFloat()*2F-1F)*16F;waypointY=surface((int)waypointX,(int)waypointZ)+10+random.nextInt(15);}
 private Entity closest(){if(!(level() instanceof ServerLevel server))return null;Entity result=null;double best=-1;for(var entity:server.getAllEntities())if(entity instanceof LivingEntity living&&!(entity instanceof HunterChopper)&&living.isAlive()&&!(living instanceof Player player&&(player.getAbilities().invulnerable||player.isSpectator()))){double radius=living.isShiftKeyDown()?250*.800000011920929D:250,distance=living.distanceToSqr(position());if(distance<radius*radius&&(best==-1||distance<best)){best=distance;result=living;}}return result;}
 @Override protected void customServerAiStep(){if(level().getDifficulty()==Difficulty.PEACEFUL){discard();return;}if(!dying){prevAttackCounter=attackCounter;double dx=waypointX-getX(),dy=waypointY-getY(),dz=waypointZ-getZ(),squared=dx*dx+dy*dy+dz*dz;if(squared<1||squared>3600)newWaypoint();if(courseChangeCooldown--<=0){courseChangeCooldown+=random.nextInt(5)+2;double distance=(float)Math.sqrt(squared);if(courseTraversable(distance))setDeltaMovement(getDeltaMovement().add(dx/distance*.1,dy/distance*.1,dz/distance*.1));else{waypointX=getX()+(random.nextFloat()*2F-1F)*16F;waypointZ=getZ()+(random.nextFloat()*2F-1F)*16F;waypointY=surface((int)waypointX,(int)waypointZ)+10+random.nextInt(15);}}
   if(targetedEntity!=null&&targetedEntity.isRemoved())targetedEntity=null;if(targetedEntity==null||attackCounter<=0)targetedEntity=closest();
   if(targetedEntity!=null&&targetedEntity.distanceToSqr(this)<4096){var look=getViewVector(1);double x=getX()+look.x*2,y=getY()-.5,z=getZ()+look.z*2,tx=targetedEntity.getX()-x,ty=targetedEntity.getBoundingBox().minY+targetedEntity.getBbHeight()/2F-y,tz=targetedEntity.getZ()-z;attackCounter++;if(attackCounter>=200)attackCounter-=200;
    if(attackCounter%2==0&&attackCounter>=120){sound("weapon.osiprShoot",10,1);var bullet=new com.hbm.entity.projectile.EntityBullet(level(),this,3F,35,45,false,"chopper");var aim=new Vec3(tx-1+random.nextInt(3),ty-1+random.nextInt(3),tz-1+random.nextInt(3)).normalize();bullet.setDeltaMovement(aim.scale(3));bullet.damage=3+random.nextInt(5);bullet.setPos(x,y,z);level().addFreshEntity(bullet);}
    if(attackCounter==80)sound("entity.chopperCharge",5,1);mineDropCounter++;if(mineDropCounter>100&&random.nextInt(15)==0){sound("entity.chopperDrop",15,1);level().addFreshEntity(new ChopperMine(level(),getX(),getY()-.5,getZ(),0,-.3,0,this));mineDropCounter=0;if(random.nextInt(3)==0){for(var motion:new double[][]{{1,-.3,0},{0,-.3,1},{-1,-.3,0},{0,-.3,-1}})level().addFreshEntity(new ChopperMine(level(),getX(),getY()-.5,getZ(),motion[0],motion[1],motion[2],this));}}}
   else if(attackCounter>0)attackCounter=0;entityData.set(ATTACKING,attackCounter>10);
  }else{var motion=getDeltaMovement().add(0,-.08,0);if(Math.sqrt(motion.x*motion.x+motion.z*motion.z)*1.2<1.8)motion=new Vec3(motion.x*1.2,motion.y,motion.z*1.2);setDeltaMovement(motion);if(random.nextInt(20)==0)blast(5);if(level() instanceof ServerLevel server)HunterChopperEffects.exhaust(server,getX(),getY(),getZ());setYRot(getYRot()+20);if(onGround()){blast(15);drop("combine_scrap",random.nextInt(8)+1);drop("plate_combine_steel",random.nextInt(5)+1);discard();}}
  rotateSource();
 }
 private void rotateSource(){var motion=getDeltaMovement();float horizontal=(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z);float desired=(float)(targetedEntity==null?Math.atan2(motion.x,motion.z)*180/Math.PI:Math.atan2(getX()-targetedEntity.getX(),getZ()-targetedEntity.getZ())*180/Math.PI);if(getYRot()-desired>=10){setYRot(getYRot()-10);yRotO=getYRot();}if(getYRot()-desired<=-10){setYRot(getYRot()+10);yRotO=getYRot();}// Source's third condition is <10 && >10 and therefore never snaps.
  xRotO=(float)(Math.atan2(motion.y,horizontal)*180/Math.PI);setXRot(xRotO);if(getXRot()<=330&&getXRot()>=30)setXRot(getXRot()<180?30:330);
 }
 private boolean courseTraversable(double distance){double dx=(waypointX-getX())/distance,dy=(waypointY-getY())/distance,dz=(waypointZ-getZ())/distance;var box=getBoundingBox();for(int i=1;i<distance;i++){box=box.move(dx,dy,dz);if(!level().noCollision(this,box))return false;}return true;}
 // Source adds no subclass NBT: waypoint/counters/target/dying still reset while superclass health/motion persist.
}
