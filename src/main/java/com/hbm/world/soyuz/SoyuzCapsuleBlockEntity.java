package com.hbm.world.soyuz;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Entire source non-ticking TileEntityInventoryBase(19): all-side extraction, no automatic insertion. */
public final class SoyuzCapsuleBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, Nameable {
    private static final int[] ACCESS = java.util.stream.IntStream.range(0, 19).toArray();
    private final NonNullList<ItemStack> slots = NonNullList.withSize(19, ItemStack.EMPTY);
    private String customName;
    public SoyuzCapsuleBlockEntity(BlockPos pos, BlockState state) { super(SoyuzCapsulesContent.BLOCK_ENTITY.get(), pos, state); }
    @Override public int getContainerSize() { return 19; }
    @Override public boolean isEmpty() { return slots.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int index) { return slots.get(index); }
    @Override public ItemStack removeItem(int index, int amount) { var result = ContainerHelper.removeItem(slots, index, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int index) { return ContainerHelper.takeItem(slots, index); }
    @Override public void setItem(int index, ItemStack stack) { stack.limitSize(64); slots.set(index, stack); setChanged(); }
    @Override public void clearContent() { slots.clear(); setChanged(); }
    @Override public int getMaxStackSize() { return 64; }
    @Override public boolean canPlaceItem(int index, ItemStack stack) { return false; }
    @Override public int[] getSlotsForFace(Direction side) { return ACCESS.clone(); }
    @Override public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction side) { return false; }
    @Override public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side) { return true; }
    @Override public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= 128;
    }
    public void setCustomName(String name) { customName = name; setChanged(); }
    @Override public boolean hasCustomName() { return customName != null && !customName.isEmpty(); }
    @Override public Component getCustomName() { return hasCustomName() ? Component.literal(customName) : null; }
    @Override public Component getName() { return hasCustomName() ? getCustomName() : Component.translatable("container.soyuzCapsule"); }
    @Override public Component getDisplayName() { return getName(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new SoyuzCapsuleMenu(id, inventory, this, worldPosition); }
    /** Native spectator opens must carry the same complete location as an ordinary source interaction. */
    @Override public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) { buffer.writeBlockPos(worldPosition); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        var list = new ListTag();
        for (int i = 0; i < 19; i++) if (!slots.get(i).isEmpty()) {
            var entry = (CompoundTag) slots.get(i).save(lookup); entry.putByte("slot", (byte) i); list.add(entry);
        }
        tag.put("items", list);
        if (customName != null) tag.putString("name", customName);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        // Preserve source partial-load semantics: entries absent from this list do not overwrite another slot.
        for (var value : tag.getList("items", Tag.TAG_COMPOUND)) {
            var entry = (CompoundTag) value; int index = entry.getByte("slot");
            if (index >= 0 && index < 19) slots.set(index, ItemStack.parseOptional(lookup, entry));
        }
        customName = tag.getString("name");
    }
}
