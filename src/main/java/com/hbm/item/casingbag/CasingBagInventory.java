package com.hbm.item.casingbag;

import com.hbm.machine.storage.CrateStackData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Original fifteen-slot ItemStackUtil items/slot payload, using the same public native stack codecs as ammo bags. */
public final class CasingBagInventory implements Container {
    public final ItemStack box;
    private final HolderLookup.Provider lookup;
    private final NonNullList<ItemStack> slots=NonNullList.withSize(15,ItemStack.EMPTY);
    public CasingBagInventory(ItemStack box,HolderLookup.Provider lookup) {
        this.box=box;this.lookup=lookup;
        if(!box.has(DataComponents.CUSTOM_DATA)) box.set(DataComponents.CUSTOM_DATA,CustomData.EMPTY);
        var list=CrateStackData.data(box).getList("items",Tag.TAG_COMPOUND);
        // Source readStacksFromNBT(box,15) reads precisely the first15 LIST entries, resolving their sparse slot bytes.
        for(int i=0;i<15;i++) {
            var tag=list.getCompound(i);int slot=tag.getByte("slot");
            var item=ItemStack.parseOptional(lookup,tag);
            if(slot>=0 && slot<15 && !item.isEmpty()) slots.set(slot,item);
        }
    }
    @Override public int getContainerSize() { return 15; }
    @Override public boolean isEmpty() { return slots.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slots.get(slot); }
    @Override public ItemStack removeItem(int slot,int amount) {
        var item=slots.get(slot);
        if(item.isEmpty()) return ItemStack.EMPTY;
        if(item.getCount()>amount) { var part=item.split(amount);setChanged();return part; }
        setItem(slot,ItemStack.EMPTY);return item;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { var item=slots.get(slot);setItem(slot,ItemStack.EMPTY);return item; }
    @Override public void setItem(int slot,ItemStack stack) { stack.limitSize(getMaxStackSize());slots.set(slot,stack);setChanged(); }
    @Override public int getMaxStackSize() { return 64; }
    @Override public boolean stillValid(Player player) { return true; }
    @Override public boolean canPlaceItem(int slot,ItemStack stack) { return false; }
    @Override public void clearContent() { slots.clear();setChanged(); }
    @Override public void setChanged() {
        // Preserve all float recovery keys and other original bag data on EVERY inventory write.
        var tag=CrateStackData.data(box);var items=new ListTag();
        for(int i=0;i<15;i++) {
            var item=slots.get(i);
            if(item.isEmpty()) { slots.set(i,ItemStack.EMPTY);continue; }
            var child=(CompoundTag)item.save(lookup);child.putByte("slot",(byte)i);items.add(child);
        }
        tag.put("items",items);CrateStackData.data(box,tag);
    }
    public Component displayName() { return box.has(DataComponents.CUSTOM_NAME)?box.getHoverName():Component.translatable("container.casingBag"); }
}
