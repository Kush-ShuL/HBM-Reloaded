package com.hbm.weapon.sedna.gun;

import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Original ComparableStack ammo matching: exact item and damage, count optional, unrelated NBT ignored. */
public record GunAmmo(Supplier<? extends Item> item, int count, int metadata) {
    public static GunAmmo standard(int metadata) { return new GunAmmo(() -> com.hbm.weapon.sedna.ammo.FlatAmmoContent.standard(metadata), 1, 0); }
    public static GunAmmo secret(int ordinal) { return new GunAmmo(() -> com.hbm.weapon.sedna.ammo.FlatAmmoContent.secret(ordinal), 1, 0); }
    public ItemStack toStack() { var stack = new ItemStack(item.get(), count); if (!(stack.getItem() instanceof com.hbm.weapon.sedna.ammo.FlatAmmoItem)) stack.set(HbmHazards.LEGACY_METADATA.get(), metadata); return stack; }
    public boolean matchesRecipe(ItemStack stack, boolean ignoreSize) { return !stack.isEmpty() && stack.is(item.get()) && (stack.getItem() instanceof com.hbm.weapon.sedna.ammo.FlatAmmoItem || stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) == metadata) && (ignoreSize || stack.getCount() >= count); }
}
