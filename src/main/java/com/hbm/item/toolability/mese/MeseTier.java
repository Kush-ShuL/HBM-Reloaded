package com.hbm.item.toolability.mese;
import com.hbm.registry.HbmContent;
import net.minecraft.tags.*;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
/** ModItems4205 actual HBM_MESE, including source zero maximum durability rather than a fake rechargeable tool. */
public enum MeseTier implements Tier {
 INSTANCE;
 public static final int SOURCE_HARVEST_LEVEL=4;
 @Override public int getUses(){return 0;}
 @Override public float getSpeed(){return 100F;}
 @Override public float getAttackDamageBonus(){return 0;}
 @Override public int getEnchantmentValue(){return 200;}
 @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;}
 @Override public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item("plate_paa"));}
}
