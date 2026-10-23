package com.hbm.item.meteorbook;

import java.util.Comparator;
import java.util.stream.IntStream;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.event.EventHooks;

/** ContainerBook's actual result+four temporary slots, exact coordinates, shift-click ranges and close drops. */
public final class BlackBookMenu extends AbstractContainerMenu {
    private final Inventory playerInventory;
    private boolean consuming;
    private RecipeHolder<MagicBookRecipe> matched;
    public final TransientCraftingContainer input = new TransientCraftingContainer(this, 2, 2);
    private final ResultContainer output = new ResultContainer();
    public BlackBookMenu(int id, Inventory inventory) {
        super(MeteorBookContent.MENU.get(), id);
        playerInventory = inventory;
        addSlot(new Slot(output, 0, 124, 35) {
            private int crafted;
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public ItemStack remove(int count) { crafted += Math.min(count, getItem().getCount()); return super.remove(count); }
            @Override protected void onQuickCraft(ItemStack stack, int amount) { crafted += amount; }
            @Override protected void onSwapCraft(int amount) { crafted += amount; }
            @Override public void onTake(Player viewer, ItemStack stack) {
                stack.onCraftedBy(viewer.level(), viewer, crafted);
                EventHooks.firePlayerCraftingEvent(viewer, stack, input);
                crafted = 0;
                consume(viewer);
                super.onTake(viewer, stack);
            }
        });
        for (int row = 0; row < 2; row++) for (int column = 0; column < 2; column++) addSlot(new Slot(input, column + row * 2, 30 + column * 36, 17 + row * 36));
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        slotsChanged(input);
    }
    private MagicBookInput recipeInput() { return new MagicBookInput(IntStream.range(0, 4).mapToObj(input::getItem).toList()); }
    @Override public void slotsChanged(Container changed) {
        if (consuming || playerInventory == null) return;
        var level = playerInventory.player.level();
        matched = level.getRecipeManager().getAllRecipesFor(MeteorBookContent.TYPE.get()).stream().sorted(Comparator.comparingInt(holder -> holder.value().order())).filter(holder -> holder.value().matches(recipeInput(), level)).findFirst().orElse(null);
        output.setItem(0, matched == null ? ItemStack.EMPTY : matched.value().assemble(recipeInput(), level.registryAccess()));
        broadcastChanges();
    }
    private void consume(Player player) {
        if (matched == null || !matched.value().matches(recipeInput(), player.level())) return;
        consuming = true;
        try {
            for (int i = 0; i < 4; i++) {
                ItemStack current = input.getItem(i);
                if (current.isEmpty()) continue;
                ItemStack remainder = current.getCraftingRemainingItem();
                input.removeItem(i, 1);
                if (!remainder.isEmpty()) {
                    ItemStack remainingInput = input.getItem(i);
                    if (remainingInput.isEmpty()) input.setItem(i, remainder);
                    else if (!player.getInventory().add(remainder)) player.drop(remainder, false);
                }
            }
        } finally { consuming = false; }
        slotsChanged(input);
    }
    @Override public boolean stillValid(Player player) { return player.getInventory().contains(new ItemStack(MeteorBookContent.BOOK.get())); }
    @Override public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) { return slot.container != output && super.canTakeItemForPickAll(stack, slot); }
    @Override public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) for (int i = 0; i < 4; i++) { ItemStack stack = input.removeItemNoUpdate(i); if (!stack.isEmpty()) player.drop(stack, false); }
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), copy = stack.copy();
        if (index == 0) { if (!moveItemStackTo(stack, 5, 41, true)) return ItemStack.EMPTY; slot.onQuickCraft(stack, copy); }
        else if (index >= 5 && index < 32) { if (!moveItemStackTo(stack, 32, 41, false)) return ItemStack.EMPTY; }
        else if (index >= 32 && index < 41) { if (!moveItemStackTo(stack, 5, 32, false)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 5, 41, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, index == 0 ? copy : stack);
        return copy;
    }
}
