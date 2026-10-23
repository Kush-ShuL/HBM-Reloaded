package com.hbm.weapon.crucible;
import net.minecraft.tags.*;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
/** ModItems3592 actual CRUCIBLE ToolMaterial; old sword harvest rules remain in the shared ItemSwordAbility. */
public enum CrucibleTier implements Tier {
 INSTANCE;
 public static final int SOURCE_HARVEST_LEVEL=10;
 @Override public int getUses(){return 3;}
 @Override public float getSpeed(){return 50F;}
 @Override public float getAttackDamageBonus(){return 100F;}
 @Override public int getEnchantmentValue(){return 0;}
 @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
 @Override public Ingredient getRepairIngredient(){return Ingredient.EMPTY;}
}
