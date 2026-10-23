package com.hbm.item.chainsaw;

import com.hbm.registry.HbmContent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/** MainRegistry113/308: CHAINSAW tier3,1500,50,22,0; the real fueled subclass overrides raw durability to1. */
public enum ChainsawTier implements Tier {
    INSTANCE;
    @Override public int getUses() { return 1500; }
    @Override public float getSpeed() { return 50F; }
    @Override public float getAttackDamageBonus() { return 22F; }
    @Override public int getEnchantmentValue() { return 0; }
    @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(HbmContent.item("ingot_steel")); }
}
