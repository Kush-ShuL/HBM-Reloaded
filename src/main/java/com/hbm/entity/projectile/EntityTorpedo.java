package com.hbm.entity.projectile;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.weapon.specialvehicle.VehicleShockEffects;
import com.hbm.weapon.sedna.gun.pistols.PistolSpecialEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** Exact original replacement onUpdate: no inherited throwable drag, swept hit, or age limit. */
public final class EntityTorpedo extends EntityThrowableNT implements LegacyTrackingRange {
    public EntityTorpedo(Level level){this(PistolSpecialEntities.TORPEDO.get(),level);}
    public EntityTorpedo(EntityType<?> type,Level level){super(type,level);noCulling=true;}
    @Override public void tick(){
        if(!level().isClientSide&&tickCount==1)for(int i=0;i<15;i++)VehicleShockEffects.balefire(level(),getX()+(random.nextDouble()-.5)*2,getY()+(random.nextDouble()-.5),getZ()+(random.nextDouble()-.5)*2,getX(),getY(),getZ());
        xOld=xo=getX();yOld=yo=getY();zOld=zo=getZ();
        var motion=getDeltaMovement();setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);
        setDeltaMovement(motion.x,Math.max(motion.y-.04,-2.5),motion.z);
        // Original Java truncation toward zero, including negative coordinates.
        if(!level().getBlockState(new BlockPos((int)getX(),(int)getY(),(int)getZ())).isAir()&&!level().isClientSide){
            discard();LargeConventionalEffects.standard(level(),getX(),getY()+1,getZ());
            var blast=new ExplosionVNT(level(),getX(),getY(),getZ(),20);blast.makeStandard();blast.explode();
        }
    }
    @Override protected void onImpact(HitResult hit){}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
    @Override public int legacyTrackingRange(){return 1000;}
}
