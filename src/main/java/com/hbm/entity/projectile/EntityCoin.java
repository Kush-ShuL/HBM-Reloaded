package com.hbm.entity.projectile;

import com.hbm.weapon.sedna.SednaProjectiles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** Original physical coin: half-height origin, no air drag, .02 gravity and block-only expiry. */
public class EntityCoin extends EntityThrowableInterp implements com.hbm.entity.LegacyTrackingRange {
    public static final float LEGACY_Y_OFFSET=.5F;
    public EntityCoin(EntityType<? extends EntityCoin> type,Level level){super(type,level);}
    public EntityCoin(Level level){this(SednaProjectiles.COIN.get(),level);}
    @Override protected AABB makeBoundingBox(){return super.makeBoundingBox().move(0,-LEGACY_Y_OFFSET,0);}
    @Override protected void onImpact(HitResult hit){if(hit.getType()==HitResult.Type.BLOCK)setDead();}
    @Override protected float getAirDrag(){return 1;}
    @Override public double getGravityVelocity(){return .02;}
    @Override public boolean isPickable(){return true;}
    @Override public boolean isAttackable(){return true;}
    @Override public int legacyTrackingRange(){return 1000;}
}
