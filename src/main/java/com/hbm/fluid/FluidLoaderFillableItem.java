package com.hbm.fluid;

import net.minecraft.world.item.ItemStack;

/** Native IFillableItem semantics, including original armor-mod recursion. */
public final class FluidLoaderFillableItem implements FluidLoadingHandler {
    @Override public boolean fillItem(ItemStack[] slots, int in, int out, FluidTank tank) { return fill(slots[in], tank); }
    @Override public boolean emptyItem(ItemStack[] slots, int in, int out, FluidTank tank) { return empty(slots[in], tank); }
    public boolean fill(ItemStack stack, FluidTank tank) {
        if (tank.pressure != 0 || stack == null || stack.isEmpty()) return false;
        armorMods(stack,tank,true);
        if (!(stack.getItem() instanceof FillableItem fillable)) return false;
        if (fillable.acceptsFluid(tank.type, stack)) tank.setFill(fillable.tryFill(tank.type, tank.getFill(), stack));
        return true;
    }
    private void armorMods(ItemStack armor, FluidTank tank, boolean filling) {
        if (!(armor.getItem() instanceof net.minecraft.world.item.ArmorItem) || !com.hbm.armor.ArmorModStorage.hasMods(armor)) return;
        for (var mod : com.hbm.armor.ArmorModStorage.pryMods(armor,null)) {
            if (!(mod.getItem() instanceof FillableItem)) continue;
            if (filling) fill(mod,tank); else empty(mod,tank);
            // Native component parsing returns a stack copy; persist the actual modified fuel component.
            com.hbm.armor.ArmorModStorage.applyMod(armor,mod,null);
        }
    }
    public boolean empty(ItemStack stack, FluidTank tank) {
        if (stack == null || stack.isEmpty()) return false;
        armorMods(stack,tank,false);
        if (!(stack.getItem() instanceof FillableItem fillable)) return false;
        if (fillable.providesFluid(tank.type, stack)) tank.setFill(tank.getFill() + fillable.tryEmpty(tank.type, tank.getMaxFill() - tank.getFill(), stack));
        return tank.getFill() == tank.getMaxFill();
    }
}
