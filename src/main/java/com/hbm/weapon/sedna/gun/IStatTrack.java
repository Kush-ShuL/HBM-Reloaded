package com.hbm.weapon.sedna.gun;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IStatTrack {
    void tickUp(ItemStack stack,LivingEntity target);
}
