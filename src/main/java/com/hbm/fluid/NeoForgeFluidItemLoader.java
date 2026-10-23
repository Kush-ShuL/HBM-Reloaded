package com.hbm.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

/** Real item-capability adapter; native fluid mappings must be explicit. */
public final class NeoForgeFluidItemLoader implements FluidLoadingHandler {
    public static void register(HbmFluidType nativeType, Fluid forgeType) { com.hbm.fluid.interop.FluidInteropMappings.register(nativeType, forgeType); }
    @Override public boolean fillItem(ItemStack[] slots, int in, int out, FluidTank tank) {
        if (!FluidInteropConfig.enabled()) return false;
        if (tank.pressure != 0 || tank.fluid <= 0 || slots[in] == null || slots[in].isEmpty()) return false;
        if (FluidContainerRegistry.getContainer(tank.type, slots[in]) != null) return false;
        Fluid mapped = com.hbm.fluid.interop.FluidInteropMappings.toNeo(tank.type);
        if (mapped == null) return false;
        ItemStack copy = slots[in].copyWithCount(1);
        var handler = copy.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null) return false;
        int filled = handler.fill(new net.neoforged.neoforge.fluids.FluidStack(mapped, tank.fluid), FluidAction.EXECUTE);
        ItemStack returned = handler.getContainer();
        if (filled <= 0 || !canReturn(slots, in, out, returned)) return true;
        returnItem(slots, in, out, returned);
        tank.fluid -= filled;
        return true;
    }
    @Override public boolean emptyItem(ItemStack[] slots, int in, int out, FluidTank tank) {
        if (!FluidInteropConfig.enabled()) return false;
        if (tank.pressure != 0 || tank.fluid >= tank.maxFluid || slots[in] == null || slots[in].isEmpty()) return false;
        if (FluidContainerRegistry.getFluidContent(slots[in], tank.type) > 0) return false;
        Fluid mapped = com.hbm.fluid.interop.FluidInteropMappings.toNeo(tank.type);
        if (mapped == null) return false;
        ItemStack copy = slots[in].copyWithCount(1);
        var handler = copy.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null) return false;
        var drained = handler.drain(new net.neoforged.neoforge.fluids.FluidStack(mapped, tank.maxFluid - tank.fluid), FluidAction.EXECUTE);
        ItemStack returned = handler.getContainer();
        if (drained.isEmpty() || com.hbm.fluid.interop.FluidInteropMappings.fromNeo(drained) != tank.type || !canReturn(slots, in, out, returned)) return false;
        returnItem(slots, in, out, returned);
        tank.fluid += drained.getAmount();
        return tank.fluid == tank.maxFluid;
    }
    private static boolean canReturn(ItemStack[] slots, int in, int out, ItemStack returned) {
        if (in == out) return slots[in].getCount() == 1;
        return returned.isEmpty() || slots[out] == null || slots[out].isEmpty()
                || (ItemStack.isSameItemSameComponents(slots[out], returned) && slots[out].getCount() + returned.getCount() <= slots[out].getMaxStackSize());
    }
    private static void returnItem(ItemStack[] slots, int in, int out, ItemStack returned) {
        if (in == out) { slots[in] = returned; return; }
        slots[in].shrink(1);
        if (slots[in].isEmpty()) slots[in] = ItemStack.EMPTY;
        if (returned.isEmpty()) return;
        if (slots[out] == null || slots[out].isEmpty()) slots[out] = returned;
        else slots[out].grow(returned.getCount());
    }
}
