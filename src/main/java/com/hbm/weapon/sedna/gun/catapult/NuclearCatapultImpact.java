package com.hbm.weapon.sedna.gun.catapult;
import com.hbm.LegacyStartup;
import com.hbm.bomb.conventional.ConventionalRadiation;
import com.hbm.bomb.nuclear.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.*;
/** Unique actual XFactoryCatapult.LAMBDA_NUKE_STANDARD/spawnMush, shared with the original W9 turret round. */
public final class NuclearCatapultImpact {
 public static void standard(EntityBulletBaseMK4 bullet,HitResult impact){if(impact instanceof EntityHitResult hit&&bullet.tickCount<3&&hit.getEntity()==bullet.getThrower())return;if(bullet.isRemoved())return;bullet.setDead();var p=impact.getLocation();new ExplosionVNT(bullet.level(),p.x,p.y,p.z,10).setEntityProcessor(new EntityProcessorCrossSmooth(2,bullet.damage).withRangeMod(1.5F)).setPlayerProcessor(new PlayerProcessorStandard()).explode();ConventionalRadiation.incrementRad(bullet.level(),p.x,p.y,p.z,1);mushroom(bullet,impact);}
 public static void mushroom(EntityBulletBaseMK4 bullet,HitResult impact){var world=bullet.level();var p=impact.getLocation();SatelliteBursts.reportEvent(world,SatelliteBursts.DURATION_LOW,SatelliteBursts.BurstIntensity.LOW,bullet.getX(),bullet.getZ());world.playSound(null,p.x,p.y+.5,p.z,HbmSounds.get("weapon.mukeExplosion"),SoundSource.BLOCKS,15,1);NuclearMukeEffects.sendAtOrigin(world,p.x,p.y+.5,p.z,"muke",LegacyStartup.polaroidId==11||world.random.nextInt(100)==0,p.x,p.y,p.z);}
 private NuclearCatapultImpact(){}
}
