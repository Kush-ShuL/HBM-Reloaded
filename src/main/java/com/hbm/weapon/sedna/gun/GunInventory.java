package com.hbm.weapon.sedna.gun;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Original InventoryPlayer ordering:36 main slots then4 armor; a modern offhand is not an extra old reload source. */
public final class GunInventory implements Container {
    public final Player player;
    public GunInventory(Player player) { this.player = player; }
    @Override public int getContainerSize() { return 40; }
    @Override public boolean isEmpty() { for (int i = 0; i < 40; i++) if (!getItem(i).isEmpty()) return false; return true; }
    @Override public ItemStack getItem(int slot) { return player.getInventory().getItem(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return player.getInventory().removeItem(slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return player.getInventory().removeItemNoUpdate(slot); }
    @Override public void setItem(int slot, ItemStack stack) { player.getInventory().setItem(slot, stack); }
    @Override public void setChanged() { player.getInventory().setChanged(); }
    @Override public boolean stillValid(Player player) { return player == this.player; }
    @Override public void clearContent() { for (int i = 0; i < 40; i++) setItem(i, ItemStack.EMPTY); }
}
