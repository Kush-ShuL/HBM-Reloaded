package com.hbm.item.plasticbag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Actual source one-slot positions and open-container item lock; no false extract-only restriction. */
public final class PlasticBagMenu extends AbstractContainerMenu {
    public final PlasticBagInventory bag;
    private final Inventory inventory;
    private final InteractionHand hand;
    public PlasticBagMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, buffer.readEnum(InteractionHand.class)); }
    public PlasticBagMenu(int id, Inventory inventory, InteractionHand hand) {
        super(PlasticBagContent.MENU.get(), id);
        this.inventory = inventory;
        this.hand = hand;
        bag = new PlasticBagInventory(inventory.player.getItemInHand(hand), inventory.player.registryAccess());
        bag.startOpen(inventory.player);
        addSlot(new Slot(bag, 0, 80, 65));
        for (int y = 0; y < 3; y++) for (int x = 0; x < 9; x++) addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 134 + y * 18));
        for (int x = 0; x < 9; x++) addSlot(new Slot(inventory, x, 8 + x * 18, 192));
    }
    @Override public boolean stillValid(Player player) { return bag.stillValid(player) && player.getItemInHand(hand) == bag.bag; }
    @Override public void clicked(int index, int button, ClickType type, Player player) {
        if (type == ClickType.SWAP && (hand == InteractionHand.MAIN_HAND ? button == inventory.selected : button == 40)) return;
        if (hand == InteractionHand.MAIN_HAND && index == inventory.selected + 28) return;
        super.clicked(index, button, type, player);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var item = slot.getItem();
        var original = item.copy();
        if (index == 0) { if (!moveItemStackTo(item, 1, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(item, 0, 1, false)) return ItemStack.EMPTY;
        if (item.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, item);
        return original;
    }
    @Override public void removed(Player player) { super.removed(player); bag.stopOpen(player); }
}
