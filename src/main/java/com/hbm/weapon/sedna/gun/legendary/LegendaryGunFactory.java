package com.hbm.weapon.sedna.gun.legendary;

import com.hbm.bomb.conventional.grenade.FireStatus;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.WeaponQuality;
import com.hbm.weapon.sedna.gun.factory.Lego;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.weapon.sedna.gun.particle.SpentCasing.CasingType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

/** The actual XFactory44 Lilmac and XFactory12ga Broken/Sexy, with their complete accepted bullet families. */
public final class LegendaryGunFactory {
    private static final SpentCasing BRASS=new SpentCasing(CasingType.STRAIGHT).setColor(SpentCasing.COLOR_CASE_BRASS).setupSmoke(1F,.5,60,20);
    private static BulletConfig bullet(String id,int metadata){return new BulletConfig("hbm:"+id).setItem(GunAmmo.standard(metadata));}
    private static BulletConfig secret(String id,int metadata){return new BulletConfig("hbm:"+id).setItem(GunAmmo.secret(metadata));}
    private static SpentCasing shell(String id,int body,int head){return new SpentCasing(CasingType.SHOTGUN).setColor(body,head).setScale(.75F).register(id);}
    public static final BulletConfig m44_bp=bullet("44/bp",10).setDamage(.75F).setBlackPowder(true).setCasing(BRASS.clone().register("m44bp"));
    public static final BulletConfig m44_sp=bullet("44/sp",11).setCasing(BRASS.clone().register("m44"));
    public static final BulletConfig m44_fmj=bullet("44/fmj",12).setDamage(.8F).setThresholdNegation(3).setArmorPiercing(.1F).setCasing(BRASS.clone().register("m44fmj"));
    public static final BulletConfig m44_jhp=bullet("44/jhp",13).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-.25F).setCasing(BRASS.clone().register("m44jhp"));
    public static final BulletConfig m44_ap=bullet("44/ap",14).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(7.5F).setArmorPiercing(.15F).setCasing(BRASS.clone().setColor(SpentCasing.COLOR_CASE_44).register("m44ap"));
    public static final BulletConfig m44_express=bullet("44/express",15).setDoesPenetrate(true).setDamage(1.5F).setThresholdNegation(3).setArmorPiercing(.1F).setWear(1.5F).setCasing(BRASS.clone().register("m44express"));
    public static final BulletConfig m44_equestrian_pip=secret("44/equestrian_pip",2).setDamage(0).setOnImpact(LegendaryImpacts::boxcar).setCasing(BRASS.clone().setColor(SpentCasing.COLOR_CASE_EQUESTRIAN).register("m44equestrianPip"));
    public static final BulletConfig g12_bp=bullet("12ga/bp",41).setBlackPowder(true).setProjectiles(8).setDamage(.75F/8).setSpread(.035F).setRicochetAngle(15).setCasing(shell("12GA_BP",SpentCasing.COLOR_CASE_BRASS,SpentCasing.COLOR_CASE_BRASS));
    public static final BulletConfig g12_bp_magnum=bullet("12ga/bp_magnum",42).setBlackPowder(true).setProjectiles(4).setDamage(.75F/4).setSpread(.035F).setRicochetAngle(25).setCasing(shell("12GA_BP_MAGNUM",SpentCasing.COLOR_CASE_BRASS,SpentCasing.COLOR_CASE_BRASS));
    public static final BulletConfig g12_bp_slug=bullet("12ga/bp_slug",43).setBlackPowder(true).setDamage(.75F).setSpread(.01F).setRicochetAngle(5).setCasing(shell("12GA_BP_SLUG",SpentCasing.COLOR_CASE_BRASS,SpentCasing.COLOR_CASE_BRASS));
    public static final BulletConfig g12=bullet("12ga/buckshot",44).setProjectiles(8).setDamage(1F/8).setSpread(.035F).setRicochetAngle(15).setThresholdNegation(2).setCasing(shell("12GA",0xB52B2B,SpentCasing.COLOR_CASE_BRASS));
    public static final BulletConfig g12_slug=bullet("12ga/slug",45).setHeadshot(1.5F).setSpread(0).setRicochetAngle(25).setThresholdNegation(4).setArmorPiercing(.15F).setCasing(shell("12GA_SLUG",0x393939,SpentCasing.COLOR_CASE_BRASS));
    public static final BulletConfig g12_flechette=bullet("12ga/flechette",46).setProjectiles(8).setDamage(1F/8).setThresholdNegation(5).setArmorPiercing(.2F).setSpread(.025F).setRicochetAngle(5).setCasing(shell("12GA_FLECHETTE",0x3C80F0,SpentCasing.COLOR_CASE_BRASS));
    public static final BulletConfig g12_magnum=bullet("12ga/magnum",47).setProjectiles(4).setDamage(2F/4).setSpread(.015F).setRicochetAngle(15).setThresholdNegation(4).setCasing(shell("12GA_MAGNUM",0x278400,SpentCasing.COLOR_CASE_12GA));
    public static final BulletConfig g12_explosive=bullet("12ga/explosive",48).setDamage(2.5F).setOnImpact((bullet,hit)->{Lego.standardExplode(bullet,hit,2F);bullet.discard();}).setSpread(0).setRicochetAngle(15).setCasing(shell("12GA_EXPLOSIVE",0xDA4127,SpentCasing.COLOR_CASE_12GA));
    public static final BulletConfig g12_phosphorus=bullet("12ga/phosphorus",49).setProjectiles(8).setDamage(1F/8).setSpread(.015F).setRicochetAngle(15).setCasing(shell("12GA_PHOSPHORUS",0x910001,SpentCasing.COLOR_CASE_12GA)).setOnImpact((bullet,hit)->{if(hit instanceof EntityHitResult entityHit&&entityHit.getEntity() instanceof LivingEntity living){var data=FireStatus.data(living);if(data.phosphorus<300)data.phosphorus=300;}});
    public static final BulletConfig g12_equestrian_bj=secret("12ga/equestrian_bj",3).setDamage(0).setOnImpact(LegendaryImpacts::boat).setCasing(shell("12gaEquestrianBJ",0xB52B2B,SpentCasing.COLOR_CASE_EQUESTRIAN));
    // Source TKR deliberately has zero damage and no impact callback; do not invent a second boat attack.
    public static final BulletConfig g12_equestrian_tkr=secret("12ga/equestrian_tkr",3).setDamage(0).setCasing(shell("12gaEquestrianTKR",0xB52B2B,SpentCasing.COLOR_CASE_EQUESTRIAN));
    public static void initBullets(){}
    public static void initCasingItems(){
        casing(m44_bp,0,12);casing(m44_sp,0,6);casing(m44_fmj,0,6);casing(m44_jhp,0,6);casing(m44_ap,2,6);casing(m44_express,0,6);
        for(var config:new BulletConfig[]{g12_bp,g12_bp_magnum,g12_bp_slug})casing(config,4,12);
        for(var config:new BulletConfig[]{g12,g12_slug,g12_flechette})casing(config,5,6);
        for(var config:new BulletConfig[]{g12_magnum,g12_explosive,g12_phosphorus})casing(config,6,6);
    }
    private static void casing(BulletConfig config,int metadata,int amount){var stack=com.hbm.item.casing.FlatCasingContent.stack(metadata,1);config.setCasing(stack,amount);}
    public static ItemGunBaseNT lilmac(){return new ItemGunBaseNT(WeaponQuality.LEGENDARY,new GunConfig().dura(31000).draw(10).inspect(23).crosshair(Crosshair.L_CLASSIC).scopeTexture(ResourceLocation.parse("hbm:textures/misc/scope_44.png")).smoke(Lego.LAMBDA_STANDARD_SMOKE)
        .rec(new Receiver(0).dmg(LegendaryGunSettings::lilmac).delay(14).reload(46).jam(23).sound("hbm:weapon.44Shoot",1,1).mag(new MagazineFullReload(0,6).addConfigs(m44_equestrian_pip,m44_bp,m44_sp,m44_fmj,m44_jhp,m44_ap,m44_express)).offset(.75,-.0625,-.3125).setupStandardFire().recoil((stack,ctx)->ItemGunBaseNT.setupRecoil(10,(float)(ctx.getPlayer().getRandom().nextGaussian()*1.5))))
        .setupStandardConfiguration().anim(LegendaryGunAnimations.LAMBDA_LILMAC_ANIMS).orchestra(LegendaryGunOrchestras.ORCHESTRA_NOPIP)).pistolDualWield(true).setDefaultAmmo(GunAmmo.standard(13),12);}
    public static ItemGunBaseNT broken(){return new ItemGunBaseNT(WeaponQuality.LEGENDARY,new GunConfig().dura(0).draw(5).inspect(39).reloadSequential(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
        .rec(new Receiver(0).dmg(LegendaryGunSettings::broken).spreadAmmo(1.15F).delay(20).reload(22,10,13,0).jam(24).sound("hbm:weapon.fire.shotgun",1,1).mag(new MagazineSingleReload(0,6).addConfigs(g12_equestrian_tkr,g12_bp,g12_bp_magnum,g12_bp_slug,g12,g12_slug,g12_flechette,g12_magnum,g12_explosive,g12_phosphorus)).offset(.75,-.0625,-.1875).canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(Lego.LAMBDA_NOWEAR_FIRE).recoil((stack,ctx)->ItemGunBaseNT.setupRecoil(10,(float)(ctx.getPlayer().getRandom().nextGaussian()*1.5))))
        .setupStandardConfiguration().anim(LegendaryGunAnimations.LAMBDA_MARESLEG_SHORT_ANIMS).orchestra(LegendaryGunOrchestras.ORCHESTRA_MARESLEG_SHORT)).setDefaultAmmo(GunAmmo.standard(47),24);}
    public static ItemGunBaseNT sexy(){return new ItemGunBaseNT(WeaponQuality.LEGENDARY,new GunConfig().dura(5000).draw(20).inspect(65).reloadSequential(true).inspectCancel(false).crosshair(Crosshair.L_CIRCLE).hideCrosshair(false).smoke(Lego.LAMBDA_STANDARD_SMOKE)
        .rec(new Receiver(0).dmg(LegendaryGunSettings::sexy).delay(4).auto(true).dryfireAfterAuto(true).reload(110).jam(19).sound("hbm:weapon.fire.shotgunAuto",1,1).mag(new MagazineFullReload(0,100).addConfigs(g12_equestrian_bj,g12_bp,g12_bp_magnum,g12_bp_slug,g12,g12_slug,g12_flechette,g12_magnum,g12_explosive,g12_phosphorus)).offset(.75,-.125,-.25).setupStandardFire().recoil((stack,ctx)->ItemGunBaseNT.setupRecoil((float)(ctx.getPlayer().getRandom().nextGaussian()*.5),(float)(ctx.getPlayer().getRandom().nextGaussian()*.5))))
        .setupStandardConfiguration().anim(LegendaryGunAnimations.LAMBDA_SEXY_ANIMS).orchestra(LegendaryGunOrchestras.ORCHESTRA_SHREDDER_SEXY)).setDefaultAmmo(GunAmmo.standard(47),50);}
    private LegendaryGunFactory(){}
}
