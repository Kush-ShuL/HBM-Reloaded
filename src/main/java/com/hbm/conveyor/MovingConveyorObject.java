package com.hbm.conveyor;
import api.hbm.conveyor.*;import com.hbm.entity.LegacyTrackingRange;import com.hbm.explosion.vanillant.ExplosionVNT;import com.hbm.explosion.vanillant.standard.ExplosionEffectTiny;import net.minecraft.core.*;import net.minecraft.world.entity.*;import net.minecraft.world.level.Level;import net.minecraft.world.phys.Vec3;
/** Original no-clip conveyor motion, four-ply interpolation, cramming and exact enter/leave order. */
public abstract class MovingConveyorObject extends Entity implements LegacyTrackingRange {
 private int turnProgress;private Vec3 sync=Vec3.ZERO,velocity=Vec3.ZERO;
 protected MovingConveyorObject(EntityType<?> type,Level level){super(type,level);noPhysics=true;}
 @Override public int legacyTrackingRange(){return 1000;}@Override public boolean canBeCollidedWith(){return true;}@Override public boolean isAttackable(){return true;}@Override public boolean isPickable(){return true;}
 @Override public void tick(){if(level().isClientSide){if(turnProgress>0){setPos(getX()+(sync.x-getX())/turnProgress,getY()+(sync.y-getY())/turnProgress,getZ()+(sync.z-getZ())/turnProgress);turnProgress--;}else setPos(getX(),getY(),getZ());return;}tickCount++;if(tickCount<=5)return;
  if((tickCount+getId())%400==0){var nearby=level().getEntitiesOfClass(MovingConveyorObject.class,getBoundingBox().inflate(.125));if(nearby.size()>=ConveyorObjects.CRAM_MAX.get()){for(var obj:nearby)obj.discard();new ExplosionVNT(level(),getX(),getY()+.125,getZ(),1,this).setSFX(new ExplosionEffectTiny()).explode();var p=blockPosition();if(level().getBlockState(p).getBlock() instanceof IConveyorBelt&&tickCount>400&&ConveyorObjects.CRAM_EXPLODE.get())level().destroyBlock(p,false);}}
  var current=blockPosition();var block=level().getBlockState(current).getBlock();boolean on=block instanceof IConveyorBelt belt&&belt.canItemStay(level(),current.getX(),current.getY(),current.getZ(),position());if(!on){if(onLeaveConveyor())return;}else{var target=((IConveyorBelt)block).getTravelLocation(level(),current.getX(),current.getY(),current.getZ(),position(),getMoveSpeed());setDeltaMovement(target.subtract(position()));}
  var last=blockPosition();move(MoverType.SELF,getDeltaMovement());var next=blockPosition();if(!last.equals(next)){var state=level().getBlockState(next);if(state.getBlock() instanceof IEnterableBlock enterable){Direction side=null;if(last.getY()==next.getY()&&last.getZ()==next.getZ()&&last.getX()!=next.getX())side=last.getX()>next.getX()?Direction.EAST:Direction.WEST;else if(last.getX()==next.getX()&&last.getZ()==next.getZ()&&last.getY()!=next.getY())side=last.getY()>next.getY()?Direction.UP:Direction.DOWN;else if(last.getX()==next.getX()&&last.getY()==next.getY()&&last.getZ()!=next.getZ())side=last.getZ()>next.getZ()?Direction.SOUTH:Direction.NORTH;enterBlock(enterable,next,side);}else if(!state.isSolid()&&level().getBlockState(next.below()).getBlock() instanceof IEnterableBlock enterable)enterBlock(enterable,next.below(),Direction.UP);}}
 public abstract void enterBlock(IEnterableBlock enterable,BlockPos pos,Direction side);public abstract boolean onLeaveConveyor();public double getMoveSpeed(){return .0625D;}
 @Override public void lerpMotion(double x,double y,double z){velocity=new Vec3(x,y,z);setDeltaMovement(velocity);}@Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int steps){sync=new Vec3(x,y,z);turnProgress=steps+2;setDeltaMovement(velocity);}
 /** Modern rotation-only packets reuse pending coordinates rather than canceling source interpolation. */
 @Override public double lerpTargetX(){return turnProgress>0?sync.x:getX();}
 @Override public double lerpTargetY(){return turnProgress>0?sync.y:getY();}
 @Override public double lerpTargetZ(){return turnProgress>0?sync.z:getZ();}
 // Source conveyor objects never interpolate yaw/pitch; do not invent angular motion/storage.
 @Override public float lerpTargetYRot(){return getYRot();}
 @Override public float lerpTargetXRot(){return getXRot();}
}
