package com.hbm.weapon.legacymelee;

import com.hbm.registry.HbmContent;
import net.minecraft.tags.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/** Only the actual ToolMaterial fields used by this original ItemSword family. */
public final class LegacyMeleeMaterials {
    public static final Tier HAMMER = material(3,0,50,999999996F,200,"block_schrabidium"),
            SLEDGE = material(1,0,25,26,200,null), ELECTRIC = material(3,0,30,12,2,null),
            ALLOY = material(3,2000,15,5,5,null), TITANIUM = material(3,1000,9,2.5F,15,"ingot_titanium");
    private static Tier material(int harvest,int uses,float speed,float damage,int enchant,String repair) {
        return new Tier() {
            @Override public int getUses(){return uses;}
            @Override public float getSpeed(){return speed;}
            @Override public float getAttackDamageBonus(){return damage;}
            @Override public TagKey<Block> getIncorrectBlocksForDrops(){return harvest==1?BlockTags.INCORRECT_FOR_STONE_TOOL:BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
            @Override public int getEnchantmentValue(){return enchant;}
            @Override public Ingredient getRepairIngredient(){return repair==null?Ingredient.EMPTY:Ingredient.of(HbmContent.item(repair));}
        };
    }
    private LegacyMeleeMaterials(){}
}
