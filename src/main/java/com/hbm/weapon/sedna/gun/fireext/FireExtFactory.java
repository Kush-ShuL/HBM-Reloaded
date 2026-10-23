package com.hbm.weapon.sedna.gun.fireext;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import net.minecraft.world.item.ItemStack;
/** Original utility extinguisher; source has no animation buses, casings, HE storage, item GUI or mod bindings. */
public final class FireExtFactory {
 public static ItemGunBaseNT create(){return new ItemGunBaseNT(WeaponQuality.UTILITY,new GunConfig()
  .dura(5000).draw(10).inspect(55).reloadChangeType(true).hideCrosshair(false).crosshair(Crosshair.L_CIRCLE)
  .rec(new Receiver(0).dmg(FireExtSettings::damage).delay(1).dry(0).auto(true).spread(0F).spreadHipfire(0F).reload(20).jam(0)
   .sound("hbm:weapon.extinguisher",1F,1F).mag(new MagazineFullReload(0,300).addConfigs(FireExtBullets.WATER,FireExtBullets.FOAM,FireExtBullets.SAND))
   .offset(1,-.0625*2.5,-.25).setupStandardFire())
  .setupStandardConfiguration().orchestra((stack,context)->{
   if(context.entity.level().isClientSide)return;
   if(ItemGunBaseNT.getLastAnim(stack,context.configIndex)==GunAnimation.RELOAD&&ItemGunBaseNT.getAnimTimer(stack,context.configIndex)==0)GunSounds.play(context.entity,"hbm:weapon.reload.pressureValve",1F,1F);
  })){
   /** Old ItemRenderer refreshes an equal item/damage stack without lowering it for NBT-only sync. */
   @Override public boolean shouldCauseReequipAnimation(ItemStack previous,ItemStack current,boolean slotChanged){
    // Its equal-item/damage fallback also superseded a changed slot; actual item/damage changes still draw normally.
    return previous.getItem()!=current.getItem()||previous.getDamageValue()!=current.getDamageValue();
   }
  };}
 private FireExtFactory(){}
}
