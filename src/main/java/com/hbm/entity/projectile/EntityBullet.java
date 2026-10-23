package com.hbm.entity.projectile;

import com.hbm.entity.mob.crab.CyberTauBullet;
import com.hbm.entity.projectile.legacy.LegacyBulletContent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/** Actual pre-ItemGunBase EntityBullet identity; all flight/hit behavior stays in the sole shared old kernel. */
@Deprecated
public final class EntityBullet extends CyberTauBullet implements com.hbm.entity.LegacyTrackingRange {
    public EntityBullet(EntityType<? extends EntityBullet> type,Level world){super(type,world);}
    public EntityBullet(Level world){this(LegacyBulletContent.BULLET.get(),world);}
    public EntityBullet(Level world,double x,double y,double z){this(world);setPos(x,y,z);}
    public EntityBullet(Level world,LivingEntity owner,LivingEntity target,float velocity,float spread){this(world);initializeSourceTarget(owner,target,velocity,spread);}
    public EntityBullet(Level world,LivingEntity owner,float velocity,int damageMin,int damageMax,boolean instant,boolean radiation){
        this(world);initializeSourceOwner(java.util.Objects.requireNonNull(owner),velocity,false);sourceHitFlags(instant,radiation);
        // Source damageMin/damageMax assignments are commented; damage remains0 until an actual caller sets it.
    }
    public EntityBullet(Level world,LivingEntity owner,float velocity){this(world);initializeSourceOwner(java.util.Objects.requireNonNull(owner),velocity,true);}
    public EntityBullet(Level world,LivingEntity owner,float velocity,int damageMin,int damageMax,boolean instant,String mode){
        this(world);initializeSourceOwner(owner,velocity,false);
        // Actual source intentionally compares these literal String identities, and never assigns instant here.
        setTau(mode=="tauDay");setChopper(mode=="chopper");setIsCritical(mode!="chopper");
    }
    public EntityBullet(Level world,int x,int y,int z,double mx,double my,double mz,double gravity){this(world);sourceLargeBody();setPos(x+.5F,y+.5F,z+.5F);setDeltaMovement(mx,my,mz);this.gravity=gravity;}
    @Override public int legacyTrackingRange(){return 250;}
}
