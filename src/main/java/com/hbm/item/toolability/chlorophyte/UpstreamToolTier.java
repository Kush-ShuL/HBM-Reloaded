package com.hbm.item.toolability.chlorophyte;
import com.hbm.registry.HbmContent;
import net.minecraft.tags.*;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
/** Actual ModItems4081/4104/4138/4162/4185 materials. Volcanic deliberately repairs with bismuth, as the source. */
public enum UpstreamToolTier implements Tier {
 DECORATED_COBALT(3,2500,15,2.5F,75,"ingot_cobalt"),STARMETAL(3,3000,20,2.5F,100,"ingot_starmetal"),
 BISMUTH(4,0,50,0,200,"ingot_bismuth"),VOLCANIC(4,0,50,0,200,"ingot_bismuth"),CHLOROPHYTE(4,0,75,0,200,"powder_chlorophyte");
 public final int sourceHarvestLevel;private final int uses,enchantability;private final float speed,bonus;private final String repair;
 UpstreamToolTier(int level,int uses,float speed,float bonus,int enchantability,String repair){sourceHarvestLevel=level;this.uses=uses;this.speed=speed;this.bonus=bonus;this.enchantability=enchantability;this.repair=repair;}
 @Override public int getUses(){return uses;}
 @Override public float getSpeed(){return speed;}
 @Override public float getAttackDamageBonus(){return bonus;}
 @Override public int getEnchantmentValue(){return enchantability;}
 @Override public TagKey<Block> getIncorrectBlocksForDrops(){return sourceHarvestLevel==3?BlockTags.INCORRECT_FOR_DIAMOND_TOOL:BlockTags.INCORRECT_FOR_NETHERITE_TOOL;}
 @Override public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item(repair));}
}
