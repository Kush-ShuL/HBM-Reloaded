package com.hbm.weapon.sedna.gun.energy.mods;
import com.hbm.weapon.sedna.gun.Receiver;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import com.hbm.weapon.sedna.gun.mods.WeaponModBase;
import net.minecraft.world.item.ItemStack;
/** Original UNDERBARREL modifier: +5% damage and integer capacity*3/2 with the same real ammo and magazine index. */
public final class WeaponModLasCapacitor extends WeaponModBase {
    public WeaponModLasCapacitor(int id){super(id,"UNDERBARREL");setPriority(PRIORITY_MULTIPLICATIVE);}
    @Override public <T>T eval(T base,ItemStack gun,String key,Object parent){
        if(key==Receiver.F_BASEDAMAGE)return cast((Float)base*1.05F,base);
        if(key==Receiver.O_MAGAZINE){var original=(MagazineFullReload)base;var adjusted=new MagazineFullReload(original.index,original.capacity*3/2);adjusted.acceptedBullets.addAll(original.acceptedBullets);return cast(adjusted,base);}
        return base;
    }
}
