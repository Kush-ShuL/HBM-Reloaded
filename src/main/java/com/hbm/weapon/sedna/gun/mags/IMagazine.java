package com.hbm.weapon.sedna.gun.mags;

import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IMagazine<T> {
    T getType(ItemStack stack, Container inventory);
    void setType(ItemStack stack, T type);
    int getCapacity(ItemStack stack);
    int getAmount(ItemStack stack, Container inventory);
    void setAmount(ItemStack stack, int amount);
    void useUpAmmo(ItemStack stack, Container inventory, int amount);
    boolean canReload(ItemStack stack, Container inventory);
    void initNewType(ItemStack stack, Container inventory);
    void reloadAction(ItemStack stack, Container inventory);
    ItemStack getIconForHUD(ItemStack stack, Player player);
    String reportAmmoStateForHUD(ItemStack stack, Player player);
    SpentCasing getCasing(ItemStack stack, Container inventory);
    void setAmountBeforeReload(ItemStack stack, int amount);
    int getAmountBeforeReload(ItemStack stack);
    void setAmountAfterReload(ItemStack stack, int amount);
    int getAmountAfterReload(ItemStack stack);
    static void handleAmmoBag(Container inventory, BulletConfig config, int shotsFired) {
        if (config.casingItem == null || config.casingAmount <= 0 || !(inventory instanceof GunInventory inv)) return;
        for (var stack : inv.player.getInventory().items) if (stack.getItem() instanceof CasingRecovery bag && bag.pushCasing(stack, config.casingItem, 1F / config.casingAmount * .5F * shotsFired, inv.player.level().registryAccess())) return;
    }
}
