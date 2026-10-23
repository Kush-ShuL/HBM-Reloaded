package com.hbm.weapon.legacymelee;

import com.hbm.armor.LegacyArmorDurabilityRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Source non-armor ItemStack.damageItem, using the one existing old Unbreaking RNG implementation. */
public final class LegacySwordWear {
    public static void damage(ItemStack stack,int amount,LivingEntity owner) {
        if(!(owner.level() instanceof ServerLevel level)||owner instanceof Player player&&player.getAbilities().instabuild||!stack.isDamageableItem())return;
        int damage=stack.getDamageValue()+LegacyArmorDurabilityRules.damageAfterUnbreaking(level,stack,amount,owner.getRandom());
        if(damage>stack.getMaxDamage()) {
            owner.onEquippedItemBroken(stack.getItem(),EquipmentSlot.MAINHAND);
            if(owner instanceof net.minecraft.server.level.ServerPlayer player)player.awardStat(net.minecraft.stats.Stats.ITEM_BROKEN.get(stack.getItem()));
            stack.shrink(1);stack.setDamageValue(0);
        } else stack.setDamageValue(damage);
    }
    private LegacySwordWear(){}
}
