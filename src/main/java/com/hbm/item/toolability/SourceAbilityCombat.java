package com.hbm.item.toolability;

import net.minecraft.world.item.ItemStack;

/** Actual old ItemTool/ItemSword/ItemHoe attack timing; this marker adds no preset or weapon modifier. */
public interface SourceAbilityCombat {
    default boolean canOperate(ItemStack stack){return true;}
    /** Null preserves native rarity; only actual source overrides opt in. */
    default net.minecraft.world.item.Rarity sourceRarityOverride(){return null;}
}
