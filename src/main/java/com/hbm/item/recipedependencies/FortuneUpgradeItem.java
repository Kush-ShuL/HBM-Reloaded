package com.hbm.item.recipedependencies;
import net.minecraft.world.item.Item;
/** Actual ItemMachineUpgrade(FORTUNE,tier): one item, no independent ticking or invented passive luck. */
public final class FortuneUpgradeItem extends Item {
 private final int tier;public FortuneUpgradeItem(int tier){super(new Properties().stacksTo(1));if(tier<1||tier>3)throw new IllegalArgumentException("Original fortune upgrade tier "+tier);this.tier=tier;}
 public int tier(){return tier;}
}
