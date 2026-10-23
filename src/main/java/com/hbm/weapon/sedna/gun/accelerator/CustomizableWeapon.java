package com.hbm.weapon.sedna.gun.accelerator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
/** Actual source ICustomizable entry point used by /ntmcustomize. */
public interface CustomizableWeapon {
    void customize(ServerPlayer player,ItemStack stack,String... arguments);
}
