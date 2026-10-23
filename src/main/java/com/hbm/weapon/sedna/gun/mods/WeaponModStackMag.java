package com.hbm.weapon.sedna.gun.mods;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.Receiver;
import com.hbm.weapon.sedna.gun.mags.*;
import net.minecraft.world.item.ItemStack;
/** Source WeaponModStackMag: only full/single reloading magazines grow by integer3/2, retaining actual ammo and rail. */
public final class WeaponModStackMag extends WeaponModBase {
 public WeaponModStackMag(int id){super(id,"MAG");}
 @Override @SuppressWarnings("unchecked") public <T>T eval(T base,ItemStack gun,String key,Object parent){if(key==Receiver.O_MAGAZINE){
  // Original DUMMY_SINGLE/FULL are explicitly temporary/nonpersistent/noncached. Native immutable index/accepted-list fields use an equivalent real instance per evaluation.
  if(base instanceof MagazineSingleReload original)return (T)new MagazineSingleReload(original.index,original.capacity*3/2).addConfigs(original.acceptedBullets.toArray(BulletConfig[]::new));
  if(base instanceof MagazineFullReload original)return (T)new MagazineFullReload(original.index,original.capacity*3/2).addConfigs(original.acceptedBullets.toArray(BulletConfig[]::new));
 }return base;}
 @Override public void onInstall(ItemStack gun,ItemStack mod,int index){XWeaponModManager.changedMagState();}
 @Override public void onUninstall(ItemStack gun,ItemStack mod,int index){XWeaponModManager.changedMagState();}
}
