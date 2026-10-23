package com.hbm.item.plasticbag;

import com.hbm.machine.storage.CrateStackData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Original InventoryPlasticBag, using the existing ItemStackUtil items/slot payload and native stack codec. */
public final class PlasticBagInventory implements Container {
    public final ItemStack bag;
    private final HolderLookup.Provider lookup;
    private ItemStack slot = ItemStack.EMPTY;
    public PlasticBagInventory(ItemStack bag, HolderLookup.Provider lookup) {
        this.bag = bag;
        this.lookup = lookup;
        if (!bag.has(DataComponents.CUSTOM_DATA)) bag.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        // Original readStacksFromNBT(bag,1) examines exactly the first list entry, not the whole list.
        var entry = CrateStackData.data(bag).getList("items", Tag.TAG_COMPOUND).getCompound(0);
        if (entry.getByte("slot") == 0) slot = ItemStack.parseOptional(lookup, entry);
    }
    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return slot.isEmpty(); }
    @Override public ItemStack getItem(int index) { return index == 0 ? slot : ItemStack.EMPTY; }
    @Override public ItemStack removeItem(int index, int count) {
        if (index != 0 || slot.isEmpty()) return ItemStack.EMPTY;
        if (slot.getCount() > count) { var part = slot.split(count); setChanged(); return part; }
        var item = slot;
        setItem(0, ItemStack.EMPTY);
        return item;
    }
    @Override public ItemStack removeItemNoUpdate(int index) {
        var item = getItem(index);
        setItem(index, ItemStack.EMPTY);
        return item;
    }
    @Override public void setItem(int index, ItemStack item) {
        if (index != 0) return;
        item.limitSize(getMaxStackSize());
        slot = item;
        setChanged();
    }
    @Override public int getMaxStackSize() { return 1; }
    @Override public boolean stillValid(Player player) { return true; }
    @Override public boolean canPlaceItem(int index, ItemStack item) { return true; }
    @Override public void clearContent() { slot = ItemStack.EMPTY; setChanged(); }
    @Override public void setChanged() {
        var data = CrateStackData.data(bag);
        var list = new ListTag();
        if (slot.isEmpty()) slot = ItemStack.EMPTY;
        else { var entry = (CompoundTag) slot.save(lookup); entry.putByte("slot", (byte) 0); list.add(entry); }
        data.put("items", list);
        CrateStackData.data(bag, data);
    }
    // Source has no ItemInventory superclass: no6kB overflow ejection, open/close sounds or new content ban.
}
