package com.hbm.item.toolability;

import com.hbm.registry.HbmContent;
import net.minecraft.tags.*;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/** MainRegistry114 and310: exact HBM_STEEL values and real ingot_steel repair, not a vanilla iron tier. */
public enum SteelTier implements Tier {
    INSTANCE;
    @Override public int getUses(){return 750;}
    @Override public float getSpeed(){return 8;}
    @Override public float getAttackDamageBonus(){return 2;}
    @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
    @Override public int getEnchantmentValue(){return 10;}
    @Override public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item("ingot_steel"));}
}
