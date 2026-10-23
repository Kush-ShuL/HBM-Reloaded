package com.hbm.missile.custom.launcher;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import com.hbm.missile.custom.CustomMissileParts.PartSize;

/** The two source menus have the same exact eight unrestricted manual slots and36player positions. */
public final class CustomLauncherMenu extends AbstractContainerMenu {
    public final Container machine;
    public final boolean large;
    private final BlockPos pos;
    public CustomLauncherMenu(int id, Inventory player, RegistryFriendlyByteBuf data, boolean large) { this(id, player, data.readBlockPos(), large); }
    private CustomLauncherMenu(int id, Inventory player, BlockPos pos, boolean large) { this(id, player, player.player.level().getBlockEntity(pos) instanceof CustomLauncherEntity machine ? machine : new SimpleContainer(8), pos, large); }
    public CustomLauncherMenu(int id, Inventory player, CustomLauncherEntity machine, BlockPos pos) { this(id, player, machine, pos, machine.large); }
    private CustomLauncherMenu(int id, Inventory player, Container machine, BlockPos pos, boolean large) {
        super(large ? CustomLauncherContent.TABLE_MENU.get() : CustomLauncherContent.COMPACT_MENU.get(), id); checkContainerSize(machine, 8); this.machine = machine; this.pos = pos; this.large = large;
        int[][] positions = {{26, 36}, {26, 72}, {116, 72}, {134, 72}, {152, 90}, {116, 108}, {116, 90}, {134, 90}};
        for (int slot = 0; slot < 8; slot++) addSlot(new Slot(machine, slot, positions[slot][0], positions[slot][1]) { @Override public boolean mayPlace(ItemStack stack) { return true; } });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(player, 9 + row * 9 + col, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(player, col, 8 + col * 18, 198));
    }
    public BlockPos position() { return pos; }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (!large || !(machine instanceof LaunchTableEntity table) || !table.stillValid(player)
                || button < PartSize.SIZE_10.ordinal() || button > PartSize.SIZE_20.ordinal()) return false;
        table.padSize = PartSize.values()[button]; table.setChanged(); return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = getSlot(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var result = stack.copy();
        if (index <= 7) { if (!moveItemStackTo(stack, 8, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 0, 8, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged(); return result;
    }
}
