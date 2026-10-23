package com.hbm.entity.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Source client follows network interpolation; it never simulates bullet flight. */
public abstract class EntityThrowableInterp extends EntityThrowableNT {
    protected int turnProgress;
    protected double syncPosX,syncPosY,syncPosZ,syncYaw,syncPitch;
    private Vec3 networkVelocity=Vec3.ZERO;
    protected EntityThrowableInterp(EntityType<?> type,Level level){super(type,level);}
    @Override public void tick(){
        if(!level().isClientSide)super.tick();
        else if(turnProgress>0){
            double x=getX()+(syncPosX-getX())/turnProgress,y=getY()+(syncPosY-getY())/turnProgress,z=getZ()+(syncPosZ-getZ())/turnProgress;
            setYRot((float)(getYRot()+Mth.wrapDegrees(syncYaw-getYRot())/turnProgress));setXRot((float)(getXRot()+(syncPitch-getXRot())/turnProgress));turnProgress--;setPos(x,y,z);
        }else setPos(getX(),getY(),getZ());
    }
    @Override public void lerpMotion(double x,double y,double z){networkVelocity=new Vec3(x,y,z);setDeltaMovement(networkVelocity);}
    @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int steps){syncPosX=x;syncPosY=y;syncPosZ=z;syncYaw=yaw;syncPitch=pitch;turnProgress=steps+approachNum();setDeltaMovement(networkVelocity);}
    /** Modern rotation-only move packets reuse these pending position targets.
     * Entity defaults return current coordinates, which cancels our in-flight
     * interpolation whenever the missile sends its original forced rotation. */
    @Override public double lerpTargetX(){return turnProgress>0?syncPosX:getX();}
    @Override public double lerpTargetY(){return turnProgress>0?syncPosY:getY();}
    @Override public double lerpTargetZ(){return turnProgress>0?syncPosZ:getZ();}
    @Override public float lerpTargetYRot(){return turnProgress>0?(float)syncYaw:getYRot();}
    @Override public float lerpTargetXRot(){return turnProgress>0?(float)syncPitch:getXRot();}
    public int approachNum(){return 0;}
}
