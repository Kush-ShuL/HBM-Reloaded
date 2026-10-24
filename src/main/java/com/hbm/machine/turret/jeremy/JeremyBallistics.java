package com.hbm.machine.turret.jeremy;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunAmmo;
import com.hbm.weapon.sedna.gun.catapult.NuclearCatapultImpact;
import com.hbm.weapon.sedna.gun.factory.Lego;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import java.util.List;
/** Exact XFactoryTurret five240mm configs. Original renderer=null is retained; these are not fake visible tracer rounds. */
public final class JeremyBallistics {
 private static final SpentCasing CASING240=new SpentCasing(SpentCasing.CasingType.BOTTLENECK).setScale(7.5F).setBounceMotion(.02F,.05F).setColor(SpentCasing.COLOR_CASE_BRASS).setupSmoke(1,.5,60,20);
 private static BulletConfig round(String name,int meta,float damage,String casing){return new BulletConfig("hbm:turret/240mm/"+name).setItem(new GunAmmo(()->JeremyTurretContent.ammo(meta),1,0)).setDamage(damage).setCasing(CASING240.clone().register(casing));}
 public static final BulletConfig SHELL_NORMAL=round("stock",0,1,"240standard").setOnImpact((bullet,hit)->{Lego.standardExplode(bullet,hit,10);bullet.setDead();});
 public static final BulletConfig SHELL_EXPLOSIVE=round("explosive",1,1.5F,"240ext").setOnImpact((bullet,hit)->{var p=hit.getLocation();new ExplosionVNT(bullet.level(),p.x,p.y,p.z,10).setBlockAllocator(new BlockAllocatorStandard()).setBlockProcessor(new BlockProcessorStandard()).setEntityProcessor(new EntityProcessorCrossSmooth(1,bullet.damage)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1)).explode();bullet.setDead();});
 public static final BulletConfig SHELL_AP=round("apfsds_t",2,2,"240w").setDoesPenetrate(true);
 public static final BulletConfig SHELL_DU=round("apfsds_du",3,2.5F,"240u").setDoesPenetrate(true).setDamageFalloffByPen(false);
 public static final BulletConfig SHELL_W9=round("w9",4,2.5F,"240n").setOnImpact(NuclearCatapultImpact::standard);
 public static List<BulletConfig> configs(){return List.of(SHELL_NORMAL,SHELL_EXPLOSIVE,SHELL_AP,SHELL_DU,SHELL_W9);}public static void init(){}
 private JeremyBallistics(){}
}
