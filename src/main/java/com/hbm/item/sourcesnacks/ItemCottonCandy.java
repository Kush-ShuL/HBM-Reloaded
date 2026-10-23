package com.hbm.item.sourcesnacks;

import com.hbm.item.structureloot.StructureFoodItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Actual source ItemCottonCandy, retaining the shared legacy food consumption path. */
public final class ItemCottonCandy extends StructureFoodItem {
    public ItemCottonCandy() { super("cotton_candy", 5, .6F, 64, true); }
    @Override protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        if (level.isClientSide) return;
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 15 * 20, 0));
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 0));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 25 * 20, 2));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 25 * 20, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 4));
    }
}
