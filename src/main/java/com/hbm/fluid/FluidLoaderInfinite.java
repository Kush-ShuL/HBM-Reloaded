package com.hbm.fluid;

import com.hbm.item.fluid.InfiniteFluidItem;
import java.util.Random;
import net.minecraft.world.item.ItemStack;

public final class FluidLoaderInfinite implements FluidLoadingHandler {
    private static final Random RANDOM = new Random();
    @Override public boolean fillItem(ItemStack[] slots, int in, int out, FluidTank tank) {
        if (slots[in] == null || slots[in].isEmpty() || !(slots[in].getItem() instanceof InfiniteFluidItem item)) return false;
        if (!item.allowPressure(tank.pressure) || (item.getType() != null && tank.type != item.getType())) return false;
        if (item.getChance() <= 1 || RANDOM.nextInt(item.getChance()) == 0) tank.setFill(Math.max(tank.getFill() - item.getAmount(), 0));
        return true;
    }
    @Override public boolean emptyItem(ItemStack[] slots, int in, int out, FluidTank tank) {
        if (slots[in] == null || slots[in].isEmpty() || !(slots[in].getItem() instanceof InfiniteFluidItem item) || tank.type == HbmFluids.NONE) return false;
        if (item.getType() != null && tank.type != item.getType()) return false;
        if (item.getChance() <= 1 || RANDOM.nextInt(item.getChance()) == 0) tank.setFill(Math.min(tank.getFill() + item.getAmount(), tank.getMaxFill()));
        return true;
    }
}
