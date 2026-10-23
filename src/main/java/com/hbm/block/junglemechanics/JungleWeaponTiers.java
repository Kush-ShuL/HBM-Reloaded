package com.hbm.block.junglemechanics;
import com.hbm.registry.HbmContent;
import net.minecraft.tags.*;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
/** Actual source MainRegistry117/120/311 materials; cobalt has no repair assignment, CMB uses combine-steel ingot. */
public enum JungleWeaponTiers implements Tier {
 COBALT(750,9,2.5F,60),CMB(8500,40,55,100);
 private final int uses,enchant;private final float speed,damage;JungleWeaponTiers(int uses,float speed,float damage,int enchant){this.uses=uses;this.speed=speed;this.damage=damage;this.enchant=enchant;}
 @Override public int getUses(){return uses;}@Override public float getSpeed(){return speed;}@Override public float getAttackDamageBonus(){return damage;}@Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}@Override public int getEnchantmentValue(){return enchant;}@Override public Ingredient getRepairIngredient(){return this==CMB?Ingredient.of(HbmContent.item("ingot_combine_steel")):Ingredient.EMPTY;}
}
