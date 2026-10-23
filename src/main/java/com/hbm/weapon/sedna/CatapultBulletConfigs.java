package com.hbm.weapon.sedna;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;

/** Original XFactoryCatapult.cluster_submunition; other catapult ammo is a separate group. */
public final class CatapultBulletConfigs {
    public static final BulletConfig CLUSTER_SUBMUNITION=new BulletConfig("hbm:cluster_submunition").setLife(1200).setGrav(.025F).setOnImpact((bullet,impact)->{
        var p=impact.getLocation();
        new ExplosionVNT(bullet.level(),p.x,p.y,p.z,7.5F,bullet.getThrower())
                .setBlockAllocator(new BlockAllocatorStandard()).setBlockProcessor(new BlockProcessorStandard())
                .setEntityProcessor(new EntityProcessorCrossSmooth(1,bullet.damage)).setPlayerProcessor(new PlayerProcessorStandard())
                .setSFX(new ExplosionEffectWeapon(10,2.5F,1)).explode();
        bullet.setDead();
    });
    private CatapultBulletConfigs(){}
    public static void init(){}
}
