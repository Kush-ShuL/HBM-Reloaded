package com.hbm.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** The source IAttackHandler/IDamageHandler armor callbacks, before native armor reduction. */
public interface LegacyArmorDamageHandler {
    default boolean sourceCancelAttack(Player player, ItemStack armor, DamageSource source, float amount) { return false; }
    default float sourceHurt(Player player, ItemStack armor, DamageSource source, float amount) { return amount; }

    static float hurt(LivingEntity living, DamageSource source, float amount) {
        if (living instanceof Player player) {
            for (ItemStack armor : player.getArmorSlots()) {
                if (armor.getItem() instanceof LegacyArmorDamageHandler handler)
                    amount = handler.sourceHurt(player, armor, source, amount);
            }
        }
        return amount;
    }
}
