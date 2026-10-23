package com.hbm.world.soyuz;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original 3x6 cargo slots plus recovery slot18, plain manual Slot and all19-slot quick transfer. */
public final class SoyuzCapsuleMenu extends AbstractContainerMenu {
    private final Container capsule;
    private final BlockPos pos;
    private record Init(Container inventory, BlockPos pos) { }
    public SoyuzCapsuleMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, resolve(inventory, buffer)); }
    private SoyuzCapsuleMenu(int id, Inventory inventory, Init init) { this(id, inventory, init.inventory(), init.pos()); }
    private static Init resolve(Inventory inventory, RegistryFriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        return new Init(inventory.player.level().getBlockEntity(pos) instanceof SoyuzCapsuleBlockEntity capsule ? capsule : new SimpleContainer(19), pos);
    }
    public SoyuzCapsuleMenu(int id, Inventory inventory, Container capsule, BlockPos pos) {
        super(SoyuzCapsulesContent.MENU.get(), id); this.capsule = capsule; this.pos = pos;
        for (int i = 0; i < 3; i++) for (int j = 0; j < 6; j++) addSlot(new Slot(capsule, j + i * 6, 26 + j * 18 + 18 * 2, 18 + i * 18));
        addSlot(new Slot(capsule, 18, 17, 36));
        for (int i = 0; i < 3; i++) for (int j = 0; j < 9; j++) addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
        for (int i = 0; i < 9; i++) addSlot(new Slot(inventory, i, 8 + i * 18, 162));
    }
    public BlockPos position() { return pos; }
    @Override public boolean stillValid(Player player) { return capsule.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        var slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var item = slot.getItem(); var copy = item.copy();
        if (index < 19) { if (!moveItemStackTo(item, 19, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(item, 0, 19, false)) return ItemStack.EMPTY;
        if (item.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }
}
