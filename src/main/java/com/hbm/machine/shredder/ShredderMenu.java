package com.hbm.machine.shredder;

import com.hbm.item.battery.HbmBattery;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Original slot order and all 66 exact coordinates, with modern authoritative menu sync. */
public final class ShredderMenu extends AbstractContainerMenu {
    private final Container machine;
    private final ContainerData data;
    public ShredderMenu(int id, Inventory player) { this(id, player, new SimpleContainer(30), new SimpleContainerData(3)); }
    public ShredderMenu(int id, Inventory player, Container machine, ContainerData data) {
        super(ShredderContent.MENU.get(), id); this.machine = machine; this.data = data;
        checkContainerSize(machine, 30); checkContainerDataCount(data, 3);
        for (int row = 0; row < 3; row++) for (int col = 0; col < 3; col++) addSlot(new Slot(machine, row * 3 + col, 44 + col * 18, 18 + row * 18));
        for (int row = 0; row < 6; row++) for (int col = 0; col < 3; col++) addSlot(new OutputSlot(player.player, machine, 9 + row * 3 + col, 116 + col * 18, 18 + row * 18));
        addSlot(new Slot(machine, 27, 44, 108)); addSlot(new Slot(machine, 28, 80, 108)); addSlot(new Slot(machine, 29, 8, 108));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(player, 9 + row * 9 + col, 8 + col * 18, 151 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(player, col, 8 + col * 18, 209));
        addDataSlots(data);
    }
    public long power() { return (int) ((data.get(0) & 0xFFFF) | (data.get(1) & 0xFFFF) << 16); }
    public int progress() { return data.get(2); }
    public int gearLeft() { return ShredderBlockEntity.gear(machine.getItem(27)); }
    public int gearRight() { return ShredderBlockEntity.gear(machine.getItem(28)); }
    public int scaledPower(int height) { return (int) (power() * height / ShredderBlockEntity.CAPACITY); }
    public int scaledProgress(int width) { return progress() * width / ShredderBlockEntity.PROCESSING_SPEED; }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index <= 29) { if (!moveItemStackTo(stack, 30, slots.size(), true)) return ItemStack.EMPTY; }
        else if (stack.getItem() instanceof HbmBattery) { if (!moveItemStackTo(stack, 29, 30, false)) return ItemStack.EMPTY; }
        else if (stack.getItem() instanceof ShredderBladeItem) { if (!moveItemStackTo(stack, 27, 29, false)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 0, 9, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        // Legacy transferStackInSlot does not invoke output onPickupFromSlot on shift-click.
        return original;
    }
    private static final class OutputSlot extends Slot {
        private final Player player; private int crafted;
        OutputSlot(Player player, Container container, int slot, int x, int y) { super(container, slot, x, y); this.player = player; }
        @Override public boolean mayPlace(ItemStack stack) { return false; }
        @Override public ItemStack remove(int count) { if (hasItem()) crafted += Math.min(count, getItem().getCount()); return super.remove(count); }
        @Override protected void onQuickCraft(ItemStack stack, int count) { crafted += count; craft(stack); }
        @Override public void onTake(Player player, ItemStack stack) { craft(stack); super.onTake(player, stack); }
        private void craft(ItemStack stack) { stack.onCraftedBy(player.level(), player, crafted); ShredderRecipes.outputTaken(player, stack); crafted = 0; }
    }
}
