package com.hbm.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Source crafting tools stay in their grid cell and directly increment damage; Forge breaks only above maxDamage. */
public class CraftingDegradationItem extends Item {
    protected final int sourceDurability;
    protected CraftingDegradationItem(int durability){this(durability,new Properties());}
    protected CraftingDegradationItem(int durability,Properties properties){super(properties(durability,properties));sourceDurability=durability;}
    private static Properties properties(int durability,Properties properties){properties.stacksTo(1).setNoRepair();return durability>0?properties.durability(durability):properties;}
    @Override public boolean hasCraftingRemainingItem(ItemStack stack){return true;}
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack){var remainder=stack.copy();if(sourceDurability>0){remainder.setDamageValue(remainder.getDamageValue()+1);if(remainder.getDamageValue()>sourceDurability)return ItemStack.EMPTY;}return remainder;}
}
