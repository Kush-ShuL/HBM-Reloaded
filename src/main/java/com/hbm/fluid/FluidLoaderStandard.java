package com.hbm.fluid;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** The source's return values and name-insensitive stacking order are intentional. */
public final class FluidLoaderStandard implements FluidLoadingHandler {
    private static boolean empty(ItemStack stack) { return stack == null || stack.isEmpty(); }
    @Override public boolean fillItem(ItemStack[] slots, int in, int out, FluidTank tank) {
        if (tank.pressure != 0) return false;
        if (empty(slots[in])) return true;
        var full = FluidContainerRegistry.getFullContainer(slots[in], tank.getTankType());
        int amount = FluidContainerRegistry.getFluidContent(full, tank.getTankType());
        if (!full.isEmpty() && tank.getFill() - amount >= 0) {
            Component name = slots[in].get(DataComponents.CUSTOM_NAME);
            if (empty(slots[out])) {
                tank.setFill(tank.getFill() - amount); slots[out] = full.copy(); slots[in].shrink(1);
                if (slots[in].isEmpty()) slots[in] = ItemStack.EMPTY;
                if (name != null) slots[out].set(DataComponents.CUSTOM_NAME, name);
            } else if (FluidContainerRegistry.sameItem(slots[out], full) && slots[out].getCount() < slots[out].getMaxStackSize()) {
                tank.setFill(tank.getFill() - amount); slots[in].shrink(1);
                if (slots[in].isEmpty()) slots[in] = ItemStack.EMPTY;
                slots[out].grow(1);
                if (name != null) slots[out].set(DataComponents.CUSTOM_NAME, name);
            }
        }
        return false;
    }
    @Override public boolean emptyItem(ItemStack[] slots, int in, int out, FluidTank tank) {
        if (empty(slots[in])) return true;
        int amount = FluidContainerRegistry.getFluidContent(slots[in], tank.getTankType());
        if (amount > 0 && tank.getFill() + amount <= tank.maxFluid) {
            var emptyContainer = FluidContainerRegistry.getEmptyContainer(slots[in]);
            Component name = slots[in].get(DataComponents.CUSTOM_NAME);
            if (empty(slots[out])) {
                tank.setFill(tank.getFill() + amount); slots[out] = emptyContainer;
                if (!emptyContainer.isEmpty() && name != null) slots[out].set(DataComponents.CUSTOM_NAME, name);
                slots[in].shrink(1); if (slots[in].isEmpty()) slots[in] = ItemStack.EMPTY;
            } else if (emptyContainer.isEmpty() || (FluidContainerRegistry.sameItem(slots[out], emptyContainer) && slots[out].getCount() < slots[out].getMaxStackSize())) {
                tank.setFill(tank.getFill() + amount); slots[in].shrink(1);
                if (slots[in].isEmpty()) slots[in] = ItemStack.EMPTY;
                if (!emptyContainer.isEmpty()) { slots[out].grow(1); if (name != null) slots[out].set(DataComponents.CUSTOM_NAME, name); }
            }
            return true;
        }
        return false;
    }
}
