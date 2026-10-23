package com.hbm.machine.turret.howard;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.machine.turret.TurretBaseEntity;
import com.hbm.radar.RadarTurretReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
/** Actual TileEntityTurretRadarCommandBase, not a plain autonomous turret with a decorative radar marker. */
public abstract class RadarCommandTurretBase extends TurretBaseEntity implements RadarTurretReceiver {
 public boolean radarDesignatedTarget;
 protected RadarCommandTurretBase(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 @Override public boolean sendCommandEntity(Entity entity){if(target!=null||!isOn)return false;if(entity==null||!entity.isAlive())return false;radarDesignatedTarget=true;if(!acceptableTarget(entity)||!entityInLOS(entity))return false;var pos=turretPosition();var point=entityPosition(entity);double dist=LegacyVector.createVectorHelper(point.x-pos.x,point.y-pos.y,point.z-pos.z).lengthVector();if(dist>detectorRange()*4){turnTowards(point);return true;}target=entity;return true;}
 @Override public boolean sendCommandPosition(int x,int y,int z){if(!isOn)return false;turnTowards(new Vec3(x,y,z));return true;}
 @Override public void tickServer(ServerLevel server){connect(server);aligned=false;if(target!=null&&!target.isAlive()){target=null;radarDesignatedTarget=false;stattrak++;}if(target!=null&&!entityInLOS(target)){target=null;radarDesignatedTarget=false;}tPos=target==null?null:entityPosition(target);if(active()&&hasPower()){if(tPos!=null)turnTowards(tPos);}else{target=null;tPos=null;}if(target!=null&&!target.isAlive()){target=null;tPos=null;radarDesignatedTarget=false;stattrak++;}if(active()&&hasPower()){searchTimer--;setPower(getPower()-consumption());if(searchTimer<=0){searchTimer=detectorInterval();if(target==null)seekNewTarget();}}else searchTimer=0;if(aligned)updateFiringTick();chargeBattery();sync(250);afterSync();}
 @Override public boolean entityInLOS(Entity entity){if(!entity.isAlive()||entity.isRemoved())return false;if(!thermalVision()&&entity instanceof LivingEntity living&&living.hasEffect(MobEffects.INVISIBILITY))return false;var p=turretPosition();var e=entityPosition(entity);var delta=LegacyVector.createVectorHelper(e.x-p.x,e.y-p.y,e.z-p.z);double length=delta.lengthVector();if(length<detectorGrace()||(!radarDesignatedTarget&&length>detectorRange()*1.1))return false;delta=delta.normalize();double pitch=Math.toDegrees(Math.asin(delta.yCoord/delta.lengthVector()));if(pitch< -depression()||pitch>elevation())return false;return level.clip(new ClipContext(e,p,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,net.minecraft.world.phys.shapes.CollisionContext.empty())).getType()==HitResult.Type.MISS;}
}
