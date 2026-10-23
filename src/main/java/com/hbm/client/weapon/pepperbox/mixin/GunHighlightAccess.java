package com.hbm.client.weapon.pepperbox.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value=Gui.class,remap=false)
public interface GunHighlightAccess {
    @Accessor("lastToolHighlight") ItemStack hbm$lastHighlightedGun();
    @Accessor("lastToolHighlight") void hbm$lastHighlightedGun(ItemStack stack);
}
