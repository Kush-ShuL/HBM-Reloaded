package com.hbm.weapon.sedna.gun.fliegerfaust;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.factory.Lego;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** X5863's baseRocket clone: constant speed, no acceleration callback, tiny impact explosion. */
public final class FliegerfaustBullets {
    public static final BulletConfig ROCKET = new BulletConfig("hbm:rocket_ff")
            .setLife(300).setSelfDamageDelay(10).setVel(7F).setGrav(0D)
            .setOnEntityHit(null).setOnRicochet(null).setOnUpdate(null)
            .setItem(PistolAmmo.ROCKET_MICRO).setOnImpact(FliegerfaustBullets::impact);

    private FliegerfaustBullets() { }

    private static void impact(EntityBulletBaseMK4 bullet, HitResult hit) {
        if (hit instanceof EntityHitResult entityHit && bullet.tickCount < 3 && entityHit.getEntity() == bullet.getThrower()) return;
        Lego.tinyExplode(bullet, hit, 1.5F);
        bullet.setDead();
    }
}
