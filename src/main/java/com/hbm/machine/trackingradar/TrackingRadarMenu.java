package com.hbm.machine.trackingradar;

import com.hbm.item.battery.HbmBattery;
import com.hbm.machine.trackingradar.TrackingRadarContent.RadarEntity;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class TrackingRadarMenu extends AbstractContainerMenu {
    public record Contact(int type, int x, int y, int z) {}

    private final Container inventory;
    private final ContainerData data;
    private final List<Contact> contacts;

    public TrackingRadarMenu(int id, Inventory player, RegistryFriendlyByteBuf buffer) {
        this(id, player, new SimpleContainer(1), new SimpleContainerData(3),
                buffer.readList(in -> new Contact(in.readVarInt(), in.readVarInt(), in.readVarInt(), in.readVarInt())));
        data.set(2, -1);
    }

    public TrackingRadarMenu(int id, Inventory player, RadarEntity radar) {
        this(id, player, radar, new ContainerData() {
            @Override public int get(int index) {
                return index == 2 ? radar.mode() : (int) (radar.getPower() >>> (index * 16)) & 0xFFFF;
            }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 3; }
        }, List.of());
    }

    private TrackingRadarMenu(int id, Inventory player, Container inventory, ContainerData data, List<Contact> contacts) {
        super(TrackingRadarContent.MENU.get(), id);
        this.inventory = inventory;
        this.data = data;
        this.contacts = List.copyOf(contacts);
        addSlot(new Slot(inventory, 0, 232, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof HbmBattery; }
        });
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) addSlot(new Slot(player, 9 + row * 9 + col, 48 + col * 18, 158 + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(player, col, 48 + col * 18, 216));
        addDataSlots(data);
    }

    public long power() { return (data.get(0) & 0xFFFFL) | ((data.get(1) & 0xFFFFL) << 16); }
    public int mode() { return data.get(2); }
    public List<Contact> contacts() { return contacts; }

    @Override public boolean stillValid(Player player) { return inventory.stillValid(player); }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        return inventory instanceof RadarEntity radar && radar.stillValid(player) && radar.toggleMode(button);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == 0) {
            if (!moveItemStackTo(stack, 1, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!slots.getFirst().mayPlace(stack) || !moveItemStackTo(stack, 0, 1, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }
}
