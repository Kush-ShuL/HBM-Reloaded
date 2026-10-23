package com.hbm.weapon.sedna.gun.panzerschreck;

import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunAmmo;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import java.util.function.BiConsumer;
import net.minecraft.world.phys.HitResult;

/** Five actual RPZB clones. Fields are assigned from the original immutable template,
 * with distinct modern config keys instead of constructing unrelated launcher configs. */
public final class PanzerschreckBullets {
    private PanzerschreckBullets() {}
    public static final BulletConfig[] ROCKETS={
        rocket("he",PistolAmmo.ROCKET_HE,1F,PanzerschreckImpacts::standard),
        rocket("heat",PistolAmmo.ROCKET_HEAT,.5F,PanzerschreckImpacts::heat),
        rocket("demo",PistolAmmo.ROCKET_DEMO,.75F,PanzerschreckImpacts::demolition),
        rocket("inc",PistolAmmo.ROCKET_INC,.75F,PanzerschreckImpacts::incendiary),
        rocket("phosphorus",PistolAmmo.ROCKET_PHOSPHORUS,.75F,PanzerschreckImpacts::phosphorus)
    };
    private static BulletConfig rocket(String key,GunAmmo ammo,float damage,BiConsumer<EntityBulletBaseMK4,HitResult> impact){
        return new BulletConfig("hbm:rocket_rpzb/"+key).setLife(300).setSelfDamageDelay(10)
            .setVel(0F).setGrav(0D).setOnEntityHit(null).setOnRicochet(null)
            .setOnUpdate(entity->{var bullet=(EntityBulletBaseMK4)entity;if(bullet.accel<7)bullet.accel+=.4D;})
            .setItem(ammo).setDamage(damage).setOnImpact(impact);
    }
}
