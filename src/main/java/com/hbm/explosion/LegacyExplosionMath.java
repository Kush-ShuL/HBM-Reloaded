package com.hbm.explosion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Native adapters for vanilla 1.7.10 primitives used by HBM's explosion implementations. */
public final class LegacyExplosionMath {
    private LegacyExplosionMath(){}
    public static int metadata(BlockState state){
        for(var property:state.getProperties())if(property.getName().equals("legacy_meta")||property.getName().equals("metadata")||property.getName().equals("level")){
            var value=state.getValue(property);if(value instanceof Integer number)return number;
        }
        return 0;
    }
    public static double dampenBlast(Level world,Entity entity,double knockback){
        if(!(entity instanceof LivingEntity living))return knockback;
        var enchantment=world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BLAST_PROTECTION);
        int highest=0;for(var armor:living.getArmorSlots())highest=Math.max(highest,EnchantmentHelper.getItemEnchantmentLevel(enchantment,armor));
        if(!(living instanceof Player))highest=Math.max(highest,EnchantmentHelper.getItemEnchantmentLevel(enchantment,living.getItemBySlot(EquipmentSlot.MAINHAND)));
        return highest>0?knockback-net.minecraft.util.Mth.floor(knockback*(double)(highest*.15F)):knockback;
    }
    public static double axisDistance(double value,double min,double max){return min<=value&&max>=value?0:Math.min(Math.abs(min-value),Math.abs(max-value));}
}
