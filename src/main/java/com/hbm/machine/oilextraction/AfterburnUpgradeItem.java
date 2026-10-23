package com.hbm.machine.oilextraction;
import net.minecraft.world.item.Item;
/** Genuine non-mutex source AFTERBURN upgrade; original gas-to-HE logic lives in the actual drill. */
public final class AfterburnUpgradeItem extends Item {private final int tier;public AfterburnUpgradeItem(int tier){super(new Properties().stacksTo(1));this.tier=tier;}public int tier(){return tier;}}
