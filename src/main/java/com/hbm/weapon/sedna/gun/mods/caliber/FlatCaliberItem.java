package com.hbm.weapon.sedna.gun.mods.caliber;

import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;

/** One physical source caliber kit. Actual gun-dependent handlers remain in XWeaponModManager. */
public final class FlatCaliberItem extends Item implements FlatSourceItem {
    FlatCaliberItem(){super(new Properties().stacksTo(1));}
}
