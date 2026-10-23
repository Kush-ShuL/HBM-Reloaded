package com.hbm.machine.turret;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.weapon.sedna.gun.pistols.XFactory9mm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
/** Edwin intentionally has no inventory/power requirement and only the left barrel actually launches a bullet. */
public final class DamagedSentryEntity extends SentryEntity {
    public DamagedSentryEntity(BlockPos pos,BlockState state){super(TurretContent.DAMAGED_ENTITY.get(),pos,state);}
    @Override public boolean damaged(){return true;}@Override public boolean hasPower(){return true;}@Override public boolean active(){return true;}@Override public double yawSpeed(){return 3;}@Override public double pitchSpeed(){return 2;}@Override protected float baseDamage(){return TurretSettings.edwinDamage();}
    @Override public boolean acceptableTarget(Entity entity){if(entity instanceof Player player&&(player.getAbilities().instabuild||player.isSpectator()))return false;return entity instanceof LivingEntity;}
    @Override protected void updateFiringTick(){firingTimer++;if(firingTimer%10!=0)return;var config=XFactory9mm.p9_fmj;if(config==null)return;cachedCasing=config.casing;double x=0,y=0,z=0;if(shotSide){fireSound(1);spawnBullet(config);var muzzle=muzzleOffset();var side=LegacyVector.createVectorHelper(.125,0,0);side.rotateAroundY((float)-rotationYaw);x=muzzle.xCoord+side.xCoord;y=muzzle.yCoord;z=muzzle.zCoord+side.zCoord;}else{fireSound(.75F);spawnCasing();}smoke(x,y,z);markFired();}
}
