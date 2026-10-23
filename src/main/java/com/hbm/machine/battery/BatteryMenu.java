package com.hbm.machine.battery;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Original 176x166 slot coordinates; 16-bit words preserve full long state. */
public final class BatteryMenu extends AbstractContainerMenu {
    private final Container machine;
    private final ContainerData data;

    public BatteryMenu(int id, Inventory inventory) { this(id, inventory, new SimpleContainer(2), new SimpleContainerData(15)); }

    public BatteryMenu(int id, Inventory inventory, Container machine, ContainerData data) {
        super(BatteryMachineContent.MENU.get(), id);
        checkContainerSize(machine, 2);
        checkContainerDataCount(data, 15);
        this.machine = machine;
        this.data = data;
        addSlot(new Slot(machine, 0, 26, 17));
        addSlot(new Slot(machine, 1, 26, 53));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        addDataSlots(data);
    }

    private long readLong(int start) {
        long result = 0;
        for (int i = 0; i < 4; i++) result |= (data.get(start + i) & 0xFFFFL) << (i * 16);
        return result;
    }
    public long power() { return readLong(0); }
    public long capacity() { return Math.max(1, readLong(4)); }
    public long delta() { return readLong(8); }
    public int redLow() { return data.get(12); }
    public int redHigh() { return data.get(13); }
    public int priority() { return Math.max(1, Math.min(3, data.get(14))); }
    public int scaledPower(int height) { return (int) (power() * height / capacity()); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }

    @Override public boolean clickMenuButton(Player player, int button) {
        return machine instanceof BatteryBlockEntity battery && battery.stillValid(player) && battery.cycleButton(button);
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();
        if (index <= 1) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 2, false)) return ItemStack.EMPTY;
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return result;
    }
}
