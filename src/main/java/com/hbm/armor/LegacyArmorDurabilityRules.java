package com.hbm.armor;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/** Official 1.7.10 ItemStack.attemptDamageItem + EnchantmentDurability RNG order. */
public final class LegacyArmorDurabilityRules {
    private LegacyArmorDurabilityRules(){}
    public static int damageAfterUnbreaking(ServerLevel level,ItemStack stack,int amount,RandomSource random){
        int unbreaking=EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING),stack);
        int ignored=0;
        if(unbreaking>0)for(int point=0;point<amount;point++){
            // Armor always consumes the point for 60% of nextFloat results.
            // Only the remaining 40% draws nextInt(level+1). No nextFloat is
            // drawn at all for zero Unbreaking, matching add/afr bytecode.
            if(stack.getItem() instanceof ArmorItem&&random.nextFloat()<.6F)continue;
            if(random.nextInt(unbreaking+1)>0)ignored++;
        }
        return amount-ignored;
    }
}
