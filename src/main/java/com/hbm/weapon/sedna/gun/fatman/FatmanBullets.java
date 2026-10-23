package com.hbm.weapon.sedna.gun.fatman;
import com.hbm.LegacyStartup;
import com.hbm.bomb.conventional.ConventionalRadiation;
import com.hbm.bomb.nuclear.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.catapult.NuclearCatapultImpact;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.*;
/** Complete six actual XFactoryCatapult mini-nuke configs; standard/cloud/radiation remain unique shared callbacks. */
public final class FatmanBullets {
 public static final BulletConfig nuke_standard=new BulletConfig("hbm:catapult/nuke_standard").setItem(PistolAmmo.NUKE_STANDARD).setLife(300).setVel(3F).setGrav(.025F).setOnImpact(NuclearCatapultImpact::standard);
 public static final BulletConfig nuke_demo=new BulletConfig("hbm:catapult/nuke_demo").setItem(PistolAmmo.NUKE_DEMO).setLife(300).setVel(3F).setGrav(.025F).setOnImpact(FatmanBullets::demo);
 public static final BulletConfig nuke_high=new BulletConfig("hbm:catapult/nuke_high").setItem(PistolAmmo.NUKE_HIGH).setLife(300).setVel(3F).setGrav(.025F).setOnImpact(FatmanBullets::high);
 public static final BulletConfig nuke_tots=new BulletConfig("hbm:catapult/nuke_tots").setItem(PistolAmmo.NUKE_TOTS).setProjectiles(8).setLife(300).setVel(3F).setGrav(.025F).setSpread(.1F).setDamage(.35F).setOnImpact(FatmanBullets::tiny);
 public static final BulletConfig nuke_hive=new BulletConfig("hbm:catapult/nuke_hive").setItem(PistolAmmo.NUKE_HIVE).setProjectiles(12).setLife(300).setVel(1F).setGrav(.025F).setSpread(.15F).setDamage(.25F).setOnImpact(FatmanBullets::hive);
 public static final BulletConfig nuke_balefire=new BulletConfig("hbm:catapult/nuke_balefire").setItem(PistolAmmo.NUKE_BALEFIRE).setDamage(2.5F).setLife(300).setVel(3F).setGrav(.025F).setOnImpact(FatmanBullets::balefire);
 public static final BulletConfig[] ALL={nuke_standard,nuke_demo,nuke_high,nuke_tots,nuke_hive,nuke_balefire};
 private static boolean skip(EntityBulletBaseMK4 bullet,HitResult impact){return impact instanceof EntityHitResult hit&&bullet.tickCount<3&&hit.getEntity()==bullet.getThrower()||bullet.isRemoved();}
 private static void demo(EntityBulletBaseMK4 bullet,HitResult impact){if(skip(bullet,impact))return;bullet.setDead();var p=impact.getLocation();new ExplosionVNT(bullet.level(),p.x,p.y,p.z,15).setBlockAllocator(new BlockAllocatorStandard(64)).setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorFire())).setEntityProcessor(new EntityProcessorCrossSmooth(2,bullet.damage).withRangeMod(1.5F)).setPlayerProcessor(new PlayerProcessorStandard()).explode();ConventionalRadiation.incrementRad(bullet.level(),p.x,p.y,p.z,1.5F);NuclearCatapultImpact.mushroom(bullet,impact);}
 private static void high(EntityBulletBaseMK4 bullet,HitResult impact){if(skip(bullet,impact))return;bullet.setDead();var p=impact.getLocation();bullet.level().addFreshEntity(NuclearExplosionMK5.statFac(bullet.level(),35,p.x,p.y,p.z));NuclearCatapultImpact.mushroom(bullet,impact);}
 private static void balefire(EntityBulletBaseMK4 bullet,HitResult impact){if(skip(bullet,impact))return;bullet.setDead();var p=impact.getLocation();new ExplosionVNT(bullet.level(),p.x,p.y,p.z,10).setBlockAllocator(new BlockAllocatorStandard(64)).setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorBalefire())).setEntityProcessor(new EntityProcessorCrossSmooth(2,bullet.damage).withRangeMod(1.5F)).setPlayerProcessor(new PlayerProcessorStandard()).explode();ConventionalRadiation.incrementRad(bullet.level(),p.x,p.y,p.z,1.5F);mushroom(bullet,impact,"muke",true);}
 private static void tiny(EntityBulletBaseMK4 bullet,HitResult impact){if(skip(bullet,impact))return;bullet.setDead();var p=impact.getLocation();new ExplosionVNT(bullet.level(),p.x,p.y,p.z,5).setEntityProcessor(new EntityProcessorCrossSmooth(2,bullet.damage).withRangeMod(1.5F)).setPlayerProcessor(new PlayerProcessorStandard()).explode();ConventionalRadiation.incrementRad(bullet.level(),p.x,p.y,p.z,.25F);mushroom(bullet,impact,"tinytot",false);}
 private static void hive(EntityBulletBaseMK4 bullet,HitResult impact){if(skip(bullet,impact))return;bullet.setDead();var p=impact.getLocation();new ExplosionVNT(bullet.level(),p.x,p.y,p.z,5).setEntityProcessor(new EntityProcessorCrossSmooth(1,bullet.damage).withRangeMod(1.5F)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1F)).explode();}
 private static void mushroom(EntityBulletBaseMK4 bullet,HitResult impact,String type,boolean balefire){var world=bullet.level();var p=impact.getLocation();SatelliteBursts.reportEvent(world,SatelliteBursts.DURATION_LOW,SatelliteBursts.BurstIntensity.LOW,bullet.getX(),bullet.getZ());world.playSound(null,p.x,p.y+.5,p.z,HbmSounds.get("weapon.mukeExplosion"),SoundSource.BLOCKS,15F,1F);NuclearMukeEffects.sendAtOrigin(world,p.x,p.y+.5,p.z,type,balefire||LegacyStartup.polaroidId==11||world.random.nextInt(100)==0,p.x,p.y,p.z);}
 public static void init(){}private FatmanBullets(){}
}
