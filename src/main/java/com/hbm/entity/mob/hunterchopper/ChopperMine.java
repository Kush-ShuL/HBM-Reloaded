package com.hbm.entity.mob.hunterchopper;
import com.hbm.entity.mob.crab.CyberTauBullet;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
/** The original100-tick mine, not a generic grenade. Source default/full constructor dimensions and unsaved state differ. */
public final class ChopperMine extends Entity implements com.hbm.entity.LegacyTrackingRange {
 public static Consumer<ChopperMine> CLIENT_TICK=e->{},CLIENT_REMOVE=e->{};public int timer;private Entity shooter;private boolean largeSourceBody;private AABB sourceQueryBox;
 public ChopperMine(EntityType<? extends ChopperMine> type,Level world){super(type,world);}
 public ChopperMine(Level world,double x,double y,double z,double mx,double my,double mz,Entity owner){this(HunterChopperContent.MINE.get(),world);largeSourceBody=true;refreshDimensions();setPos(x,y,z);setDeltaMovement(mx,my,mz);shooter=owner;
  // Old Entity.setSize only enlarged the initial bbox's max; direct pos fields never moved it.
  sourceQueryBox=new AABB(-.30000001192092896,0,-.30000001192092896,11.699999988079071,12,11.699999988079071);
 }
 @Override public EntityDimensions getDimensions(Pose pose){return largeSourceBody?EntityDimensions.scalable(12,12):super.getDimensions(pose);}@Override public boolean fireImmune(){return largeSourceBody;}
 @Override public int legacyTrackingRange(){return 1000;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override protected void addAdditionalSaveData(CompoundTag tag){}@Override protected void readAdditionalSaveData(CompoundTag tag){/* Source timer/owner/full-constructor flags were not serialized. */sourceQueryBox=null;}
 /** Original default entity packet positioning refreshes only the client query box; raw integration does not. */
 @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int steps){super.lerpTo(x,y,z,yaw,pitch,steps);sourceQueryBox=getBoundingBox();}
 private void explode(){level().explode(shooter,getX(),getY(),getZ(),5,false,Level.ExplosionInteraction.NONE);discard();}
 @Override public void tick(){super.tick();if(sourceQueryBox==null)sourceQueryBox=getBoundingBox();var motion=getDeltaMovement();Vec3 from=position(),to=from.add(motion);var block=level().clip(new ClipContext(from,to,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));HitResult impact=block.getType()==HitResult.Type.MISS?null:block;Vec3 end=impact==null?to:impact.getLocation();Entity target=null;double nearest=0;
  for(var entity:level().getEntities(this,sourceQueryBox.expandTowards(motion).inflate(1))){if(!entity.isPickable()||entity==shooter)continue;var hit=CyberTauBullet.sourceIntercept(entity.getBoundingBox().inflate((double).3F),from,end);if(hit!=null){double distance=from.distanceTo(hit);if(distance<nearest||nearest==0){target=entity;nearest=distance;}}}if(target!=null)impact=new EntityHitResult(target);if(impact instanceof EntityHitResult hit&&hit.getEntity() instanceof Player)explode();
  if(level().isClientSide)CLIENT_TICK.accept(this);if(timer>=100||!level().getBlockState(new BlockPos((int)getX(),(int)getY(),(int)getZ())).isAir())explode();double y=motion.y;if(y>-.85)y-=.05;setDeltaMovement(motion.x*.9,y,motion.z*.9);setPos(position().add(getDeltaMovement()));timer++;
 }
 @Override public void remove(RemovalReason reason){if(level().isClientSide)CLIENT_REMOVE.accept(this);super.remove(reason);}
}
