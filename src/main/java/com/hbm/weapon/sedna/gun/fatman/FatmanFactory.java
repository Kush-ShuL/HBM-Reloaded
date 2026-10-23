package com.hbm.weapon.sedna.gun.fatman;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.WeaponQuality;
import com.hbm.weapon.sedna.gun.mags.MagazineSingleReload;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
/** Actual single-receiver XFactoryCatapult declaration; no external pistol-dual permission. */
public final class FatmanFactory {
 public static ItemGunBaseNT create(){return new ItemGunBaseNT(WeaponQuality.A_SIDE,new GunConfig().dura(300).draw(20).inspect(30).reloadChangeType(true).crosshair(Crosshair.L_CIRCUMFLEX).hideCrosshair(false).rec(new Receiver(0).dmg(FatmanSettings.DAMAGE).spreadHipfire(0F).delay(10).reload(57).jam(40).sound("hbm:weapon.fire.fatman",1F,1F).mag(new MagazineSingleReload(0,1).addConfigs(FatmanBullets.ALL)).offset(1,-.0625*1.5,-.1875D).offsetScoped(1,-.0625*1.5,-.125D).setupStandardFire().recoil((stack,ctx)->{})).setupStandardConfiguration().anim(FatmanAnimations.ANIMATIONS).orchestra((stack,ctx)->{if(ctx.entity.level().isClientSide)return;if(ItemGunBaseNT.getLastAnim(stack,ctx.configIndex)==GunAnimation.RELOAD&&ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex)==0)GunSounds.play(ctx.entity,"hbm:weapon.reload.fatmanFull",1F,1F);})).setDefaultAmmoExpensive(PistolAmmo.NUKE_STANDARD,1);}
 private FatmanFactory(){}
}
