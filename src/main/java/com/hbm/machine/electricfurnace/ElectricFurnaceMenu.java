package com.hbm.machine.electricfurnace;

import com.hbm.item.battery.HbmBattery;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/** Exact legacy slot order and coordinates; vanilla menu packets sync authoritative state. */
public final class ElectricFurnaceMenu extends AbstractContainerMenu {
    private final Container machine;
    private final ContainerData data;

    public ElectricFurnaceMenu(int id, Inventory inventory) { this(id, inventory, new SimpleContainer(4), new SimpleContainerData(7)); }
    public ElectricFurnaceMenu(int id, Inventory inventory, Container machine, ContainerData data) {
        super(ElectricFurnaceContent.MENU.get(), id);
        checkContainerSize(machine, 4); checkContainerDataCount(data, 7);
        this.machine = machine; this.data = data;
        addSlot(new Slot(machine, 0, 152, 54));
        addSlot(new Slot(machine, 1, 20, 35));
        addSlot(new SmeltingSlot(inventory.player, machine, 2, 80, 35));
        addSlot(new Slot(machine, 3, 111, 34) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof FurnaceUpgradeItem; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 162));
        addDataSlots(data);
    }
    public long power() { return (data.get(0) & 0xFFFFL) | (data.get(1) & 0xFFFFL) << 16; }
    public int progress() { return data.get(2); }
    public int maxProgress() { return data.get(3) > 0 ? data.get(3) : 100; }
    public int consumption() { return data.get(4) > 0 ? data.get(4) : 50; }
    public boolean active() { return data.get(5) != 0; }
    public int scaledProgress(int width) { return progress() * width / maxProgress(); }
    public int scaledPower(int height) { return (int) (power() * height / ElectricFurnaceBlockEntity.CAPACITY); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();
        if (index <= 3) {
            if (!moveItemStackTo(stack, 4, slots.size(), true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, result);
        } else if (stack.getItem() instanceof HbmBattery) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof FurnaceUpgradeItem) {
            if (!moveItemStackTo(stack, 3, 4, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack);
        return result;
    }

    public static final class SmeltingSlot extends Slot {
        private final Player player;
        private int itemCount;
        public SmeltingSlot(Player player, Container inventory, int slot, int x, int y) { super(inventory, slot, x, y); this.player = player; }
        @Override public boolean mayPlace(ItemStack stack) { return false; }
        @Override public ItemStack remove(int amount) { if (hasItem()) itemCount += Math.min(amount, getItem().getCount()); return super.remove(amount); }
        @Override protected void onQuickCraft(ItemStack stack, int amount) { itemCount += amount; checkCrafted(stack); }
        @Override public void onTake(Player player, ItemStack stack) { checkCrafted(stack); super.onTake(player, stack); }
        private void checkCrafted(ItemStack stack) {
            if (itemCount <= 0) return; // A quick-move has already settled its count in onQuickCraft.
            stack.onCraftedBy(player.level(), player, itemCount);
            if (player.level() instanceof ServerLevel server) {
                float experience = server.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING).stream()
                        .filter(recipe -> ElectricFurnaceBlockEntity.sameLegacyItem(recipe.value().getResultItem(server.registryAccess()), stack))
                        .map(recipe -> recipe.value().getExperience()).findFirst().orElse(0F);
                int amount = itemCount;
                if (experience == 0F) amount = 0;
                else if (experience < 1F) {
                    float value = itemCount * experience;
                    amount = (int) Math.floor(value);
                    if (amount < Math.ceil(value) && (float) Math.random() < value - amount) amount++;
                }
                if (amount > 0) ExperienceOrb.award(server, new Vec3(player.getX(), player.getY() + 0.5, player.getZ()), amount);
            }
            itemCount = 0;
            EventHooks.firePlayerSmeltedEvent(player, stack);
        }
    }
}
