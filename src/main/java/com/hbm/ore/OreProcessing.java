package com.hbm.ore;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/** Processing-only yield arithmetic; intermediate materials are never boosted twice. */
public final class OreProcessing {
    private OreProcessing() {}

    public static double rate(ItemStack input) {
        if (RawOreContent.isRaw(input)) return OreProcessingConfig.RAW_YIELD_PERCENT.get() / 100D;
        if (RawOreContent.isManagedOre(input)) return OreProcessingConfig.BLOCK_YIELD_PERCENT.get() / 100D;
        return 1D;
    }

    /** The consumed ore quantities weight mixed raw/block recipes; other reagents do not. */
    public static double rate(ItemStack[] inputs, int[] consumed) {
        double total = 0D;
        long count = 0L;
        for (int i = 0; i < Math.min(inputs.length, consumed.length); i++) {
            if (consumed[i] <= 0 || !RawOreContent.isRaw(inputs[i]) && !RawOreContent.isManagedOre(inputs[i])) continue;
            total += rate(inputs[i]) * consumed[i];
            count += consumed[i];
        }
        return count == 0L ? 1D : total / count;
    }

    /** Capacity checks reserve the greatest possible result and never draw random numbers. */
    public static int maximumCount(int base, double rate) {
        return (int) Math.ceil(scaled(base, rate));
    }

    private static double scaled(int base, double rate) {
        double exact = Math.min(Integer.MAX_VALUE, base * rate);
        double integer = Math.rint(exact);
        // E.g. 100 * 110% must reserve110, never111 due to a binary round-off.
        return Math.abs(exact - integer) <= Math.ulp(exact) * 4D ? integer : exact;
    }

    /** Fractional items are paid probabilistically, once at committed completion. */
    public static int count(int base, double rate, RandomSource random) {
        double exact = scaled(base, rate);
        int whole = (int) exact;
        return whole + (exact > whole && random.nextDouble() < exact - whole ? 1 : 0);
    }

    public static ItemStack maximumOutput(ItemStack result, double rate) {
        return result.isEmpty() ? ItemStack.EMPTY : result.copyWithCount(maximumCount(result.getCount(), rate));
    }

    public static ItemStack output(ItemStack result, double rate, RandomSource random) {
        return result.isEmpty() ? ItemStack.EMPTY : result.copyWithCount(count(result.getCount(), rate, random));
    }
}
