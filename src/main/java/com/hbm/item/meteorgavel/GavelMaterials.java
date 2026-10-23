package com.hbm.item.meteorgavel;

import com.hbm.registry.HbmContent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/** Original HBM_STEEL and HBM_MESEGAVEL fields, including the latter's intentionally zero durability. */
final class GavelMaterials {
    static final Tier STEEL = material(750, 8F, 2F, 10, "ingot_steel");
    static final Tier MESE = material(0, 50F, 0F, 200, "plate_paa");
    private static Tier material(int uses, float speed, float damage, int enchantability, String repair) {
        return new Tier() {
            @Override public int getUses() { return uses; }
            @Override public float getSpeed() { return speed; }
            @Override public float getAttackDamageBonus() { return damage; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
            @Override public int getEnchantmentValue() { return enchantability; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(HbmContent.item(repair)); }
        };
    }
    private GavelMaterials() {}
}
