package com.hbm.weapon.sedna.gun.accelerator;
import com.hbm.weapon.sedna.gun.mods.WeaponModBase;
import net.minecraft.world.item.ItemStack;
/** Source passive modifier: its real ID is consumed by the NI4NI coin-limit check. */
public final class WeaponModNickel extends WeaponModBase {
    public WeaponModNickel(int id,String slot){super(id,slot);setPriority(PRIORITY_SET);}
    @Override public <T>T eval(T base,ItemStack stack,String key,Object parent){return base;}
}
