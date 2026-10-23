package com.hbm.machine.storagedrum;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Exact source six-by-six cropped diamond,24 plain Slot objects and original player-slot order. */
public final class StorageDrumMenu extends AbstractContainerMenu {
    private final Container drum;
    private final BlockPos pos;
    public StorageDrumMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, resolve(inventory, buffer)); }
    private record ClientInit(Container inventory, BlockPos pos) {}
    private StorageDrumMenu(int id, Inventory inventory, ClientInit init) { this(id, inventory, init.inventory(), init.pos()); }
    private static ClientInit resolve(Inventory inventory, RegistryFriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        Container container = inventory.player.level().getBlockEntity(pos) instanceof StorageDrumEntity drum ? drum
            : new SimpleContainer(24) { @Override public int getMaxStackSize() { return 1; } };
        return new ClientInit(container, pos);
    }
    public StorageDrumMenu(int id, Inventory inventory, Container drum, BlockPos pos) {
        super(StorageDrumContent.MENU.get(), id);
        this.drum = drum;
        this.pos = pos;
        int index = 0;
        for (int j = 0; j < 6; j++) for (int i = 0; i < 6; i++) {
            if (i + j > 1 && i + j < 9 && 5 - i + j > 1 && i + 5 - j > 1) {
                // Plain source Slot allows manual insertion, unlike ISidedInventory automation filters.
                addSlot(new Slot(drum, index++, 35 + i * 18, 24 + j * 18));
            }
        }
        for (int i = 0; i < 3; i++) for (int j = 0; j < 9; j++) addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 152 + i * 18));
        for (int i = 0; i < 9; i++) addSlot(new Slot(inventory, i, 8 + i * 18, 210));
    }
    public BlockPos position() { return pos; }
    @Override public boolean stillValid(Player player) { return drum.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var original = slot.getItem();
        var result = original.copy();
        if (index < 24) {
            if (!moveItemStackTo(original, 24, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(original, 0, 24, false)) return ItemStack.EMPTY;
        if (original.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, original);
        return result;
    }
}
