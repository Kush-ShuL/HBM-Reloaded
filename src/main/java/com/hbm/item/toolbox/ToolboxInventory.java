package com.hbm.item.toolbox;

import com.hbm.machine.storage.CrateContents;
import com.hbm.machine.storage.CrateStackData;
import com.hbm.registry.HbmSounds;
import java.io.IOException;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Actual InventoryToolBox/ItemInventory, using the existing sole crate stack/overflow/ejection helpers. */
public final class ToolboxInventory implements Container {
    public final Player player;
    public final ItemStack target;
    private final NonNullList<ItemStack> slots = NonNullList.withSize(24, ItemStack.EMPTY);
    public ToolboxInventory(Player player, ItemStack box) {
        this.player = player;
        target = box;
        var from = readRows(box, player.registryAccess());
        if (from != null) for (int i = 0; i < 24; i++) slots.set(i, from[i]);
        else CrateStackData.data(box, new CompoundTag());
    }
    public static ItemStack[] readRows(ItemStack box, HolderLookup.Provider lookup) {
        if (!CrateStackData.hasSourceData(box)) return null;
        var rows = new ItemStack[24];
        java.util.Arrays.fill(rows, ItemStack.EMPTY);
        var list = CrateStackData.data(box).getList("items", Tag.TAG_COMPOUND);
        // Source fixed-count reader examines exactly the first24 list entries and each sparse slot byte.
        for (int i = 0; i < 24; i++) {
            var entry = list.getCompound(i);
            int slot = entry.getByte("slot");
            var item = ItemStack.parseOptional(lookup, entry);
            if (slot >= 0 && slot < 24 && !item.isEmpty()) rows[slot] = item;
        }
        return rows;
    }
    public static void writeRows(ItemStack box, ItemStack[] rows, HolderLookup.Provider lookup) {
        var data = CrateStackData.data(box);
        var list = new ListTag();
        for (int i = 0; i < 24; i++) {
            if (rows[i].isEmpty()) continue;
            var entry = (CompoundTag) rows[i].save(lookup);
            entry.putByte("slot", (byte) i);
            list.add(entry);
        }
        data.put("items", list);
        CrateStackData.data(box, data);
    }
    @Override public int getContainerSize() { return 24; }
    @Override public boolean isEmpty() { return slots.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int index) { return slots.get(index); }
    @Override public ItemStack removeItem(int index, int count) {
        var item = slots.get(index);
        if (item.isEmpty()) return ItemStack.EMPTY;
        if (item.getCount() > count) { var part = item.split(count); setChanged(); return part; }
        setItem(index, ItemStack.EMPTY);
        return item;
    }
    @Override public ItemStack removeItemNoUpdate(int index) { var item = slots.get(index); setItem(index, ItemStack.EMPTY); return item; }
    @Override public void setItem(int index, ItemStack item) { item.limitSize(getMaxStackSize()); slots.set(index, item); setChanged(); }
    @Override public int getMaxStackSize() { return 64; }
    @Override public boolean stillValid(Player viewer) { return true; }
    @Override public boolean canPlaceItem(int index, ItemStack item) { return !item.is(ToolboxContent.TOOLBOX.get()); }
    @Override public void clearContent() { slots.clear(); setChanged(); }
    @Override public void setChanged() {
        writeRows(target, slots.toArray(ItemStack[]::new), player.registryAccess());
        if (player.level().isClientSide) return;
        try {
            if (CrateStackData.compressedSize(target, player.registryAccess()) > 6000) {
                player.displayClientMessage(Component.translatable("message.hbm.storage_crate.overflow").withStyle(ChatFormatting.RED), false);
                CrateContents.ejectHeld(player, this);
                CrateStackData.clearSourceData(target);
                slots.clear();
            }
        } catch (IOException ignored) { }
    }
    public Component displayName() { return target.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME) ? target.getHoverName() : Component.translatable("container.toolBox"); }
    @Override public void startOpen(Player ignored) { sound("block.crateOpen"); }
    @Override public void stopOpen(Player ignored) {
        sound("block.crateClose");
        var data = CrateStackData.data(target);
        data.remove("isOpen");
        data.putInt("rand", player.level().random.nextInt());
        CrateStackData.data(target, data);
        player.inventoryMenu.broadcastChanges();
    }
    private void sound(String sound) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HbmSounds.get(sound), SoundSource.PLAYERS, 1F, .8F);
    }
}
