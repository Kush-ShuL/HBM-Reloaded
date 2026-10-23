package com.hbm.item.lemegeton;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.event.EventHooks;

/** ContainerLemegeton: result0/input1, original36 player slots, drop input on close; no parallel bag or global crafting recipe. */
public final class LemegetonMenu extends AbstractContainerMenu {
    private final Inventory inventory;
    private boolean consuming;
    private RecipeHolder<LemegetonRecipe> matched;
    public final TransientCraftingContainer input = new TransientCraftingContainer(this, 1, 1);
    private final ResultContainer output = new ResultContainer();
    public LemegetonMenu(int id, Inventory inventory) {
        super(LemegetonContent.MENU.get(), id); this.inventory = inventory;
        addSlot(new Slot(output, 0, 107, 35) {
            private int crafted;
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public ItemStack remove(int count) { crafted += Math.min(count, getItem().getCount()); return super.remove(count); }
            @Override protected void onQuickCraft(ItemStack stack, int amount) { crafted += amount; }
            @Override protected void onSwapCraft(int amount) { crafted += amount; }
            @Override public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player.level(), player, crafted); EventHooks.firePlayerCraftingEvent(player, stack, input); crafted = 0;
                consume(player); super.onTake(player, stack);
            }
        });
        addSlot(new Slot(input, 0, 49, 35));
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        slotsChanged(input);
    }
    private LemegetonInput recipeInput() { return new LemegetonInput(input.getItem(0)); }
    @Override public void slotsChanged(Container changed) {
        if (consuming || inventory == null) return;
        matched = LemegetonConversions.find(inventory.player.level(), recipeInput());
        output.setItem(0, matched == null ? ItemStack.EMPTY : matched.value().assemble(recipeInput(), inventory.player.level().registryAccess()));
        broadcastChanges();
    }
    private void consume(Player player) {
        if (matched == null || !matched.value().matches(recipeInput(), player.level())) return;
        consuming = true;
        try {
            var current = input.getItem(0); var remainder = current.getCraftingRemainingItem(); input.removeItem(0, 1);
            // Original SlotCrafting's default container-leaves-grid path tries player inventory before the matrix/drop fallback.
            if (!remainder.isEmpty() && !player.getInventory().add(remainder)) { if (input.getItem(0).isEmpty()) input.setItem(0, remainder); else player.drop(remainder, false); }
        } finally { consuming = false; }
        slotsChanged(input);
    }
    @Override public boolean stillValid(Player player) { return player.getInventory().contains(new ItemStack(LemegetonContent.BOOK.get())); }
    @Override public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) { return slot.container != output && super.canTakeItemForPickAll(stack, slot); }
    @Override public void removed(Player player) { super.removed(player); if (!player.level().isClientSide) { var stack = input.removeItemNoUpdate(0); if (!stack.isEmpty()) player.drop(stack, false); } }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = getSlot(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var copy = stack.copy();
        if (index <= 1) { if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY; slot.onQuickCraft(stack, copy); }
        else if (!moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, index == 0 ? copy : stack); return copy;
    }
}
