package com.hbm.item.toolbox;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Original ContainerToolBox 8x3 cells and SlotNonRetarded semantics. */
public final class ToolboxMenu extends AbstractContainerMenu {
    public final ToolboxInventory box;
    private final Inventory inventory;
    private final InteractionHand hand;
    public ToolboxMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, buffer.readEnum(InteractionHand.class)); }
    public ToolboxMenu(int id, Inventory inventory, InteractionHand hand) {
        super(ToolboxContent.MENU.get(), id);
        this.inventory = inventory;
        this.hand = hand;
        box = new ToolboxInventory(inventory.player, inventory.player.getItemInHand(hand));
        box.startOpen(inventory.player);
        for (int y = 0; y < 3; y++) for (int x = 0; x < 8; x++) addSlot(new Slot(box, x + y * 8, 17 + x * 18, 49 + y * 18) {
            @Override public boolean mayPlace(ItemStack item) { return box.canPlaceItem(getContainerSlot(), item); }
            @Override public int getMaxStackSize() { return Math.max(box.getMaxStackSize(), hasItem() ? getItem().getCount() : 1); }
        });
        for (int y = 0; y < 3; y++) for (int x = 0; x < 9; x++) addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 129 + y * 18));
        for (int x = 0; x < 9; x++) addSlot(new Slot(inventory, x, 8 + x * 18, 187));
    }
    @Override public boolean stillValid(Player player) { return box.stillValid(player) && player.getItemInHand(hand) == box.target; }
    @Override public void clicked(int index, int button, ClickType type, Player player) {
        if (type == ClickType.SWAP && (hand == InteractionHand.MAIN_HAND ? button == inventory.selected : button == 40)) return;
        if (hand == InteractionHand.MAIN_HAND && index == inventory.selected + 51) return;
        super.clicked(index, button, type, player);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var item = slot.getItem(); var original = item.copy();
        if (index < 24) { if (!moveItemStackTo(item, 24, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(item, 0, 24, false)) return ItemStack.EMPTY;
        if (item.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, item);
        return original;
    }
    @Override public void removed(Player player) { super.removed(player); box.stopOpen(player); }
}
