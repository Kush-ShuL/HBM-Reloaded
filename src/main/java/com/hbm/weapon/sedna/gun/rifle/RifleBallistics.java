package com.hbm.weapon.sedna.gun.rifle;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import java.util.*;
import java.util.function.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.weapon.sedna.gun.particle.SpentCasing.CasingType;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunFactory;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunAnimations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.bomb.conventional.grenade.FireStatus;
/** Actual source bullet configs, independent of gun registration. No fake ammunition items. */
public final class RifleBallistics {
 private static boolean initialized;
 public static BulletConfig r556_sp;
 public static BulletConfig r556_fmj;
 public static BulletConfig r556_jhp;
 public static BulletConfig r556_ap;
 public static BulletConfig r762_sp;
 public static BulletConfig r762_fmj;
 public static BulletConfig r762_jhp;
 public static BulletConfig r762_ap;
 public static BulletConfig r762_du;
 public static BulletConfig r762_he;
 public static BulletConfig bmg50_sp;
 public static BulletConfig bmg50_fmj;
 public static BulletConfig bmg50_jhp;
 public static BulletConfig bmg50_ap;
 public static BulletConfig bmg50_du;
 public static BulletConfig bmg50_he;
 public static BulletConfig bmg50_sm;
 public static BulletConfig r556_inc_sp;
 public static BulletConfig r556_inc_fmj;
 public static BulletConfig r556_inc_jhp;
 public static BulletConfig r556_inc_ap;
 public static synchronized void initialize(){if(initialized)return;initialized=true;
SpentCasing casing556 = new SpentCasing(CasingType.BOTTLENECK).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(0.8F);
r556_sp = new BulletConfig("hbm:rifle/r556_sp").setItem(PistolAmmo.R556_SP)
				.setCasing(casing556.clone().register("r556"));
r556_fmj = new BulletConfig("hbm:rifle/r556_fmj").setItem(PistolAmmo.R556_FMJ).setDamage(0.8F).setThresholdNegation(4F).setArmorPiercing(0.1F)
				.setCasing(casing556.clone().register("r556fmj"));
r556_jhp = new BulletConfig("hbm:rifle/r556_jhp").setItem(PistolAmmo.R556_JHP).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
				.setCasing(casing556.clone().register("r556jhp"));
r556_ap = new BulletConfig("hbm:rifle/r556_ap").setItem(PistolAmmo.R556_AP).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(10F).setArmorPiercing(0.15F)
				.setCasing(casing556.clone().setColor(SpentCasing.COLOR_CASE_44).register("r556ap"));
SpentCasing casing762 = new SpentCasing(CasingType.BOTTLENECK).setColor(SpentCasing.COLOR_CASE_BRASS);
r762_sp = new BulletConfig("hbm:rifle/r762_sp").setItem(PistolAmmo.R762_SP)
				.setCasing(casing762.clone().register("r762"));
r762_fmj = new BulletConfig("hbm:rifle/r762_fmj").setItem(PistolAmmo.R762_FMJ).setDamage(0.8F).setThresholdNegation(5F).setArmorPiercing(0.1F)
				.setCasing(casing762.clone().register("r762fmj"));
r762_jhp = new BulletConfig("hbm:rifle/r762_jhp").setItem(PistolAmmo.R762_JHP).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
				.setCasing(casing762.clone().register("r762jhp"));
r762_ap = new BulletConfig("hbm:rifle/r762_ap").setItem(PistolAmmo.R762_AP).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(12.5F).setArmorPiercing(0.15F)
				.setCasing(casing762.clone().setColor(SpentCasing.COLOR_CASE_44).register("r762ap"));
r762_du = new BulletConfig("hbm:rifle/r762_du").setItem(PistolAmmo.R762_DU).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.5F).setThresholdNegation(15F).setArmorPiercing(0.25F)
				.setCasing(casing762.clone().setColor(SpentCasing.COLOR_CASE_44).register("r762du"));
r762_he = new BulletConfig("hbm:rifle/r762_he").setItem(PistolAmmo.R762_HE).setWear(3F).setDamage(1.75F).setOnImpact(RifleBallistics::explode762)
				.setCasing(casing762.clone().setColor(SpentCasing.COLOR_CASE_44).register("r762he"));
SpentCasing casing50 = new SpentCasing(CasingType.BOTTLENECK).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(1.5F);
bmg50_sp = new BulletConfig("hbm:rifle/bmg50_sp").setItem(PistolAmmo.BMG50_SP)
				.setCasing(casing50.clone().register("bmg50"));
bmg50_fmj = new BulletConfig("hbm:rifle/bmg50_fmj").setItem(PistolAmmo.BMG50_FMJ).setDamage(0.8F).setThresholdNegation(7F).setArmorPiercing(0.1F)
				.setCasing(casing50.clone().register("bmg50fmj"));
bmg50_jhp = new BulletConfig("hbm:rifle/bmg50_jhp").setItem(PistolAmmo.BMG50_JHP).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
				.setCasing(casing50.clone().register("bmg50jhp"));
bmg50_ap = new BulletConfig("hbm:rifle/bmg50_ap").setItem(PistolAmmo.BMG50_AP).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(17.5F).setArmorPiercing(0.15F)
				.setCasing(casing50.clone().setColor(SpentCasing.COLOR_CASE_44).register("bmg50ap"));
bmg50_du = new BulletConfig("hbm:rifle/bmg50_du").setItem(PistolAmmo.BMG50_DU).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.5F).setThresholdNegation(21F).setArmorPiercing(0.25F)
				.setCasing(casing50.clone().setColor(SpentCasing.COLOR_CASE_44).register("bmg50du"));
