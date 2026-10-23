package com.hbm.armor.t51;
import com.hbm.armor.*;
/** Actual three source ItemModBattery definitions; same existing slot8 persistence/menu application. */
public final class ItemModBattery extends ArmorModItem {
 public final double multiplier;
 public ItemModBattery(double multiplier){super(ArmorModStorage.BATTERY,true,true,true,true);this.multiplier=multiplier;}
}
