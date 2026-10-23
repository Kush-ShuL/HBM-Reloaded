package com.hbm.item.sourcedrinks.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Source public EntityPlayer.itemInUseCount is the modern LivingEntity field; BDCL is its only caller. */
@Mixin(LivingEntity.class)
public interface SourceDrinkUseAccess {
    @Accessor("useItemRemaining") void hbm$sourceDrinkRemaining(int remaining);
}
