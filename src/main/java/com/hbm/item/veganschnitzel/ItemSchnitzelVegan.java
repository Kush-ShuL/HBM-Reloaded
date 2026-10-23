package com.hbm.item.veganschnitzel;

import com.hbm.item.structureloot.StructureFoodItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Real source food, including its seconds/ticks fire-duration quirk. */
public final class ItemSchnitzelVegan extends StructureFoodItem {
    public ItemSchnitzelVegan() { super("schnitzel_vegan", 0, .6F); }

    @Override
    protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        if (level.isClientSide) return;
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 10 * 20, 0));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30 * 20, 0));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 3 * 60 * 20, 4));
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 3 * 20, 0));
        // Original setFire(5 * 20) accepts seconds, not ticks: retain 100s.
        player.igniteForSeconds(5 * 20);
        var motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, 2, motion.z);
    }
}
