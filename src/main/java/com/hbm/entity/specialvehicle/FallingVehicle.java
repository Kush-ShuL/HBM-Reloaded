package com.hbm.entity.specialvehicle;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.weapon.specialvehicle.VehicleShockEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** The source explicitly replaces EntityThrowable.onUpdate: no superclass physics, swept collision, drag or lifetime. */
public abstract class FallingVehicle extends EntityThrowableNT implements LegacyTrackingRange {
    protected FallingVehicle(EntityType<?> type,Level level){super(type,level);noCulling=true;}
    protected abstract double burstWidth();protected abstract double burstHeight();protected abstract double burstLength();protected abstract void impact();
    @Override public final void tick(){
        if(!level().isClientSide&&tickCount==1)for(int i=0;i<50;i++)VehicleShockEffects.balefire(level(),getX()+(random.nextDouble()-.5)*burstWidth(),getY()+(random.nextDouble()-.5)*burstHeight(),getZ()+(random.nextDouble()-.5)*burstLength(),getX(),getY(),getZ());
        xOld=xo=getX();yOld=yo=getY();zOld=zo=getZ();var motion=getDeltaMovement();setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);setDeltaMovement(motion.x,Math.max(motion.y-.03D,-1.5D),motion.z);
        // These casts are truncation toward zero in the original, deliberately not floor/blockPosition.
        if(!level().getBlockState(new BlockPos((int)getX(),(int)getY(),(int)getZ())).isAir())impact();
    }
    @Override protected final void onImpact(HitResult hit){}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
    @Override public int legacyTrackingRange(){return 1000;}
}
