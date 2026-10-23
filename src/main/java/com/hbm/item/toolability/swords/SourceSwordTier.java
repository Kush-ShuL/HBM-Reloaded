package com.hbm.item.toolability.swords;
import com.hbm.registry.HbmContent;
import net.minecraft.tags.*;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
/** Exact MainRegistry111/116/118/119 materials not already exposed by the real CMB/legacy-melee/Upstream/Mese tiers. */
public enum SourceSwordTier implements Tier {
 SCHRABIDIUM(3,10000,50,100,200,"ingot_schrabidium"),DESH(2,0,7.5F,2,10,"ingot_desh");
 public final int sourceHarvestLevel;private final int uses,enchantment;private final float speed,bonus;private final String repair;
 SourceSwordTier(int level,int uses,float speed,float bonus,int enchant,String repair){sourceHarvestLevel=level;this.uses=uses;this.speed=speed;this.bonus=bonus;enchantment=enchant;this.repair=repair;}
 @Override public int getUses(){return uses;}@Override public float getSpeed(){return speed;}@Override public float getAttackDamageBonus(){return bonus;}
 @Override public int getEnchantmentValue(){return enchantment;}@Override public TagKey<Block> getIncorrectBlocksForDrops(){return sourceHarvestLevel>=3?BlockTags.INCORRECT_FOR_DIAMOND_TOOL:BlockTags.INCORRECT_FOR_IRON_TOOL;}
 @Override public Ingredient getRepairIngredient(){return repair==null?Ingredient.EMPTY:Ingredient.of(HbmContent.item(repair));}
}
