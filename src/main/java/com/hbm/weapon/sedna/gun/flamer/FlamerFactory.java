package com.hbm.weapon.sedna.gun.flamer;

import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.Crosshair;
import com.hbm.weapon.sedna.gun.GunConfig;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.WeaponQuality;
import com.hbm.weapon.sedna.gun.Receiver;
import com.hbm.weapon.sedna.gun.GunAmmo;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import java.util.function.Supplier;

/** Three true source receiver variants, not fluid chemthrower substitutes. */
public final class FlamerFactory {
    private static ItemGunBaseNT build(WeaponQuality quality,Supplier<Float> damage,int capacity,int delay,boolean day,BulletConfig... ammunition){
        var receiver=new Receiver(0).dmg(damage).spreadHipfire(0).delay(delay).auto(true).reload(90).jam(17).mag(new MagazineFullReload(0,capacity).addConfigs(ammunition)).offset(.75,-.0625,-.25).setupStandardFire();
        if(day)receiver.sound("hbm:weapon.fire.blackPowder",1,1);
        return new ItemGunBaseNT(quality,new GunConfig().dura(20_000).draw(10).inspect(17).crosshair(Crosshair.L_CIRCLE).rec(receiver).setupStandardConfiguration()
            .anim(FlamerAnimations.LAMBDA_FLAMER_ANIMS).orchestra(day?FlamerOrchestras.ORCHESTRA_FLAMER_DAYBREAKER:FlamerOrchestras.ORCHESTRA_FLAMER)).setDefaultAmmo(GunAmmo.standard(63),1);
    }
    public static ItemGunBaseNT gun_flamer(){return build(WeaponQuality.A_SIDE,FlamerSettings::GUN_FLAMER_DAMAGE,300,1,false,FlamerBullets.flame_diesel,FlamerBullets.flame_gas,FlamerBullets.flame_napalm,FlamerBullets.flame_balefire);}
    public static ItemGunBaseNT gun_flamer_topaz(){return build(WeaponQuality.B_SIDE,FlamerSettings::GUN_TOPAZ_DAMAGE,500,1,false,FlamerBullets.flame_topaz_diesel,FlamerBullets.flame_topaz_gas,FlamerBullets.flame_topaz_napalm,FlamerBullets.flame_topaz_balefire);}
    public static ItemGunBaseNT gun_flamer_daybreaker(){return build(WeaponQuality.LEGENDARY,FlamerSettings::GUN_DAYBREAKER_DAMAGE,50,10,true,FlamerBullets.flame_daybreaker_diesel,FlamerBullets.flame_daybreaker_gas,FlamerBullets.flame_daybreaker_napalm,FlamerBullets.flame_daybreaker_balefire);}
    private FlamerFactory(){}
}
