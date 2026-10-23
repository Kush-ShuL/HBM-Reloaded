package com.hbm.weapon.sedna.gun.utility;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
/** XFactoryTool's actual utility receiver. This long utility gun is not a dual-wield pistol. */
public final class ChargeThrowerFactory {
 public static ItemGunBaseNT create(){return new ItemGunChargeThrower(WeaponQuality.UTILITY,new GunConfig()
  .dura(3000).draw(10).inspect(55).reloadChangeType(true).hideCrosshair(false).crosshair(Crosshair.L_CIRCUMFLEX)
  .rec(new Receiver(0).dmg(ChargeThrowerSettings::damage).delay(4).dry(10).auto(true).spread(0F).spreadHipfire(0F).reload(60).jam(0)
   .sound("hbm:weapon.fire.grenade",1F,1F)
   .mag(new MagazineFullReload(0,1).addConfigs(ChargeThrowerBullets.HOOK,ChargeThrowerBullets.MORTAR,ChargeThrowerBullets.HEAVY_MORTAR))
   .offset(1,-.0625*2.5,-.25).setupStandardFire()
   .recoil((stack,context)->ItemGunBaseNT.setupRecoil(10,(float)(context.getPlayer().getRandom().nextGaussian()*1.5))))
  .setupStandardConfiguration()
  // Source tether reads SECONDARY in inventoryTick; keep this native binding routed to that held-key state.
  .ps((stack,context)->{}).anim(ChargeThrowerAnimations.ANIMATIONS).orchestra((stack,context)->{
   var entity=context.entity;if(entity.level().isClientSide)return;
   var type=ItemGunBaseNT.getLastAnim(stack,context.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,context.configIndex);
   if(type==GunAnimation.CYCLE_DRY&&timer==0&&entity.level().getEntity(ItemGunChargeThrower.getLastHook(stack))==null)
    GunSounds.play(entity,"hbm:weapon.reload.dryFireClick",1F,.75F);
   if(type==GunAnimation.RELOAD){if(timer==30)GunSounds.play(entity,"hbm:weapon.reload.insertRocket",1F,1F);if(timer==40)GunSounds.play(entity,"hbm:weapon.reload.boltClose",1F,1F);}
  })).setDefaultAmmo(PistolAmmo.CT_MORTAR,3);}
 private ChargeThrowerFactory(){}
}
