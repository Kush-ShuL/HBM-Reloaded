package com.hbm.item.toolability.titanium;

import com.hbm.registry.HbmContent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/** Actual MainRegistry.tMatTitan material and its source ingot_titanium repair assignment. */
public enum TitaniumToolTier implements Tier {
    INSTANCE;
    @Override public int getUses(){return 1000;}
    @Override public float getSpeed(){return 9;}
    @Override public float getAttackDamageBonus(){return 2.5F;}
    @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
    @Override public int getEnchantmentValue(){return 15;}
    @Override public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item("ingot_titanium"));}
}
