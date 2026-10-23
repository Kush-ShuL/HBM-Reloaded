package com.hbm.weapon.sedna.gun.missilelauncher;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.panzerschreck.PanzerschreckBullets;
/** XFactoryRocket.makeML is a plain clone. Reuse the true five shared warhead callbacks, not QD steering. */
public final class MissileLauncherBullets {
 public static final BulletConfig[] ROCKETS=initialize();
 private MissileLauncherBullets(){}
 private static BulletConfig[] initialize(){
  var result=new BulletConfig[5];String[] names={"he","heat","demo","inc","phosphorus"};
  for(int i=0;i<result.length;i++){
   var source=PanzerschreckBullets.ROCKETS[i];
   result[i]=new BulletConfig("hbm:rocket_ml/"+names[i]).setLife(source.expires).setSelfDamageDelay(source.selfDamageDelay)
    .setVel(source.velocity).setGrav(source.gravity).setOnEntityHit(source.onEntityHit).setOnRicochet(source.onRicochet)
    .setOnUpdate(source.onUpdate).setItem(source.ammo).setDamage(source.damageMult).setOnImpact(source.onImpact);
  }
  return result;
 }
}
