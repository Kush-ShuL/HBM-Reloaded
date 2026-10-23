package com.hbm.machine.missileassembly;

import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original 6+36 slots; manual/shift input is unrestricted, the machine validates only when constructing. */
public final class MissileAssemblyMenu extends AbstractContainerMenu {
    public final Container machine;
    private final BlockPos pos;
    public MissileAssemblyMenu(int id, Inventory player, RegistryFriendlyByteBuf data) { this(id, player, data.readBlockPos()); }
    private MissileAssemblyMenu(int id, Inventory player, BlockPos pos) { this(id, player, player.player.level().getBlockEntity(pos) instanceof MissileAssemblyBlockEntity be ? be : new SimpleContainer(6), pos); }
    public MissileAssemblyMenu(int id, Inventory player, Container machine, BlockPos pos) {
        super(MissileAssemblyContent.MENU.get(), id); checkContainerSize(machine, 6); this.machine = machine; this.pos = pos;
        for (int slot = 0; slot < 5; slot++) addSlot(new Slot(machine, slot, 8 + slot * 18, 36) { @Override public boolean mayPlace(ItemStack stack) { return true; } });
        addSlot(new GenericOutputSlot(player.player, machine, 5, 152, 36));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(player, 9 + row * 9 + col, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(player, col, 8 + col * 18, 198));
    }
    public BlockPos position() { return pos; }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (button != 0 || !(machine instanceof MissileAssemblyBlockEntity be) || !be.stillValid(player)) return false;
        be.construct(); return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = getSlot(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var copy = stack.copy();
        if (index <= 5) { if (!moveItemStackTo(stack, 6, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 0, 5, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }
}
