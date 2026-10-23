package com.hbm.weapon.sedna.gun.quadro;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.panzerschreck.PanzerschreckBullets;
/** Source makeQD shallow clone: all RPZB fields are unchanged except life400 and update callback. */
public final class QuadroBullets {
    public static final BulletConfig[] ROCKETS=initialize();
    private QuadroBullets(){}
    private static BulletConfig[] initialize(){
        var result=new BulletConfig[5];String[] names={"he","heat","demo","inc","phosphorus"};
        for(int i=0;i<result.length;i++){
            var source=PanzerschreckBullets.ROCKETS[i];
            result[i]=new BulletConfig("hbm:rocket_qd/"+names[i]).setLife(400).setSelfDamageDelay(source.selfDamageDelay)
                .setVel(source.velocity).setGrav(source.gravity).setOnEntityHit(source.onEntityHit).setOnRicochet(source.onRicochet)
                .setOnUpdate(QuadroGuidance::update).setItem(source.ammo).setDamage(source.damageMult).setOnImpact(source.onImpact);
        }
        return result;
    }
}