bmg50_he = new BulletConfig("hbm:rifle/bmg50_he").setItem(PistolAmmo.BMG50_HE).setWear(3F).setDamage(1.75F).setOnImpact(RifleBallistics::explode50)
				.setCasing(casing50.clone().setColor(SpentCasing.COLOR_CASE_44).register("bmg50he"));
bmg50_sm = new BulletConfig("hbm:rifle/bmg50_sm").setItem(PistolAmmo.BMG50_SM).setWear(10F).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(2.5F).setThresholdNegation(30F).setArmorPiercing(0.35F)
				.setCasing(casing50.clone().setColor(SpentCasing.COLOR_CASE_44).register("bmg50sm"));
r556_inc_sp = new BulletConfig("hbm:rifle/r556_inc_sp").setItem(PistolAmmo.R556_SP)
				.setCasing(r556_sp.casing).setOnImpact(RifleBallistics::incendiary);
r556_inc_fmj = new BulletConfig("hbm:rifle/r556_inc_fmj").setItem(PistolAmmo.R556_FMJ).setDamage(0.8F).setThresholdNegation(4F).setArmorPiercing(0.1F)
				.setCasing(r556_fmj.casing).setOnImpact(RifleBallistics::incendiary);
r556_inc_jhp = new BulletConfig("hbm:rifle/r556_inc_jhp").setItem(PistolAmmo.R556_JHP).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
				.setCasing(r556_jhp.casing).setOnImpact(RifleBallistics::incendiary);
r556_inc_ap = new BulletConfig("hbm:rifle/r556_inc_ap").setItem(PistolAmmo.R556_AP).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(10F).setArmorPiercing(0.15F)
				.setCasing(r556_ap.casing).setOnImpact(RifleBallistics::incendiary);
 }
 private static boolean earlySelf(EntityBulletBaseMK4 bullet,HitResult hit){return hit instanceof EntityHitResult entity&&bullet.tickCount<3&&entity.getEntity()==bullet.getThrower();}
 private static void explode762(EntityBulletBaseMK4 bullet,HitResult hit){if(earlySelf(bullet,hit))return;Lego.tinyExplode(bullet,hit,1.5F);bullet.setDead();}
 private static void explode50(EntityBulletBaseMK4 bullet,HitResult hit){if(earlySelf(bullet,hit))return;Lego.tinyExplode(bullet,hit,2F);bullet.setDead();}
 private static void incendiary(EntityBulletBaseMK4 bullet,HitResult hit){if(hit instanceof EntityHitResult entity&&entity.getEntity() instanceof LivingEntity living){var data=FireStatus.data(living);if(data.phosphorus<300)data.phosphorus=300;}}
 public static void initializeCasings(){initialize();
  recovery(r556_sp,0,8);
  recovery(r556_fmj,0,8);
  recovery(r556_jhp,0,8);
  recovery(r556_ap,2,8);
  recovery(r762_sp,0,6);
  recovery(r762_fmj,0,6);
  recovery(r762_jhp,0,6);
  recovery(r762_ap,2,6);
  recovery(r762_du,2,6);
  recovery(r762_he,2,6);
  recovery(bmg50_sp,1,12);
  recovery(bmg50_fmj,1,12);
  recovery(bmg50_jhp,1,12);
  recovery(bmg50_ap,3,12);
  recovery(bmg50_du,3,12);
  recovery(bmg50_he,3,12);
  recovery(bmg50_sm,3,6);
  recovery(r556_inc_sp,0,8);
  recovery(r556_inc_fmj,0,8);
  recovery(r556_inc_jhp,0,8);
  recovery(r556_inc_ap,2,8);
 }
 private static void recovery(BulletConfig config,int metadata,int count){var casing=com.hbm.item.casing.FlatCasingContent.stack(metadata,1);config.setCasing(casing,count);}
 private RifleBallistics(){}
}
