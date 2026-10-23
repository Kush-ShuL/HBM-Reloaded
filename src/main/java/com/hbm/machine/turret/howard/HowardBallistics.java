package com.hbm.machine.turret.howard;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunAmmo;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
/** Original GunDGKFactory's sole active casing and XFactoryTurret.dgk_normal magazine identity; no unused legacy gun fabricated. */
public final class HowardBallistics {
 public static final SpentCasing CASING_DGK=new SpentCasing(SpentCasing.CasingType.STRAIGHT).setScale(1.5F).setBounceMotion(1,.5F).setColor(SpentCasing.COLOR_CASE_BRASS).register("DGK").setupSmoke(.02F,.5,60,20).setMaxAge(60);
 public static final BulletConfig DGK_NORMAL=new BulletConfig("hbm:turret/dgk_normal").setItem(new GunAmmo(()->HbmContent.item("ammo_dgk"),1,0));
 public static void init(){}private HowardBallistics(){}
}
