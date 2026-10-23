package com.hbm.machine.crystallizer;
import net.minecraft.world.item.Item;
/** Actual ItemMachineUpgrade(EFFECT,tier), non-mutex summed per source machine caps. */
public final class EffectUpgradeItem extends Item {
 private final int tier;public EffectUpgradeItem(int tier){super(new Properties().stacksTo(1));this.tier=tier;}public int tier(){return tier;}
}
