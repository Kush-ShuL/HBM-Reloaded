package com.hbm.machine.radgen;

import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class RadGenMenu extends AbstractContainerMenu {
    private final Container machine;
    private final BlockPos position;
    public RadGenMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, read(inventory, buffer)); }
    private record ClientInit(Container machine, BlockPos position) {}
    private RadGenMenu(int id, Inventory inventory, ClientInit init) { this(id, inventory, init.machine(), init.position()); }
    private static ClientInit read(Inventory inventory, RegistryFriendlyByteBuf buffer) { var pos = buffer.readBlockPos(); return new ClientInit(inventory.player.level().getBlockEntity(pos) instanceof RadGenEntity machine ? machine : new SimpleContainer(24), pos); }
    public RadGenMenu(int id, Inventory inventory, Container machine, BlockPos position) {
        super(RadGenContent.MENU.get(), id); this.machine = machine; this.position = position;
        for (int i = 0; i < 4; i++) for (int j = 0; j < 3; j++) addSlot(new Slot(machine, j + i * 3, 8 + j * 18, 17 + i * 18));
        for (int i = 0; i < 4; i++) for (int j = 0; j < 3; j++) addSlot(new GenericOutputSlot(inventory.player, machine, j + i * 3 + 12, 116 + j * 18, 17 + i * 18));
        for (int i = 0; i < 3; i++) for (int j = 0; j < 9; j++) addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
        for (int i = 0; i < 9; i++) addSlot(new Slot(inventory, i, 8 + i * 18, 160));
    }
    public BlockPos position() { return position; }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        var slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var result = stack.copy();
        if (index <= 23) { if (!moveItemStackTo(stack, 24, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 0, 12, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return result;
    }
}
